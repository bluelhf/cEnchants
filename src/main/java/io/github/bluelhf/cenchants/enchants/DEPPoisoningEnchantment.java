package io.github.bluelhf.cenchants.enchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DEPPoisoningEnchantment extends CEnchantment {
    public DEPPoisoningEnchantment(String key) {
        super(key);
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }

    @Override
    public @NotNull String getName() {
        return "Poisoning";
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.TOOL;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return true;
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment other) {
        if (other instanceof DEPHealingEnchantment) return true;
        return false;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return super.canEnchantItem(item);
    }

    @Override
    public void doTick(Player p) {
        List<ItemStack> hands = Arrays.asList(p.getEquipment().getItemInMainHand(), p.getEquipment().getItemInOffHand());
        List<ItemStack> valid = hands.stream().filter(stack -> stack.containsEnchantment(this)).collect(Collectors.toList());
        if (valid.isEmpty()) return;

        ItemStack hand = valid.stream().findAny().orElseThrow();

        int duration = Optional.ofNullable(p.getPotionEffect(PotionEffectType.POISON))
                .map(PotionEffect::getDuration)
                .orElse(0);

        if (hand != null && duration < 30) {
            p.addPotionEffect(new PotionEffect(
                    PotionEffectType.POISON,
                    30,
                    hand.getEnchantmentLevel(this), true, false
            ));
        }
    }
}
