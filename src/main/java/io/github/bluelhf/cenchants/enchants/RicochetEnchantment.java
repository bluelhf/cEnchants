package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.utilities.ProjectileUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class RicochetEnchantment extends CEnchantment implements Listener {


    public RicochetEnchantment(NamespacedKey key) {
        super(key);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLand(ProjectileHitEvent ev) {
        Optional<MetadataValue> meta = cEnchants.getMetadata(ev.getEntity(), "ricochet");
        meta.ifPresent(val -> {
            ItemStack i = (ItemStack) val.value();
            if (i == null) return;
            cEnchants.get().getLogger().info("Hello from ricochet!");
            Projectile p = ev.getEntity();
            Vector inversion = new Vector(1, 1, 1);
            Location pL = p.getLocation();
            if (ev.getHitEntity() != null) {
                Entity e = ev.getHitEntity();
                Location eL = e.getLocation();
                double xDiff = Math.abs(eL.getX() - pL.getX());
                double yDiff = Math.abs(eL.getY() - pL.getY());
                double zDiff = Math.abs(eL.getZ() - pL.getZ());
                double max = Math.max(Math.max(xDiff, yDiff), zDiff);
                if (max == xDiff) {
                    inversion.setX(-1);
                } else if (max == yDiff) {
                    inversion.setY(-1);
                } else if (max == zDiff) {
                    inversion.setZ(-1);
                }
                cEnchants.get().getLogger().info("Got hit entity, inversion is " + inversion);
            } else if (ev.getHitBlockFace() != null) {
                Vector d = ev.getHitBlockFace().getDirection();
                inversion = new Vector((d.getX() != 0) ? -1 : 1, (d.getY() != 0) ? -1 : 1, (d.getZ() != 0) ? -1 : 1);
                cEnchants.get().getLogger().info("Got hit block, inversion is " + inversion);
            }

            ProjectileUtil.shootCopy(ev, p.getVelocity().clone().multiply(inversion).multiply(0.8));
            p.remove();

        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent ev) {
        if (ev.getBow() == null || !ev.getBow().containsEnchantment(this) || !(ev.getEntity() instanceof Player)) return;
        cEnchants.get().getLogger().info("Hello from Ricochet shoot");
        ev.getProjectile().setMetadata("ricochet", cEnchants.getMetaValue(ev.getBow().clone()));
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
    public Rarity getRarity() {
        return Rarity.RARE;
    }

    @Override
    public BaseComponent[] getDescription() {
        return new ComponentBuilder().append("Ricochets your arrows from entities!").create();
    }

    @Override
    public @NotNull String getName() {
        return "Ricochet";
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
}
