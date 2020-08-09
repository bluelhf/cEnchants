package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.utilities.EnchantUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class FlamingAuraEnchantment extends CEnchantment implements Listener {

    public FlamingAuraEnchantment(String key) {
        super(key);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent ev) {
        if (!(ev.getEntity() instanceof Player)) return;
        if (cEnchants.getMetadata(ev.getEntity(), "flaming_aura_cooldown").map(MetadataValue::asLong).orElse(0L) > System.currentTimeMillis()) return;
        Player player = (Player) ev.getEntity();

        if (!(ev.getEntity() instanceof InventoryHolder)) return;
        ItemStack item;
        if ((item = EnchantUtil.getEnchantment((Player)ev.getEntity(), this)) == null) return;

        int level = item.getEnchantmentLevel(this);
        int delay = Math.max(5 / level, 2);
        int[] count = new int[]{0};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (++count[0] > Math.floor(20 / (double)delay) * (level/(double)getMaxLevel())*3) {
                    this.cancel();
                    return;
                }
                double max = Math.PI * 2;
                Location source = player.getLocation();
                source.add(0, 0.5, 0);
                for (double theta = 0; theta < max; theta += max / (16D*level/2D)) {
                        double x = source.getX() + level/2D * Math.sin(theta);
                        double z = source.getZ() + level/2D * Math.cos(theta);
                        Location loc = new Location(source.getWorld(), x, source.getY(), z);
                        Vector srctoLoc = loc.toVector().subtract(source.toVector());
                        source.getWorld().spawnParticle(Particle.FLAME, source, 0, srctoLoc.getX(), 0, srctoLoc.getZ(), 0.1);
                }

                source.getWorld().playSound(source, Sound.BLOCK_CAMPFIRE_CRACKLE, 2, 2);
                source.getWorld().playSound(source, Sound.ENTITY_BOAT_PADDLE_LAND, 2, 0);
                source.getWorld().getNearbyEntities(source, level / 2D, level / 2D, level / 2D).stream()
                        .filter(e -> e.getLocation().distanceSquared(source) < level * level)
                        .filter(e -> e != ev.getEntity()).forEach(e -> {
                    if (e instanceof LivingEntity) {
                        LivingEntity v = (LivingEntity) e;
                        v.damage(2, ev.getEntity());
                        v.setFireTicks(v.getFireTicks() + level * 20);

                        new BukkitRunnable() {@Override public void run() {
                            Vector victimToV = v.getLocation().toVector().subtract(ev.getEntity().getLocation().toVector());
                            victimToV.normalize();
                            victimToV.add(new Vector(0, 0.3, 0));
                            victimToV.multiply(0.05 * level);

                            v.setVelocity(v.getVelocity().add(victimToV));
                        }}.runTaskLater(cEnchants.get(), 1);
                    }
                });
            }
        }.runTaskTimer(cEnchants.get(), 0, delay);
        ev.getEntity().setMetadata("flaming_aura_cooldown", cEnchants.getMetaValue(System.currentTimeMillis() + 6000));

    }

    @Override
    public BaseComponent[] getDescription() {
        return new ComponentBuilder()
            .append("Flaming Aura ").bold(true)
            .append("creates a powerful\nring of fire ").reset()
            .append("around you when you get damaged!")
            .create();
    }

    @Override
    public @NotNull String getName() {
        return "Flaming Aura";
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getStartLevel() {
        return 0;
    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ARMOR;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }


    @Override
    public void onRegister() {
        Bukkit.getPluginManager().registerEvents(this, cEnchants.get());
    }

    @Override
    public void onUnregister() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public Rarity getRarity() {
        return Rarity.EPIC;
    }
}
