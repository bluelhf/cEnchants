package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.utilities.ProjectileUtil;
import io.github.bluelhf.cenchants.utilities.Quaternion;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.util.Random;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class SmeltingTouchEnchantment extends CEnchantment implements Listener {
	
    public SmeltingTouchEnchantment(NamespacedKey key) {
        super(key);
    }
    
    Random random = new Random();
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockDropItemEvent ev) {
    	
    	if (ev.isCancelled()) return;
    	if (!(ev.getEntity() instanceof Player)) return;
        Player pl = (Player) ev.getEntity();
        if (EnchantUtil.getEnchantment(pl, this) == null) return;
        int level = EnchantUtil.getEnchantment(pl, this).getEnchantmentLevel(this);
        int fortune = 1;
        if (EnchantUtil.getEnchantment(pl, this) != null) {
        	int fortune = 1 + random.nextInt(EnchantUtil.getEnchantment(pl, Enchantment.LOOT_BONUS_BLOCKS).getEnchantmentLevel(this));
        }
    	
    	Item[] drops = ev.getItems().toArray(Item[]::new);
    	
    	for (int i = 0; i < drops.length; i ++) {
    		ItemStack drop = getSmelted(drops[i].getItemStack());
    		if (drop == null) continue;
        	drop = new ItemStack(drop.getType(), drop.getAmount() * fortune);
        	ev.getItems().set(i, drop);
    		
    	}
    	
    }
    
    private ItemStack getSmelted(ItemStack input) {
    	
    	ItemStack result = null;
		Iterator<Recipe> iter = Bukkit.recipeIterator();
		while (iter.hasNext()) {
		   Recipe recipe = iter.next();
		   if (!(recipe instanceof FurnaceRecipe)) continue;
		   if (((FurnaceRecipe) recipe).getInput().getType() != input.getType()) continue;
		   result = recipe.getResult();
		   break;
		}
		
		return result;
		
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
