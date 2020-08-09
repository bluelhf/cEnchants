package io.github.bluelhf.cenchants.utilities;

import io.github.bluelhf.cenchants.cEnchants;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import io.github.bluelhf.cenchants.enchants.CEnchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnchantUtil {
    private static final String[] NUMERALS = { "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X" };

    public static ItemMeta enchantItem(ItemMeta oldMeta, @NotNull Enchantment ench, int enchLevel, boolean force) {
        ItemMeta meta = oldMeta.clone();
        boolean success = meta.addEnchant(ench, enchLevel, force);
        if (!success)
            throw new IllegalArgumentException("Level must be between " + ench.getStartLevel() + " and " + ench.getMaxLevel());


        if (ench instanceof CEnchantment) {
            // Special handling for CEnchantments since they have custom lores
            if (meta.hasLore()) {
                List<String> newLore = EnchantUtil.removeLoreEntry((CEnchantment) ench, meta.getLore());
                newLore.add(0, "§r§7" + EnchantUtil.getLoreEntry(ench, enchLevel));
                meta.setLore(newLore);
            } else {
                meta.setLore(Arrays.asList("§r§7" + EnchantUtil.getLoreEntry(ench, enchLevel)));
            }
        }

        return meta;
    }

    public static String getLoreEntry(Enchantment ench, int enchLevel){
        if (enchLevel == 1 && ench.getMaxLevel() == 1){
            return getName(ench);
        }
        if (enchLevel > 10 || enchLevel <= 0){
            return getName(ench) + " enchantment.level." + enchLevel;
        }

        return getName(ench) + " " + NUMERALS[enchLevel-1];
    }

    public static List<Integer> findLoreEntry(CEnchantment ench, List<String> lore) {
        List<String> newLore = new ArrayList<>(lore);
        List<Integer> indices = new ArrayList<>();
        int ctr = 0;
        for(String s : newLore) {
            boolean contains = ChatColor.stripColor(s).contains(ChatColor.stripColor(ench.getName()));
            if (contains)
                indices.add(ctr);
            ctr++;
        }
        return indices;
    }
    public static List<String> removeLoreEntry(CEnchantment ench, List<String> lore) {
        List<Integer> indices = findLoreEntry(ench, lore);
        List<String> newLore = new ArrayList<>();
        for(int i = 0; i < lore.size(); i++) {
            if (indices.contains(i)) continue;
            newLore.add(lore.get(i));
        }
        return newLore;
    }

    public static String getName(Enchantment ench) {
        StringBuilder builder = new StringBuilder();
        String id;

        // We can trust our own enchantments to have proper names
        if (!(ench instanceof CEnchantment)) {
            id = ench.getKey().getKey().replaceAll("_", " ");
            for (String s : id.split(" ")) {
                builder.append(s.substring(0, 1).toUpperCase()).append(s.substring(1).toLowerCase()).append(" ");
            }
        } else {
            CEnchantment wrapped = (CEnchantment) ench;
            id = ench.getName();
            builder.append(wrapped.getRarity().getColour()).append(id);
        }

        return builder.toString().trim();
    }

    public static @Nullable ItemStack getEnchantment(Player e, Enchantment ench) {
        EnchantmentTarget target = ench.getItemTarget();
        int idx = -1;
        for(ItemStack i : e.getInventory()) {
            idx++;
            if (i == null) continue;
            if ((target == EnchantmentTarget.ARMOR       || target == EnchantmentTarget.WEARABLE) && (idx < 36 || idx > 39)) continue;
            if (target  == EnchantmentTarget.ARMOR_HEAD  && idx    != 39) continue;
            if (target  == EnchantmentTarget.ARMOR_TORSO && idx    != 38) continue;
            if (target  == EnchantmentTarget.ARMOR_LEGS  && idx    != 37) continue;
            if (target  == EnchantmentTarget.ARMOR_FEET  && idx    != 36) continue;
            if ((target == EnchantmentTarget.TOOL
                    || target == EnchantmentTarget.BOW
                    || target == EnchantmentTarget.BREAKABLE
                    || target == EnchantmentTarget.CROSSBOW
                    || target == EnchantmentTarget.WEAPON
                    || target == EnchantmentTarget.FISHING_ROD
                    || target == EnchantmentTarget.VANISHABLE
                    || target == EnchantmentTarget.TRIDENT) && idx != e.getInventory().getHeldItemSlot() && idx != 40 /* offhand */) continue;

            if (i.containsEnchantment(ench)) {
                return i;
            }
        }
        return null;
    }
}
