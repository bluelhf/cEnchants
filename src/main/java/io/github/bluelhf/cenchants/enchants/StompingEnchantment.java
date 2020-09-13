package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.utilities.EnchantUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class StompingEnchantment extends CEnchantment implements Listener {
	
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onFall(EntityDamageEvent ev) {
    	
        if (ev.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (!(ev.getEntity() instanceof Player)) return;
        Player pl = (Player) ev.getEntity();
        if (EnchantUtil.getEnchantment(pl, this) == null) return;
        int level = EnchantUtil.getEnchantment(pl, this).getEnchantmentLevel(this);
        
        double dmg = ev.getDamage();
        
        double power = dmg*level/3;
        
        Entity[] victims = pl.getWorld().getNearbyEntities(pl.getLocation(), power, power, power, e -> e.isOnGround() && e instanceof org.bukkit.entity.Damageable).toArray(Entity[]::new);
        
        pl.getWorld().playSound(pl.getLocation(), Sound.BLOCK_ANVIL_HIT, 1, 1.5F);
        pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) (Math.random()*30+30), 3));
        ItemStack item = EnchantUtil.getEnchantment(pl, this);
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable)meta;
            damageable.setDamage((int) (damageable.getDamage() + Math.ceil(level)));
        }
        item.setItemMeta(meta);
        
        for(int i = 0; i < victims.length; i ++) {
        	Entity target = victims[i];
            org.bukkit.entity.Damageable d = (org.bukkit.entity.Damageable) target;
            d.damage(power);
        	
        	target.getVelocity().add(new Vector(
        			power*(target.getLocation().getX() - pl.getLocation().getX()),
        			power*(target.getLocation().getY() - pl.getLocation().getY()),
        			power*(target.getLocation().getZ() - pl.getLocation().getZ())));
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
