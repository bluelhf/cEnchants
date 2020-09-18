package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.utilities.ProjectileUtil;
import io.github.bluelhf.cenchants.utilities.Quaternion;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.AbstractArrow;
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
    public MultiquiverEnchantment(NamespacedKey key) {
        super(key);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent ev) {
        if (ev.getBow() == null) return;
        if (!ev.getBow().containsEnchantment(this)) return;
        Projectile p = (Projectile) ev.getProjectile();
        if (p.hasMetadata("multishot_arrow")) return;

        World w = ev.getEntity().getWorld();

        int level = ev.getBow().getEnchantmentLevel(this);
        ev.setCancelled(true);
        if (ev.getConsumable() != null && ev.shouldConsumeItem()) ev.getConsumable().setAmount(ev.getConsumable().getAmount()-1);

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
            AbstractArrow proj = (AbstractArrow) ProjectileUtil.shootCopy(ev, shotDirection.multiply(ev.getForce()*3));
            proj.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);

            // Allows a random multi-shot arrow to be picked up.
            if (ev.shouldConsumeItem() && ctr++ != -1 && ctr == random) {
                proj.setPickupStatus(AbstractArrow.PickupStatus.ALLOWED);
                ctr = -1;
            }

            EntityShootBowEvent event = new EntityShootBowEvent(ev.getEntity(), ev.getBow(), null, proj, ev.getHand(), ev.getForce(), false);
            proj.setMetadata("multishot_arrow", cEnchants.getMetaValue(true));
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    @Override
    public BaseComponent[] getDescription() {
        return new ComponentBuilder()
            .append("Multi-Quiver ").bold(true)
            .append("splits your arrow into many!").reset()
            .create();
    }

    private Vector getUpVector(double pitch, double yaw) {
        return getViewVector(pitch - Math.PI / 2, yaw);
    }

    private Vector getViewVector(double pitch, double yaw) {
        yaw *= -1;
        double cosYaw = Math.cos(yaw);
        double sinYaw = Math.sin(yaw);
        double cosPitch = Math.cos(pitch);
        double sinPitch = Math.sin(pitch);
        return new Vector(sinYaw * cosPitch, -sinPitch, cosYaw * cosPitch);
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
