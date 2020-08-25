package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.utilities.EnchantUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GreenFingerEnchantment extends CEnchantment implements Listener {
    public static final HashMap<Material, Material> CROP_DROP_MAP = new HashMap<>(Map.of(
            Material.POTATOES, Material.POTATO,
            Material.BEETROOTS, Material.BEETROOT,
            Material.WHEAT, Material.WHEAT,
            Material.CARROTS, Material.CARROT
    ));

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHoe(BlockBreakEvent ev) {
        if (EnchantUtil.getEnchantment(ev.getPlayer(), this) == null) return;
        ArrayList<Material> crops = new ArrayList<>(Tag.CROPS.getValues());
        Material broken = ev.getBlock().getType();
        if (!crops.contains(broken)) return;
        if (!CROP_DROP_MAP.containsKey(broken)) return;

        ev.setCancelled(true);
        ArrayList<ItemStack> drops = new ArrayList<>(ev.getBlock().getDrops(ev.getPlayer().getEquipment().getItemInMainHand(), ev.getPlayer()));
        if (drops.size() == 0) return;

        ArrayList<ItemStack> toRemove = new ArrayList<>();
        for (ItemStack i : drops) {
            if (i.getType() == CROP_DROP_MAP.get(broken)) {
                if (i.getAmount() == 1) {
                    toRemove.add(i);
                    break;
                } else {
                    i.setAmount(i.getAmount() - 1);
                }
            }
        }

        drops.removeAll(toRemove);
        for (ItemStack i : drops)
            ev.getBlock().getWorld().dropItemNaturally(ev.getBlock().getLocation(), i);


        BlockData nbd = ev.getBlock().getBlockData().clone();

        if (nbd instanceof Ageable)
            ((Ageable)nbd).setAge(0);

        ev.getBlock().setType(Material.AIR, true);
        new BukkitRunnable() {@Override public void run() {
            ev.getBlock().setBlockData(nbd, true);
        }}.runTaskLater(cEnchants.get(), 1);
    }
    public GreenFingerEnchantment(NamespacedKey key) {
        super(key);
    }

    @Override
    public Rarity getRarity() {
        return Rarity.RARE;
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
    public BaseComponent[] getDescription() {
        return new ComponentBuilder().append("Replants crops.").create();
    }

    @Override
    public @NotNull String getName() {
        return "Green-Finger";
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
        return EnchantmentTarget.TOOL;
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
    public boolean canEnchantItem(@NotNull ItemStack item) {
        if (item.getType() == Material.AIR) return false;
        if (item.getType() != Material.WOODEN_HOE
            && item.getType() != Material.GOLDEN_HOE
            && item.getType() != Material.IRON_HOE
            && item.getType() != Material.DIAMOND_HOE
            && item.getType() != Material.NETHERITE_HOE
        ) return false;
        if (item.getEnchantments().keySet().stream().anyMatch(e -> this.conflictsWith(e) && e.getKey() != this.getKey())) return false;
        return true;
    }

}
