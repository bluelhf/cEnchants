package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class NightWingsEnchantment extends CEnchantment {
    private BukkitTask tickingTask;
    public NightWingsEnchantment(NamespacedKey key) {
        super(key);
    }

    @Override
    public void onRegister() {
        if (tickingTask != null && !tickingTask.isCancelled())
            tickingTask.cancel();

        NightWingsEnchantment instance = this;
        tickingTask = new BukkitRunnable() {@Override public void run() {
            Bukkit.getOnlinePlayers().forEach((p) -> {
                if (p.isGliding() && p.getEquipment() != null && p.getEquipment().getChestplate() != null && p.getEquipment().getChestplate().containsEnchantment(instance)) {
                    if (getPower(p.getWorld()) == 0) return;
                    p.setVelocity(p.getLocation().getDirection().multiply(p.getVelocity().length() + getPower(p.getWorld()) / 10000D));
                }
            });
        }}.runTaskTimer(cEnchants.get(), 0, 2);
    }

    @Override
    public void onUnregister() {
        if (tickingTask != null && !tickingTask.isCancelled())
            tickingTask.cancel();

    }

    @Override
    public Rarity getRarity() {
        return Rarity.EPIC;
    }

    @Override
    public BaseComponent[] getDescription() {
        return new ComponentBuilder("Night Wings").bold(true).append("  strengthens wings at night!").create();
    }

    @Override
    public @NotNull String getName() {
        return "Night Wings";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ARMOR_TORSO;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        if (item.getType() == Material.AIR) return false;
        if (item.getType() != Material.ELYTRA) return false;
        if (item.getEnchantments().keySet().stream().anyMatch(e -> this.conflictsWith(e) && e.getKey() != this.getKey())) return false;
        return true;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    private double getPower(World world) {
        long ticks = world.getTime() % 24000;
        return getPower(ticks);
    }

    private double getPower(long ticks) {
        if (ticks < 13500) return 0;
        if (ticks > 23000) return 0;

        double min;
        double max;
        boolean invert = false;
        if (ticks < 18000) {
            min = 13500;
            max = 18000;
        } else {
            min = 18000;
            max = 23000;
            invert = true;
        }

        double mu = (ticks - min) / (max - min);
        if (invert) mu = 1 - mu;
        double cosTicks = cosLerp(min, max, mu);
        return ((1 / (max - min)) * (cosTicks - min));
    }

    private double cosLerp(double a, double b, double mu) {
        double mu2 = (1-Math.cos(mu*Math.PI))/2D;
        return (a*(1-mu2)+b*mu2);
    }
}
