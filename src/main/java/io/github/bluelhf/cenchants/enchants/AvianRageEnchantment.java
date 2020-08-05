package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.utilities.EnchantUtil;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class AvianRageEnchantment extends CEnchantment implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent ev) {
        if (!(ev.getEntity() instanceof Player)) return;
        if (ev.getEntity().getMetadata("avian_rage_cooldown").stream().anyMatch(m -> m.getOwningPlugin() == cEnchants.get() && m.asLong() > System.currentTimeMillis())) return;
        ItemStack item = EnchantUtil.getEnchantment((Player)ev.getEntity(), this);
        if (item == null) return;
        int level = item.getEnchantmentLevel(this);

        ((Player) ev.getEntity()).addPotionEffects(Arrays.asList(
                new PotionEffect(PotionEffectType.SPEED, 400, level),
                new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 400, level),
                new PotionEffect(PotionEffectType.JUMP, 400, level)
        ));
        ev.getEntity().setMetadata("avian_rage_cooldown", cEnchants.getMetaValue(System.currentTimeMillis() + 25000));
    }

    @Override
    public void onRegister() {
        Bukkit.getPluginManager().registerEvents(this, cEnchants.get());
    }

    @Override
    public void onUnregister() {
        HandlerList.unregisterAll(this);
    }

    public AvianRageEnchantment(String key) {
        super(key);
    }

    @Override
    public Rarity getRarity() {
        return Rarity.RARE;
    }

    @Override
    public @NotNull String getName() {
        return "Avian Rage";
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
}
