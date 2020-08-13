package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class DEPFlingEnchantment extends CEnchantment implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof LivingEntity)) return;
        LivingEntity ld = (LivingEntity) e.getDamager();
        if (!ld.getEquipment().getItemInMainHand().containsEnchantment(this)) return;
        int level = ld.getEquipment().getItemInMainHand().getEnchantmentLevel(this);
        new BukkitRunnable() {@Override public void run() {
            e.getEntity().setVelocity(e.getEntity().getVelocity().add(new Vector(0, level * 0.3, 0)));
        }}.runTaskLater(cEnchants.get(), 1);
    }

    public DEPFlingEnchantment(NamespacedKey key) {
        super(key);
    }

    @Override
    public @NotNull String getName() {
        return "Fling";
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
    public boolean conflictsWith(@NotNull Enchantment other) {
        return false;
    }

    @Override
    public BaseComponent[] getDescription() {
        return new BaseComponent[0];
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
        return Rarity.UNCOMMON;
    }
}
