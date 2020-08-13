package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.utilities.EnchantUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class BlessingOfTheDesertEnchantment extends CEnchantment {
    public BlessingOfTheDesertEnchantment(NamespacedKey key) {
        super(key);
    }

    @Override
    public Rarity getRarity() {
        return Rarity.RARE;
    }

    @Override
    public @NotNull String getName() {
        return "Blessing of the Desert";
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
        return EnchantmentTarget.ARMOR_FEET;
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
            .append("Blessing of the Desert ").bold(true)
            .append("speeds you up while on sand.").reset()
            .create();
    }

    @Override
    public void doTick(Player p) {
        ItemStack item = EnchantUtil.getEnchantment(p, this);
        if (item == null) return;
        if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.SAND) return;
        if (p.hasPotionEffect(PotionEffectType.SPEED) && p.getPotionEffect(PotionEffectType.SPEED).getDuration() <= 20) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, (item.getEnchantmentLevel(this)-1), true, true));
        } else if (!p.hasPotionEffect(PotionEffectType.SPEED)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, (item.getEnchantmentLevel(this)-1), true, true));
        }
    }
}
