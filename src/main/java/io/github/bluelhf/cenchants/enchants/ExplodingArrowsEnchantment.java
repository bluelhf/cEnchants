package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ExplodingArrowsEnchantment extends CEnchantment implements Listener {

    public ExplodingArrowsEnchantment(NamespacedKey key) {
        super(key);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLand(ProjectileHitEvent ev) {
        Optional<MetadataValue> meta = cEnchants.getMetadata(ev.getEntity(), "explode");
        meta.ifPresent(val -> {
            ItemStack i = (ItemStack) val.value();
            if (i == null) return;
            ev.getEntity().getWorld().createExplosion(ev.getEntity().getLocation(), i.getEnchantmentLevel(this), false, false);
            ev.getEntity().remove();
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent ev) {
        if (ev.getBow() == null || !ev.getBow().containsEnchantment(this) || !(ev.getEntity() instanceof Player)) return;
        ev.getProjectile().setMetadata("explode", cEnchants.getMetaValue(ev.getBow().clone()));
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

    @Override
    public BaseComponent[] getDescription() {
        return new ComponentBuilder().append("We need to have exploding arrows. They're actually kind of fun.").create();
    }

    @Override
    public @NotNull String getName() {
        return "Exploding Arrows";
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
