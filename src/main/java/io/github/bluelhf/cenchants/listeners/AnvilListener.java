package io.github.bluelhf.cenchants.listeners;

import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.enchants.CEnchantment;
import io.github.bluelhf.cenchants.utilities.EnchantUtil;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AnvilListener implements Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onAnvil(PrepareAnvilEvent ev) {
        AnvilInventory inventory = ev.getInventory();
        ItemStack target = inventory.getItem(0);
        ItemStack enchant = inventory.getItem(1);

        if (target == null || enchant == null) return;

        ItemMeta enchantMeta = enchant.getItemMeta();
        if (!(enchantMeta instanceof EnchantmentStorageMeta)) return;
        EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) enchantMeta;

        ItemStack result = target.clone();
        AtomicReference<ItemMeta> resultMeta = new AtomicReference<>(target.clone().getItemMeta());

        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        storageMeta.getStoredEnchants().forEach(enchants::put);
        for (CEnchantment e : CEnchantment.getEnchantments()) {
            if (enchantMeta.hasEnchant(e) && EnchantUtil.findLoreEntry(e, enchantMeta.getLore()).size() > 0) {
                enchants.put(e, enchantMeta.getEnchantLevel(e));
            }

        }

        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment ench = entry.getKey();
            int level = entry.getValue();
            if (!ench.canEnchantItem(result)) return;


            try {
                resultMeta.set(EnchantUtil.enchantItem(resultMeta.get(), ench, level, true));
            } catch (IllegalArgumentException ignored) {}
        }

        result.setItemMeta(resultMeta.get());
        ev.setResult(result);
        new BukkitRunnable() {@Override public void run() {
            ev.getInventory().setItem(2, result);
        }}.runTaskLater(cEnchants.get(), 2);

    }
}
