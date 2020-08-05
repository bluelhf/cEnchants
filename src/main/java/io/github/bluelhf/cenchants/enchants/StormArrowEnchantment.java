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
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class StormArrowEnchantment extends CEnchantment implements Listener {

    public StormArrowEnchantment(@NotNull String name) {
        super(name);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent ev) {
        if (!(ev.getEntity() instanceof Player)) return;
        ItemStack item;
        if ((item = EnchantUtil.getEnchantment((Player) ev.getEntity(), this)) == null) return;
        ev.getProjectile().setMetadata("storm_arrow", cEnchants.getMetaValue(true));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLand(ProjectileHitEvent ev) {
        if (ev.getEntity().getMetadata("storm_arrow").stream().noneMatch(s -> s.getOwningPlugin() == cEnchants.get())) return;
        if (!ev.getEntity().getWorld().isThundering()) return;
        ev.getEntity().getWorld().strikeLightning(ev.getEntity().getLocation());
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
        return Rarity.LEGENDARY;
    }

    @Override
    public @NotNull String getName() {
        return "Storm Arrow";
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
        return EnchantmentTarget.BOW;
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
