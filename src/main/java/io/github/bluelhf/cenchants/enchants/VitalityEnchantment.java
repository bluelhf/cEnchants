package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.utilities.EnchantUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class VitalityEnchantment extends CEnchantment {
    public VitalityEnchantment(NamespacedKey key) {
        super(key);
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }

    @Override
    public @NotNull String getName() {
        return "Vitality";
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
        return EnchantmentTarget.ARMOR_TORSO;
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
    public BaseComponent[] getDescription() {
        return new ComponentBuilder()
            .append("Vitality ").bold(true)
            .append("is basically health boost").reset()
            .create();
    }

    @Override
    public void doTick(Player p) {
        ItemStack item = EnchantUtil.getEnchantment(p, this);
        if (item == null) return;
        if (p.hasPotionEffect(PotionEffectType.HEALTH_BOOST) && p.getPotionEffect(PotionEffectType.HEALTH_BOOST).getDuration() < 160) {
            double oldHealth = p.getHealth();
            p.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 300, (item.getEnchantmentLevel(this)-1), true, false));
            p.setHealth(oldHealth);
        } else if (!p.hasPotionEffect(PotionEffectType.HEALTH_BOOST)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 300, (item.getEnchantmentLevel(this)-1), true, false));
        }
    }
}
