package io.github.bluelhf.cenchants;

import com.moderocky.mask.template.BukkitPlugin;
import io.github.bluelhf.cenchants.commands.CEnchantCommand;
import io.github.bluelhf.cenchants.enchants.*;
import io.github.bluelhf.cenchants.listeners.AnvilListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class cEnchants extends BukkitPlugin {

    private BukkitTask tickerTask;


    @Override
    public void startup() {

        // Register native enchantments
        getLogger().info("Registering cEnchants enchantments");

        //EnchantmentWrapper.register(new HealingEnchantment("healing"));
        //EnchantmentWrapper.register(new PoisoningEnchantment("poisoning"));
        //EnchantmentWrapper.register(new FlingEnchantment("fling"));

        CEnchantment.register(new FlamingAuraEnchantment        (getNamespacedKey("flaming_aura"          )));
        CEnchantment.register(new LifeStealEnchantment          (getNamespacedKey("life_steal"            )));
        CEnchantment.register(new HomingEnchantment             (getNamespacedKey("homing"                )));
        CEnchantment.register(new MultiquiverEnchantment        (getNamespacedKey("multiquiver"           )));
        CEnchantment.register(new FrostTouchEnchantment         (getNamespacedKey("frost_touch"           )));
        CEnchantment.register(new StormArrowEnchantment         (getNamespacedKey("storm_arrow"           )));
        CEnchantment.register(new VitalityEnchantment           (getNamespacedKey("vitality"              )));
        CEnchantment.register(new AvianRageEnchantment          (getNamespacedKey("avian_rage"            )));
        CEnchantment.register(new BaneOfTheSeaEnchantment       (getNamespacedKey("bane_of_the_sea"       )));
        CEnchantment.register(new BaneOfTheIllagersEnchantment  (getNamespacedKey("bane_of_the_illagers"  )));
        CEnchantment.register(new RecurseEnchantment            (getNamespacedKey("recursion"             )));
        CEnchantment.register(new SandstormShieldEnchantment    (getNamespacedKey("sandstorm_shield"      )));
        CEnchantment.register(new BlastOfTheAncientsEnchantment (getNamespacedKey("blast_of_the_ancients" )));
        CEnchantment.register(new BlessingOfTheDesertEnchantment(getNamespacedKey("blessing_of_the_desert")));
        CEnchantment.register(new ArrowMaelstromEnchantment     (getNamespacedKey("arrow_maelstrom"       )));
        CEnchantment.register(new ShieldOfFeathersEnchantment   (getNamespacedKey("shield_of_feathers"    )));
        CEnchantment.register(new ExplodingArrowsEnchantment    (getNamespacedKey("exploding_arrows"      )));
        CEnchantment.register(new RicochetEnchantment           (getNamespacedKey("ricochet"              )));
        CEnchantment.register(new GreenFingerEnchantment        (getNamespacedKey("green_finger"          )));
        CEnchantment.register(new StompingEnchantment           (getNamespacedKey("stomping"              )));

        // Start ticker task
        getLogger().info("Starting ticking task");
        tickerTask = new BukkitRunnable() {
            @Override
            public void run() {
                // Tick enchants
                for (Player p : Bukkit.getOnlinePlayers()) {
                    CEnchantment.getEnchantments().forEach(e -> e.doTick(p));
                }

            }
        }.runTaskTimer(get(), 20, 20);
    }

    /**
     * @param m The metadatable to find the metadata value in
     * @param s The key of the metadata value to find
     * @return The first metadata value of <i>m</i> to have the key <i>s</i> and be owned by this plugin, as an {@link java.util.Optional}
     * */
    public static Optional<MetadataValue> getMetadata(Metadatable m, String s) {
        return m.getMetadata(s).stream().filter(meta -> meta.getOwningPlugin() == cEnchants.get()).findFirst();
    }

    @Override
    protected void registerCommands() {
        getLogger().info("Registering commands");
        register(new CEnchantCommand());
    }

    @Override
    protected void registerListeners() {
        register(new AnvilListener());
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
        if (tickerTask != null) tickerTask.cancel();
    }

    public static cEnchants get() {
        return JavaPlugin.getPlugin(cEnchants.class);
    }
}
