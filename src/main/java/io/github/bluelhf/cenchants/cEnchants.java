package io.github.bluelhf.cenchants;

import com.moderocky.mask.template.BukkitPlugin;
import io.github.bluelhf.cenchants.commands.EnchantsCommand;
import io.github.bluelhf.cenchants.enchants.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class cEnchants extends BukkitPlugin {

    private BukkitTask tickerTask;


    @Override
    public void startup() {

        // Register native enchantments
        getLogger().info("Registering cEnchants enchantments");

        //EnchantmentWrapper.register(new HealingEnchantment("healing"));
        //EnchantmentWrapper.register(new PoisoningEnchantment("poisoning"));
        //EnchantmentWrapper.register(new FlingEnchantment("fling"));

        CEnchantment.register(new FlamingAuraEnchantment("flaming_aura"));
        CEnchantment.register(new LifeStealEnchantment("life_steal"));
        CEnchantment.register(new HomingEnchantment("homing"));
        CEnchantment.register(new MultiquiverEnchantment("multiquiver"));
        CEnchantment.register(new FrostTouchEnchantment("frost_touch"));
        CEnchantment.register(new StormArrowEnchantment("storm_arrow"));
        CEnchantment.register(new VitalityEnchantment("vitality"));
        CEnchantment.register(new AvianRageEnchantment("avian_rage"));
        CEnchantment.register(new BaneOfTheSeaEnchantment("bane_of_the_sea"));
        CEnchantment.register(new BaneOfTheIllagersEnchantment("bane_of_the_illagers"));

        // Start ticker task
        getLogger().info("Starting enchant ticking task");
        tickerTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    CEnchantment.getEnchantments().forEach(e -> e.doTick(p));
                }
            }
        }.runTaskTimer(get(), 20, 20);
    }

    @Override
    protected void registerCommands() {
        getLogger().info("Registering commands");
        register(new EnchantsCommand());
    }

    @Override
    public void disable() {
        List<String> lines = new ArrayList<>();
        lines.add("Unregistering enchantments...");
        int registered = CEnchantment.getEnchantments().size();
        int unregistered = 0;
        for (CEnchantment e : new ArrayList<>(CEnchantment.getEnchantments()) /* prevent ConcurrentModificationException */ ) {
            CEnchantment.unregister(e);
            lines.add("  Unregistered " + e.getKey().toString());
            unregistered++;
        }
        if (unregistered == registered) {
            lines.add("Done! Unregistered " + unregistered + "/" + registered + " enchantments.");
        } else {
            lines.add("Failed to unregister " + (registered-unregistered) + "/" + registered + " enchantments!");
        }

        lines.forEach(s -> getLogger().info(s));
        tickerTask.cancel();
    }

    public static cEnchants get() {
        return JavaPlugin.getPlugin(cEnchants.class);
    }
}
