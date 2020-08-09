package io.github.bluelhf.cenchants.enchants;

import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.utilities.EnchantUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class ArrowMaelstromEnchantment extends CEnchantment implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent ev) {
        if (!(ev.getEntity() instanceof Player)) return;
        Player pl = (Player) ev.getEntity();
        if (pl.getHealth()-ev.getFinalDamage() > 6) return;
        if (EnchantUtil.getEnchantment(pl, this) == null) return;
        int level = EnchantUtil.getEnchantment(pl, this).getEnchantmentLevel(this);

        AtomicBoolean isAvailable = new AtomicBoolean(false);
        cEnchants.getMetadata(pl, "arrowmaelstrom").ifPresentOrElse(value -> {
            if (value.asInt() == 0) isAvailable.set(true);
        }, () -> {
            isAvailable.set(true);
        });
        if (!isAvailable.get()) return;
        final double rps = Math.PI * 2 / 10;
        final Vector v = new Vector(0, 0, -1);
        long start = System.currentTimeMillis();
        pl.setMetadata("arrowmaelstrom", cEnchants.getMetaValue(2));
        new BukkitRunnable() {@Override public void run() {
            if (System.currentTimeMillis() > start + 4000*level) {
                this.cancel();
                pl.setMetadata("arrowmaelstrom", cEnchants.getMetaValue(pl.getHealth() < pl.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() ? 1 : 0));
                return;
            }
            int max = 4+level;
            for(int i = 0; i < max; i++) {
                v.rotateAroundY(Math.PI * 2 / max);
                AbstractArrow proj = pl.launchProjectile(Arrow.class, v.clone().multiply(level));
                proj.setKnockbackStrength(level);
                proj.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                proj.setMetadata("maelstrom_arrow", cEnchants.getMetaValue(true));
            }
            v.rotateAroundY(Math.PI / 18);


            pl.getLocation().getWorld().playSound(pl.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 1, 0.8F);
        }}.runTaskTimer(cEnchants.get(), 0, 1);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLand(ProjectileHitEvent ev) {
        if (cEnchants.getMetadata(ev.getEntity(), "maelstrom_arrow").isPresent()) ev.getEntity().remove();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHeal(EntityRegainHealthEvent ev) {
        if (!(ev.getEntity() instanceof Player)) return;
        Player pl = (Player) ev.getEntity();
        if (pl.getHealth()+ev.getAmount() >= pl.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() && cEnchants.getMetadata(pl, "arrowmaelstrom").isPresent()) {
            pl.setMetadata("arrowmaelstrom", cEnchants.getMetaValue(0));
        }
    }

    public ArrowMaelstromEnchantment(String key) {
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
    public BaseComponent[] getDescription() {
        return new ComponentBuilder().append("Summons spirals of arrows when your hearts go below 3.").create();
    }

    @Override
    public @NotNull String getName() {
        return "Arrow Maelstrom";
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
        return EnchantmentTarget.ARMOR_TORSO;
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
    public void doTick(Player p) {
        if (EnchantUtil.getEnchantment(p, this) != null) {
            ComponentBuilder builder = new ComponentBuilder();
            builder.append("✷ ").color(ChatColor.YELLOW).bold(true);
            cEnchants.getMetadata(p, "arrowmaelstrom").ifPresentOrElse(value -> {
                switch (value.asInt()) {
                    case 0:
                        builder.append("AVAILABLE").reset().color(ChatColor.YELLOW);
                        break;
                    case 1:
                        builder.append("UNAVAILABLE").color(ChatColor.GRAY);
                        break;
                    case 2:
                        builder.append("IN USE").color(ChatColor.YELLOW).bold(true);
                        break;
                }
            }, () -> {
                builder.append("AVAILABLE").reset().color(ChatColor.YELLOW);
            });
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, builder.create());
        }
    }
}
