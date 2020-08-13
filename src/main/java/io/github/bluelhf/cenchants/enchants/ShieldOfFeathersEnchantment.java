package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.utilities.EnchantUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ShieldOfFeathersEnchantment extends CEnchantment implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onFall(EntityDamageEvent ev) {
        if (ev.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (!(ev.getEntity() instanceof Player)) return;
        Player pl = (Player) ev.getEntity();
        if (pl.getLocation().getDirection().angle(new Vector(0, -1, 0)) > Math.PI / 4) return;
        if (EnchantUtil.getEnchantment(pl, this) == null) return;
        if (!pl.isBlocking()) return;
        int level = EnchantUtil.getEnchantment(pl, this).getEnchantmentLevel(this);

        ev.setDamage(ev.getDamage() / (level+1));
        pl.getWorld().playSound(pl.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 0.8F);
        pl.getWorld().playSound(pl.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1.5F);
        ItemStack item = EnchantUtil.getEnchantment(pl, this);
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable)meta;
            cEnchants.get().getLogger().info("Past dmg is " + damageable.getDamage());
            damageable.setDamage(damageable.getDamage() + 1 + getMaxLevel()/level);
            cEnchants.get().getLogger().info("Future dmg is " + damageable.getDamage());
        }
        item.setItemMeta(meta);
    }

    @Override
    public BaseComponent[] getDescription() {
        return new ComponentBuilder()
            .append("Shield of Feathers ").bold(true)
            .append("decreases your fall damage\nwhen blocking and looking down.").reset()
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

    public ShieldOfFeathersEnchantment(NamespacedKey key) {
        super(key);
    }

    @Override
    public Rarity getRarity() {
        return Rarity.RARE;
    }

    @Override
    public @NotNull String getName() {
        return "Shield of Feathers";
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
}
