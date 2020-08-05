package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.utilities.Quaternion;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class MultiquiverEnchantment extends CEnchantment implements Listener {
    public MultiquiverEnchantment(String key) {
        super(key);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent ev) {
        if (!ev.getBow().containsEnchantment(this)) return;
        Projectile p = (Projectile) ev.getProjectile();
        if (p.hasMetadata("multishot_arrow")) return;

        World w = ev.getEntity().getWorld();

        int level = ev.getBow().getEnchantmentLevel(this);
        ev.setCancelled(true);
        if (ev.getEntity() instanceof InventoryHolder) {
            InventoryHolder holder = (InventoryHolder) ev.getEntity();
            Inventory inv = holder.getInventory();
            if (inv.getContents() != null) {
                for (ItemStack i : holder.getInventory()) {
                    if (i == null) continue;
                    if (i.getType() != Material.ARROW && i.getType() != Material.TIPPED_ARROW && i.getType() != Material.SPECTRAL_ARROW)
                        continue;
                    if (holder instanceof Player && (
                                ((Player) holder).getGameMode() == GameMode.CREATIVE)
                                || ev.getBow().containsEnchantment(Enchantment.ARROW_INFINITE)
                            ) continue;
                    i.setAmount(i.getAmount()-1);
                    break;
                }
            }
            if (holder instanceof Player) ((Player) holder).updateInventory();

        }

        double maxAngle = Math.PI / 6; // 180/6 = 30;
        double yaw = ev.getEntity().getLocation().getYaw() * Math.PI / 180;
        double pitch = ev.getEntity().getLocation().getPitch() * Math.PI / 180;

        Vector upVector = getUpVector(pitch, yaw);
        // Add a degree to the max angle to account for floating point errors
        int ctr = 1;
        int maxI = (int) ((maxAngle + (Math.PI / 180)) / (maxAngle / (double) level));
        int random = (int) (Math.random() * maxI);
        for (double i = -maxAngle / 2; i <= maxAngle / 2 + (Math.PI / 180); i += maxAngle / (double) level) {
            w.playSound(ev.getEntity().getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 2, 1);
            Quaternion quaternion = new Quaternion(upVector.clone(), i, false);
            Vector shotDirection = getViewVector(pitch, yaw);
            shotDirection = Quaternion.transformByQuaternion(shotDirection, quaternion);

            // Multiply by 3 because that's the speed of a full charge arrow
            AbstractArrow old = (AbstractArrow) ev.getProjectile();
            AbstractArrow proj = ev.getEntity().launchProjectile(Arrow.class, shotDirection.multiply(ev.getForce() * 3));
            proj.setDamage(old.getDamage());
            proj.setCritical(old.isCritical());
            proj.setKnockbackStrength(old.getKnockbackStrength());
            proj.setPierceLevel(old.getPierceLevel());
            proj.setBounce(old.doesBounce());
            proj.setFireTicks(old.getFireTicks());

            boolean hasStormArrow = ev.getProjectile().getMetadata("storm_arrow").stream().anyMatch(s -> s.getOwningPlugin() == cEnchants.get() && s.asBoolean());
            if (hasStormArrow) proj.setMetadata("storm_arrow", cEnchants.getMetaValue(hasStormArrow));
            proj.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            if (ctr != -1) {
                ctr++;
                if (ctr == random) {
                    proj.setPickupStatus(AbstractArrow.PickupStatus.ALLOWED);
                    ctr = -1;
                }
            }
            EntityShootBowEvent event = new EntityShootBowEvent(ev.getEntity(), ev.getBow(), proj, ev.getForce());
            proj.setMetadata("multishot_arrow", cEnchants.getMetaValue(true));
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    private Vector getUpVector(double pitch, double yaw) {
        return getViewVector(pitch - Math.PI / 2, yaw);
    }

    private Vector getViewVector(double pitch, double yaw) {
        yaw *= -1;
        double var_2 = Math.cos(yaw);
        double var_3 = Math.sin(yaw);
        double var_4 = Math.cos(pitch);
        double var_5 = Math.sin(pitch);
        return new Vector(var_3 * var_4, -var_5, var_2 * var_4);
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
        return "Multi-Quiver";
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
        return EnchantmentTarget.BOW;
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
