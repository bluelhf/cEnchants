package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.utilities.EnchantUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class SmeltingTouchEnchantment extends CEnchantment implements Listener {

    private HashMap<Material, Material> smeltMap = new HashMap<>();
    private Random random = new Random();

    public SmeltingTouchEnchantment(NamespacedKey key) {
        super(key);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockDropItemEvent ev) {
        if (ev.isCancelled()) return;
        if (EnchantUtil.getEnchantment(ev.getPlayer(), this) == null) return;

        int fortuneLevel = 0;
        if (ev.getPlayer().getEquipment() != null)
            fortuneLevel = ev.getPlayer().getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

        for (int i = 0; i < ev.getItems().size(); i++) {

            Item drop = ev.getItems().get(i);
            ItemStack stack = drop.getItemStack();

            if (smeltMap.containsKey(stack.getType())) {
                int add = 0;
                if (random.nextInt(101) >= (2 / (fortuneLevel + 2) * 100)) {
                    add = random.nextInt(fortuneLevel - 1) + 1;
                }
                stack.setAmount(stack.getAmount() + add);
                stack.setType(smeltMap.get(stack.getType()));
                drop.setItemStack(stack);
            }
        }
    }

    @Override
    public BaseComponent[] getDescription() {
        return new ComponentBuilder()
            .append("Smelting Touch").bold(true)
            .append("smelts things it touches!").reset()
            .create();
    }

    @Override
    public void onRegister() {
        Bukkit.getPluginManager().registerEvents(this, cEnchants.get());

        Iterator<Recipe> iter = Bukkit.recipeIterator();
        while (iter.hasNext()) {
            Recipe recipe = iter.next();
            if (!(recipe instanceof FurnaceRecipe)) continue;
            smeltMap.put(((FurnaceRecipe) recipe).getInput().getType(), recipe.getResult().getType());
        }
    }

    @Override
    public void onUnregister() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public @NotNull String getName() {
        return "Smalting Touch";
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
    public Rarity getRarity() {
        return Rarity.RARE;
    }

}
