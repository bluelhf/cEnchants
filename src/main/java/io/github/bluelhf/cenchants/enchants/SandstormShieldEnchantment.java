package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class SandstormShieldEnchantment extends CEnchantment implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onBlock(PlayerInteractEvent ev) {
        if (ev.getItem() == null) return;
        if (!ev.getItem().containsEnchantment(this)) return;
        if (ev.getItem().getType() != Material.SHIELD) return;
        int level = ev.getItem().getEnchantmentLevel(this);
        new BukkitRunnable() {@Override public void run() {

            final double[] theta = new double[]{0};
            final double[] y = new double[]{0};
            final int[] dir = new int[]{1};
            final int r = level;
            new BukkitRunnable() {@Override public void run() {
                if (!ev.getPlayer().isBlocking()) {
                    this.cancel();
                    return;
                }
                if (ev.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.SAND) return;

                Location loc = ev.getPlayer().getLocation();
                loc.add(r * Math.sin(theta[0]), y[0], r * Math.cos(theta[0]));
                loc.getWorld().spawnParticle(Particle.FALLING_DUST, loc, 1, 0, 0, 0, 0, Bukkit.createBlockData("minecraft:sand"));
                Location loc2 = ev.getPlayer().getLocation();
                loc2.add(r * Math.sin(theta[0]+Math.PI), ev.getPlayer().getHeight(), r * Math.cos(theta[0]+Math.PI));
                loc2.getWorld().spawnParticle(Particle.FALLING_DUST, loc2, 1, 0, 0, 0, 0, Bukkit.createBlockData("minecraft:sand"));


                for (Entity e : loc.getWorld().getNearbyEntities(loc, r, r, r, e -> (e.getLocation().distance(loc) < r && e != ev.getPlayer()))) {
                    Vector toE = e.getLocation().toVector().subtract(ev.getPlayer().getLocation().toVector());
                    e.setVelocity(e.getVelocity().add(toE.normalize().multiply(0.3*level)));
                }


                y[0] += dir[0]*0.09;
                theta[0] += Math.PI*2/10;
                if (y[0] > ev.getPlayer().getHeight() || y[0] < 0) dir[0] *= -1;
                if (theta[0] > Math.PI*2) theta[0] = 0;
            }}.runTaskTimer(cEnchants.get(), 0, 1);
        }}.runTaskLater(cEnchants.get(), 10);
    }



    public SandstormShieldEnchantment(NamespacedKey key) {
        super(key);
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
        return Rarity.EPIC;
    }

    @Override
    public @NotNull String getName() {
        return "Sandstorm Shield";
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
        return EnchantmentTarget.TOOL;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        if (item.getType() == Material.AIR) return false;
        if (item.getType() != Material.SHIELD) return false;
        if (item.getEnchantments().keySet().stream().anyMatch(e -> this.conflictsWith(e) && e.getKey() != this.getKey())) return false;
        return true;
    }

    @Override
    public BaseComponent[] getDescription() {
        return new ComponentBuilder()
            .append("Sandstorm Shield ").bold(true)
            .append("blocks anything from\napproaching you when blocking\nwith your shield on sand.").reset()
            .create();
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
