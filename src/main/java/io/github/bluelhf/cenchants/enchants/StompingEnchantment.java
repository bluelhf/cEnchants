package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.utilities.ProjectileUtil;
import io.github.bluelhf.cenchants.utilities.Quaternion;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.util.Collection;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class StompingEnchantment extends CEnchantment implements Listener {
	
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onFall(EntityDamageEvent ev) {
    	
        if (ev.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (!(ev.getEntity() instanceof LivingEntity)) return;
        Entity entity = (Entity) ev.getEntity();
        if (EnchantUtil.getEnchantment(entity, this) == null) return;
        int level = EnchantUtil.getEnchantment(entity, this).getEnchantmentLevel(this);
        
        double dmg = ev.getDamage();
        
        double power = dmg*level/3;
        
        Collection<Entity> victims = entity.getWorld().getNearbyEntities(entity.getLocation(), power, power, power, e -> (e.isOnGround()));
        
        entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_ANVIL_HIT, 1, 1.5F);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) (Math.random()*30+30), 3));
        ItemStack item = EnchantUtil.getEnchantment(entity, this);
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable)meta;
            damageable.setDamage(damageable.getDamage() + Math.ceil(level));
        }
        item.setItemMeta(meta);
        
        for(int i = 0; i < victims.size(); i ++) {
        	Entity target = victims[i];
        	
        	entity.getServer().broadcastMessage(target.getCustomName() + " just got stomped lol");
        	target.getVelocity().add(new Vector(
        			power*(target.getLocation().getX() - entity.getLocation().getX()), 
        			power*(target.getLocation().getY() - entity.getLocation().getY()), 
        			power*(target.getLocation().getZ() - entity.getLocation().getZ())));
        }
        
    }
	
    public StompingEnchantment(NamespacedKey key) {
        super(key);
    }

    @Override
    public BaseComponent[] getDescription() {
        return new ComponentBuilder()
            .append("Stomping ").bold(true)
            .append("flings enemies when you land!").reset()
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
        return "Stomping";
    }

    @Override
    public int getMaxLevel() {
        return 4;
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
    public Rarity getRarity() {
        return Rarity.EPIC;
    }
}
