package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.utilities.EnchantUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class FrostTouchEnchantment extends CEnchantment implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent ev) {
        if (!(ev.getDamager() instanceof Player)) return;
        if (!(ev.getEntity() instanceof LivingEntity)) return;
        LivingEntity livingVictim = (LivingEntity) ev.getEntity();

        if (!(ev.getDamager() instanceof Player)) return;
        ItemStack item;
        if ((item = EnchantUtil.getEnchantment((Player)ev.getDamager(), this)) == null) return;
        if (livingVictim.hasPotionEffect(PotionEffectType.SLOW)) return;

        int level = item.getEnchantmentLevel(this);
        livingVictim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100*level, level, true, true));
        long start = System.currentTimeMillis();
        new BukkitRunnable() {@Override public void run() {
            long ticksTaken = (System.currentTimeMillis() - start) / 50;
            if (ticksTaken > 100 * level || !livingVictim.isValid()) {
                this.cancel();
                return;
            }
            livingVictim.getWorld().playSound(livingVictim.getLocation(), Sound.ITEM_ELYTRA_FLYING, 0.02F, 2F);

            double w = livingVictim.getWidth() / 1.5D;
            double h = livingVictim.getHeight() / 1.5D;
            livingVictim.getWorld().spawnParticle(Particle.ITEM_CRACK, livingVictim.getLocation(), (int) (Math.random()*7+5), w, h, w, 0, new ItemStack(Material.ICE));
        }}.runTaskTimer(cEnchants.get(), 0, 4);

    }

    @Override
    public BaseComponent[] getDescription() {
        return new ComponentBuilder()
            .append("Frost Touch ").bold(true)
            .append("freezes everything you hit!").reset()
            .create();
    }

    public FrostTouchEnchantment(NamespacedKey key) {
        super(key);
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
    public @NotNull String getName() {
        return "Frost Touch";
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
        return Rarity.UNCOMMON;
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment other) {
        return super.conflictsWith(other) || other instanceof FlamingAuraEnchantment || other == Enchantment.FIRE_ASPECT;
    }
}
