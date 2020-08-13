package io.github.bluelhf.cenchants.utilities;

import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.enchants.CEnchantment;
import io.github.bluelhf.cenchants.enchants.HomingEnchantment;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

public class ProjectileUtil {

    @SuppressWarnings("unchecked")
    public static Projectile shootCopy(EntityShootBowEvent ev, Vector initVelocity) {
        Projectile proj = ev.getEntity().launchProjectile((Class<? extends Projectile>) ev.getProjectile().getClass(), initVelocity);
        copy(ev.getProjectile(), proj);
        return proj;
    }

    public static void copy(Entity old, Entity copy) {
        if (old instanceof AbstractArrow && copy instanceof AbstractArrow) {
            AbstractArrow oldArrow = (AbstractArrow) old;
            AbstractArrow copyArrow = (AbstractArrow) copy;

            copyArrow.setCritical(oldArrow.isCritical());
            copyArrow.setKnockbackStrength(oldArrow.getKnockbackStrength());
            copyArrow.setPickupStatus(oldArrow.getPickupStatus());
            copyArrow.setPierceLevel(oldArrow.getPierceLevel());
            copyArrow.setDamage(oldArrow.getDamage());
            copyArrow.setShotFromCrossbow(oldArrow.isShotFromCrossbow());
            cEnchants.getMetadata(oldArrow, "storm_arrow").ifPresent(val -> copyArrow.setMetadata("storm_arrow", val));
            cEnchants.getMetadata(oldArrow, "explode").ifPresent(val -> copyArrow.setMetadata("explode", val));
            if (cEnchants.getMetadata(oldArrow, "homing").isPresent()) {
                HomingEnchantment.HomingData homingData = (HomingEnchantment.HomingData) cEnchants.getMetadata(oldArrow, "homing").get().value();
                ((HomingEnchantment)CEnchantment.findByKey(new NamespacedKey(cEnchants.get(), "homing"))).home(homingData.player, copyArrow, homingData.level);
            }
        } else if (old instanceof Firework && copy instanceof Firework) {
            Firework oldFirework = (Firework) old;
            Firework copyFirework = (Firework) copy;

            copyFirework.setFireworkMeta(oldFirework.getFireworkMeta());
            copyFirework.setShotAtAngle(oldFirework.isShotAtAngle());
        }

        if (old instanceof Projectile && copy instanceof Projectile) {
            ((Projectile) copy).setBounce(((Projectile) old).doesBounce());
            ((Projectile) copy).setShooter(((Projectile) old).getShooter());
        }


        copy.setCustomName(old.getCustomName());
        copy.setFireTicks(old.getFireTicks());
        copy.setCustomNameVisible(old.isCustomNameVisible());
        copy.setInvulnerable(old.isInvulnerable());
        copy.setGlowing(old.isGlowing());
        copy.setSilent(old.isSilent());
        copy.setGravity(old.hasGravity());
        copy.setOp(old.isOp());
        copy.setPortalCooldown(old.getPortalCooldown());
    }

    @SuppressWarnings("unchecked")
    public static Projectile shootCopy(ProjectileHitEvent ev, Vector initVelocity) {
        Projectile proj = ev.getEntity().getLocation().getWorld().spawnArrow(ev.getEntity().getLocation(), initVelocity, (float) initVelocity.length(), 0);
        copy(ev.getEntity(), proj);
        return proj;
    }
}
