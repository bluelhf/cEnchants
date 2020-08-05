package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class CEnchantment extends Enchantment {

    private static List<CEnchantment> enchantments = new ArrayList<>();

    public CEnchantment(String key) {
        super(new NamespacedKey(cEnchants.get(), key));
    }

    public static void register(CEnchantment enchantment) {
        try {
            Field acceptingNew = Enchantment.class.getDeclaredField("acceptingNew");
            acceptingNew.setAccessible(true);
            acceptingNew.set(null, true);
            Enchantment.registerEnchantment(enchantment);
            enchantments.add(enchantment);
            enchantment.onRegister();
            acceptingNew.set(null, false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onRegister() {

    }

    public void onUnregister() {

    }

    @SuppressWarnings({"unchecked", "deprecation"})
    public static void unregister(CEnchantment enchantment) {
        try {
            enchantments.remove(enchantment);
            enchantment.onUnregister();
            Field byKeyField = Enchantment.class.getDeclaredField("byKey");
            Field byNameField = Enchantment.class.getDeclaredField("byName");
            byKeyField.setAccessible(true);
            byNameField.setAccessible(true);
            Map<NamespacedKey, Enchantment> byKey = (Map<NamespacedKey, Enchantment>) byKeyField.get(null);
            Map<String, Enchantment> byName = (Map<String, Enchantment>) byNameField.get(null);
            byKey.remove(enchantment.getKey());
            byName.remove(enchantment.getName());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public abstract Rarity getRarity();

    @Override
    public boolean conflictsWith(@NotNull Enchantment other) {
        CEnchantment wrapped = fromBukkitEnchantment(other);
        if (wrapped != null) {
            int min = Rarity.EPIC.ordinal();
            if (this.getRarity().ordinal() >= min && wrapped.getRarity().ordinal() >= min) return true;
        }
        return false;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        if (item.getType() == Material.AIR) return false;
        if (!getItemTarget().includes(item)) return false;
        if (item.getEnchantments().keySet().stream().anyMatch(e -> this.conflictsWith(e) && e.getKey() != this.getKey())) return false;
        return true;
    }


    public void doTick(Player p) {

    }


    public static @Nullable CEnchantment fromBukkitEnchantment(Enchantment enchantment) {
        return findByKey(enchantment.getKey());
    }
    public static @Nullable CEnchantment findByKey(NamespacedKey key) {
        return enchantments.stream().filter(e -> e.getKey().equals(key)).findFirst().orElse(null);
    }
    public static List<CEnchantment> getEnchantments() {
        return enchantments;
    }
}
