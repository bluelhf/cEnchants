package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class LifeStealEnchantment extends CEnchantment implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity)) return;
        LivingEntity livingDamager = (LivingEntity) event.getDamager();

        if (!livingDamager.getEquipment().getItemInMainHand().containsEnchantment(this)) return;
        if (livingDamager.getMetadata("lifesteal_active").stream().anyMatch(s -> s.getOwningPlugin() == cEnchants.get())) return;

        Location target = livingDamager.getLocation();
        Location source = event.getEntity().getLocation();
        Vector direction = target.toVector().subtract(source.toVector());
        double max = target.distance(source);
        direction.normalize();
        livingDamager.getWorld().playSound(livingDamager.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_DEATH, 0.2F, 2);
        final double[] i = new double[]{0};
        int level = livingDamager.getEquipment().getItemInMainHand().getEnchantmentLevel(this);
        double totalHeal = event.getFinalDamage() * (level / (double)getMaxLevel());
        livingDamager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) Math.ceil((max/0.3)*2)+20, 5, false, false));
        livingDamager.setMetadata("lifesteal_active", cEnchants.getMetaValue(true));
        new BukkitRunnable() {@Override public void run() {
            if (!(i[0] < max)) {
                livingDamager.removeMetadata("lifesteal_active", cEnchants.get());
                this.cancel();
                return;
            }

            Location location = source.clone().add(direction.clone().multiply(i[0]));
            location.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, location, 1, 0, 0, 0, 0);
            livingDamager.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 3, 0, false, false));
            livingDamager.setHealth(Math.min(livingDamager.getHealth() + totalHeal / (max / 0.3), livingDamager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
            i[0] += 0.3;
        }}.runTaskTimer(cEnchants.get(), 0, 2);
    }

    @Override
    public void onRegister() {
        Bukkit.getPluginManager().registerEvents(this, cEnchants.get());
    }

    @Override
    public void onUnregister() {
        HandlerList.unregisterAll(this);
    }

    public LifeStealEnchantment(String key) {
        super(key);
    }

    @Override
    public @NotNull String getName() {
        return "Life Steal";
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getStartLevel() {
        return 0;
    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.WEAPON;
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
    public Rarity getRarity() {
        return Rarity.LEGENDARY;
    }
}
