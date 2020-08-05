package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.utilities.ShapeUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class HomingEnchantment extends CEnchantment implements Listener {
    public HomingEnchantment(String key) {
        super(key);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent event) {
        if (!event.getBow().containsEnchantment(this)) return;
        Projectile p = (Projectile) event.getProjectile();
        int level = event.getBow().getEnchantmentLevel(this);
        int maxDist = 2 * level;

        final LivingEntity[] target = {null};
        long start = System.currentTimeMillis();
        final AtomicInteger ticks = new AtomicInteger(0);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (ticks.incrementAndGet() > 400 || p.isOnGround() || !p.isValid()) {
                    if (p.isValid()) p.remove();
                    this.cancel();
                    return;
                }

                boolean missingTarget = target[0] == null || !target[0].isValid();
                if (event.getEntity() instanceof Player) {
                   ((Player) event.getEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder()
                            .append(missingTarget ? "Searching for target." : "Target locked.")
                            .color(missingTarget ? ChatColor.YELLOW : ChatColor.RED)
                            .create());
                }



                if (missingTarget) {
                    // Obstacle avoidance

                    LivingEntity potentialTarget = p.getWorld().getNearbyEntities(p.getLocation(), maxDist, maxDist + 1.5, maxDist).stream()
                            .filter(e -> e instanceof LivingEntity)
                            .map(e -> (LivingEntity) e)
                            .filter(e -> e != event.getEntity())
                            .filter(e -> e != p)
                            .min((a, b) -> (int) (a.getLocation().distanceSquared(p.getLocation()) - b.getLocation().distanceSquared(p.getLocation())))
                            .orElse(null);
                    if (potentialTarget != null) {
                        if (potentialTarget.hasLineOfSight(p)) {
                            target[0] = potentialTarget;
                            if (event.getEntity() instanceof Player) {
                                Player p = (Player) event.getEntity();
                                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 2F);
                            }
                        }
                    }
                } else {
                    Location targetLocation = target[0].getLocation().add(0, target[0].getHeight() / 2, 0);
                    if (event.getEntity() instanceof Player) {
                        Player pl = (Player) event.getEntity();
                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BOAT_PADDLE_LAND, 2, 0);

                        Location l0 = target[0].getBoundingBox().getMin().toLocation(target[0].getWorld());
                        Location l1 = target[0].getBoundingBox().getMax().toLocation(target[0].getWorld());
                        for (Location loc : ShapeUtil.wireframe(l0, l1, 0.3)) {
                            pl.spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 0.3F));
                        }
                    }
                    Vector toTarget = targetLocation.toVector().subtract(p.getLocation().toVector());
                    double dist = p.getLocation().distance(targetLocation);
                    toTarget.add(p.getVelocity().clone().multiply(5 * (dist / maxDist)));
                    p.setVelocity(p.getVelocity().clone().add(toTarget).normalize().multiply(p.getVelocity().length()));

                    if ((p.getLocation().distance(targetLocation) < 0.5 || !p.isValid()) && event.getEntity() instanceof Player) {
                        ((Player) event.getEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("Hit!").color(ChatColor.GREEN).bold(true).create());
                        this.cancel();
                        return;
                    }
                }

            }
        }.runTaskTimer(cEnchants.get(), 1, 1);
    }

    private double signedCeil(double y) {
        if (y < 0) return Math.floor(y);
        else return Math.ceil(y);
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
        return "Homing";
    }

    @Override
    public int getMaxLevel() {
        return 3;
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
        return Rarity.LEGENDARY;
    }
}
