package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.utilities.ShapeUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlastOfTheAncientsEnchantment extends CEnchantment implements Listener {
    // Shield charging
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent ev) {
        if (!(ev.getEntity() instanceof Player)) return;
        Player pl = (Player) ev.getEntity();
        if (!(pl.getEquipment().getItemInMainHand().getType() == Material.SHIELD) && !(pl.getEquipment().getItemInOffHand().getType() == Material.SHIELD)) return;

        ItemStack hand = null;
        if (pl.getEquipment().getItemInMainHand().containsEnchantment(this)) hand = pl.getEquipment().getItemInMainHand();
        if (pl.getEquipment().getItemInOffHand().containsEnchantment(this)) hand = pl.getEquipment().getItemInOffHand();
        if (hand == null) return;

        int level = hand.getEnchantmentLevel(this);


        int preHit = pl.getStatistic(Statistic.DAMAGE_BLOCKED_BY_SHIELD);
        ItemStack finalHand = hand;
        new BukkitRunnable() {@Override public void run() {
            int postHit = pl.getStatistic(Statistic.DAMAGE_BLOCKED_BY_SHIELD);
            int increase = postHit - preHit;

            ItemMeta meta = finalHand.getItemMeta();
            double charge = 0;
            try {
                charge = meta.getPersistentDataContainer().get(cEnchants.getNamespacedKey("bota_charge"), PersistentDataType.DOUBLE);
            } catch (NullPointerException ignored) { }

            if (charge >= 1000) {
                pl.playSound(pl.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 1);
                pl.playSound(pl.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 2, 0);
                pl.sendTitle("", BaseComponent.toLegacyText(new ComponentBuilder().append("The power of the Ancients fills your body").bold(true).color(ChatColor.DARK_PURPLE).create()), 0, 80, 40);



                long start = System.currentTimeMillis();
                double[] radius = new double[]{0};
                new BukkitRunnable() {@Override public void run() {
                    if (System.currentTimeMillis() - start > 4500) {
                        this.cancel();
                        return;
                    }

                    radius[0] = radius[0] + level/10D;
                    double r = 3+radius[0];

                    if (System.currentTimeMillis() - start < 1500) {
                        List<StoredParticle> particles = new ArrayList<>();
                        for (Location l : ShapeUtil.fibonacciLattice(pl.getLocation(), r, 200)) {
                            particles.add(new StoredParticle(Particle.DRAGON_BREATH, l, 0, 0, 0, 0, 0));
                        }
                        particles.forEach(StoredParticle::spawn);
                    }

                    Collection<Entity> entities = pl.getLocation().getWorld().getNearbyEntities(pl.getLocation(), r, r, r, e -> {
                        if (e.getLocation().distanceSquared(pl.getLocation()) > r*r) return false;
                        if (!(e instanceof LivingEntity) && !(e instanceof Projectile)) return false;
                        if (e == pl) return false;
                        if (e instanceof Tameable && ((Tameable)e).getOwner() == pl) return false;
                        return true;
                    });
                    for (Entity e : entities) {
                        Vector toE = e.getLocation().toVector().subtract(pl.getLocation().toVector());
                        if (e instanceof Damageable) ((Damageable)e).damage(level*5, pl);
                        e.setVelocity(e.getVelocity().add(toE.multiply(0.3 * level)));
                    }


                }}.runTaskTimer(cEnchants.get(), 0, 2);

                meta.getPersistentDataContainer().set(cEnchants.getNamespacedKey("bota_charge"), PersistentDataType.DOUBLE, 0D);
                finalHand.setItemMeta(meta);
                return;
            }

            double divisor = 20;
            double lim = charge + increase;

            ComponentBuilder builder = new ComponentBuilder();
            StringBuilder strBuilder = new StringBuilder();
            for (int i = 1; i <= 1000/divisor; i++) {
                strBuilder.append("|");
                if (i > (lim/divisor) && i-1 <= (lim/divisor) || (int)(lim) == 1000) {
                    builder.append(strBuilder.toString());
                    builder.color(ChatColor.GREEN);
                    strBuilder = new StringBuilder();
                }
            }
            builder.append(strBuilder.toString());
            builder.color(ChatColor.DARK_PURPLE);
            pl.spigot().sendMessage(ChatMessageType.ACTION_BAR, builder.create());

            meta.getPersistentDataContainer().set(cEnchants.getNamespacedKey("bota_charge"), PersistentDataType.DOUBLE, charge + increase);
            finalHand.setItemMeta(meta);

        }}.runTaskLater(cEnchants.get(), 1);
    }

    @Override
    public BaseComponent[] getDescription() {
        return new ComponentBuilder()
            .append("Blast of the Ancients ").bold(true)
            .append("charges as you\nblock damage with your shield.\n").reset()
            .append("A powerful blast is released at full charge!")
            .create();
    }

    @Override
    public void onRegister() {
        Bukkit.getPluginManager().registerEvents(this, cEnchants.get());
    }

    @Override
    public void onUnregister() {
        HandlerList.unregisterAll(this);
    }

    public BlastOfTheAncientsEnchantment(NamespacedKey key) {
        super(key);
    }

    @Override
    public Rarity getRarity() {
        return Rarity.LEGENDARY;
    }

    @Override
    public @NotNull String getName() {
        return "Blast of the Ancients";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 0;
    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.TOOL;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        if (item.getType() == Material.AIR) return false;
        if (item.getType() != Material.SHIELD) return false;
        if (item.getEnchantments().keySet().stream().anyMatch(e -> this.conflictsWith(e) && e.getKey() != this.getKey())) return false;
        return true;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    public static class StoredParticle {
        final Particle particle;
        final Location location;
        final int count;
        final double xOff;
        final double yOff;
        final double zOff;
        final double extra;
        public StoredParticle(Particle particle, Location location, int count, double xOff, double yOff, double zOff, double extra) {
            this.particle = particle;
            this.location = location;
            this.count = count;
            this.xOff = xOff;
            this.yOff = yOff;
            this.zOff = zOff;
            this.extra = extra;
        }

        public void spawn() {
            this.location.getWorld().spawnParticle(particle, location, 0, xOff, yOff, zOff, extra);
        }
    }
}
