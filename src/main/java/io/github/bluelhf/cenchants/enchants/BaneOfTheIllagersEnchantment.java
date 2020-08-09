package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.utilities.EnchantUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BaneOfTheIllagersEnchantment extends CEnchantment implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent ev) {
        if (!(ev.getDamager() instanceof Player)) return;
        if (ev.getEntityType() != EntityType.DOLPHIN
                && ev.getEntityType() != EntityType.ILLUSIONER
                && ev.getEntityType() != EntityType.PILLAGER
                && ev.getEntityType() != EntityType.EVOKER
                && ev.getEntityType() != EntityType.RAVAGER
                && ev.getEntityType() != EntityType.VINDICATOR
                && ev.getEntityType() != EntityType.VEX) return;
        ItemStack i = EnchantUtil.getEnchantment((Player)ev.getDamager(), this);
        if (i == null) return;
        int level = i.getEnchantmentLevel(this);
        ev.getEntity().getWorld().playSound(ev.getEntity().getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1, 1.3F);
        ev.setDamage(ev.getDamage() + (ev.getDamage() * (0.25 * level)));
    }

    @Override
    public BaseComponent[] getDescription() {
        return new ComponentBuilder().append("Deals more damage to Illagers").create();
    }

    public BaneOfTheIllagersEnchantment(String key) {
        super(key);
    }

    @Override
    public void onRegister() {
        Bukkit.getPluginManager().registerEvents(this, cEnchants.get());
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
    }

    @Override
    public Rarity getRarity() {
        return Rarity.EPIC;
    }

    @Override
    public @NotNull String getName() {
        return "Bane of the Illagers";
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
}
