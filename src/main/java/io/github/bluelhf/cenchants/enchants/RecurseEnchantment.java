package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.utilities.ProjectileUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class RecurseEnchantment extends CEnchantment implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent ev) {
        if (!ev.getBow().containsEnchantment(this) || !(ev.getEntity() instanceof Player)) return;
        Player shooter = (Player)ev.getEntity();
        ev.getProjectile().setMetadata("recursion_data", cEnchants.getMetaValue(new RecursionData(shooter, ev.getBow().getEnchantmentLevel(this))));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLand(ProjectileHitEvent ev) {
        if (cEnchants.getMetadata(ev.getEntity(), "recursion_data").isPresent()) {
            RecursionData data = (RecursionData) cEnchants.getMetadata(ev.getEntity(), "recursion_data").get().value();
            data = data.clone();
            if (data.depth >= data.level) {
                ev.getEntity().remove();
                return;
            }
            data.depth++;
            int max = data.level+2;
            for(int i = 0; i < max; i++) {
                Vector shotDirection = new Vector(0.2, 1, 0);
                double angle = (i/(double)max)*Math.PI*2;
                shotDirection.rotateAroundY(angle);
                Projectile proj = ProjectileUtil.shootCopy(ev, shotDirection.multiply(ev.getEntity().getVelocity().length()));
                proj.setMetadata("recursion_data", cEnchants.getMetaValue(data));
            }
            ev.getEntity().removeMetadata("recursion_data", cEnchants.get());
            ev.getEntity().remove();

        }
    }

    @Override
    public BaseComponent[] getDescription() {
        return new ComponentBuilder()
            .append("Recursion ").bold(true)
            .append("is like mitosis but arrows!").reset()
            .create();
    }

    public RecurseEnchantment(String key) {
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
        return Rarity.LEGENDARY;
    }

    @Override
    public @NotNull String getName() {
        return "Recursion";
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

    class RecursionData implements Cloneable {
        Player shooter;
        int level = 1;
        int depth = 0;

        public RecursionData(Player shooter, int level) {
            this.shooter = shooter;
            this.level = level;
        }
        public RecursionData(RecursionData data) {
            this.shooter = data.shooter;
            this.depth = data.depth;
        }

        public int getLevel() {
            return level;
        }

        public int getDepth() {
            return depth;
        }

        public void incrementDepth() {
            depth++;
        }

        public Player getShooter() {
            return shooter;
        }

        public RecursionData clone() {
            try {
                return (RecursionData) super.clone();
            } catch (CloneNotSupportedException e) {
                return new RecursionData(this);
            }
        }

        @Override
        public String toString() {
            return "RecursionData{" +
                    "shooter=" + shooter +
                    ", level=" + level +
                    ", depth=" + depth +
                    '}';
        }
    }
}
