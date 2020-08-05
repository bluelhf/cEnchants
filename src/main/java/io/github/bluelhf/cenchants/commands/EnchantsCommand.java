package io.github.bluelhf.cenchants.commands;

import com.moderocky.mask.api.MagicList;
import com.moderocky.mask.command.ArgInteger;
import com.moderocky.mask.command.ArgString;
import com.moderocky.mask.command.Commander;
import com.moderocky.mask.template.WrappedCommand;
import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.enchants.CEnchantment;
import io.github.bluelhf.cenchants.utilities.EnchantUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EnchantsCommand extends Commander<CommandSender> implements WrappedCommand {
    @Override
    protected CommandImpl create() {
        return command("cenchants")
                .arg("add", arg((sender, input) -> {
                    if (!(sender instanceof Player)) {
                        sender.spigot().sendMessage(new ComponentBuilder()
                                .append("Only players can add cEnchantments to their tools!")
                                .color(ChatColor.RED)
                                .create());
                        return;
                    }
                    Player p = (Player) sender;
                    String enchant = (String) input[0];
                    enchant = enchant.replaceAll(" ", "_");
                    CEnchantment enchantment = CEnchantment.findByKey(cEnchants.getNamespacedKey(enchant));
                    if (enchantment == null) {
                        sender.spigot().sendMessage(new ComponentBuilder()
                                .append("That enchantment doesn't exist")
                                .color(ChatColor.RED)
                                .create());
                        return;
                    }
                    if (p.getEquipment().getItemInMainHand().getType() == Material.AIR
                            || !enchantment.canEnchantItem(p.getEquipment().getItemInMainHand())) {
                        sender.spigot().sendMessage(new ComponentBuilder()
                                .append("This item can not be enchanted with " + EnchantUtil.getName(enchantment))
                                .color(ChatColor.RED)
                                .create());
                        return;
                    }

                    int oldLevel = p.getEquipment().getItemInMainHand().getEnchantmentLevel(enchantment);
                    int newLevel = Math.min(oldLevel + 1, enchantment.getMaxLevel());
                    // Find in lore

                    ItemMeta meta = p.getEquipment().getItemInMainHand().getItemMeta();
                    if (meta.hasLore()) {
                        List<String> newLore = EnchantUtil.removeLoreEntry(enchantment, meta.getLore());
                        newLore.add(0, "§r§7" + EnchantUtil.getLoreEntry(enchantment, newLevel));
                        meta.setLore(newLore);
                    } else {
                        meta.setLore(Arrays.asList("§r§7" + EnchantUtil.getLoreEntry(enchantment, newLevel)));
                    }
                    p.getEquipment().getItemInMainHand().setItemMeta(meta);
                    p.getEquipment().getItemInMainHand().addEnchantment(enchantment, newLevel);
                }, new ArgString().setLabel("enchant")))
                .arg("set", arg((sender, input) -> {
                        if (!(sender instanceof Player)) {
                            sender.spigot().sendMessage(new ComponentBuilder()
                                .append("Only players can add cEnchantments to their tools!")
                                .color(ChatColor.RED)
                                .create());
                            return;
                        }
                        Player p = (Player) sender;
                        String enchant = (String) input[0];
                        int level = (int) input[1];
                        enchant = enchant.replaceAll(" ", "_");
                        CEnchantment enchantment = CEnchantment.findByKey(cEnchants.getNamespacedKey(enchant));
                        if (enchantment == null) {
                            sender.spigot().sendMessage(new ComponentBuilder()
                                .append("That enchantment doesn't exist")
                                .color(ChatColor.RED)
                                .create());
                            return;
                        }

                        ItemMeta meta = p.getEquipment().getItemInMainHand().getItemMeta();
                        if (meta.hasLore()) {
                            List<String> newLore = EnchantUtil.removeLoreEntry(enchantment, meta.getLore());
                            newLore.add(0, "§r§7" + EnchantUtil.getLoreEntry(enchantment, level));
                            meta.setLore(newLore);
                        } else {
                            meta.setLore(Arrays.asList("§r§7" + EnchantUtil.getLoreEntry(enchantment, level)));
                        }
                        p.getEquipment().getItemInMainHand().setItemMeta(meta);
                        p.getEquipment().getItemInMainHand().addUnsafeEnchantment(enchantment, level);

                    }, new ArgString().setLabel("enchant"),
                       new ArgInteger().setLabel("level")));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        cEnchants.get().getLogger().info(args.length + " " + Arrays.toString(args));
        if (args.length == 1) return Arrays.asList("add", "set");
        if (args.length == 2 && args[0].equalsIgnoreCase("add")) return CEnchantment.getEnchantments().stream().map(s -> s.getKey().getKey()).collect(Collectors.toList());
        if (args.length == 2 && args[0].equalsIgnoreCase("set")) return CEnchantment.getEnchantments().stream().map(s -> s.getKey().getKey()).collect(Collectors.toList());
        if (args.length == 3 && args[0].equalsIgnoreCase("set")) return Arrays.asList("1", "2", "5");
        return Collections.emptyList();
    }


    @Override
    public CommandSingleAction<CommandSender> getDefault() {
        return sender -> {
            sender.spigot().sendMessage(new ComponentBuilder()
                    .append("This server is running cEnchants v" + cEnchants.get().getVersion())
                    .color(ChatColor.GREEN)
                    .create()
            );
        };
    }

    @Override
    public @NotNull String getUsage() {
        return "/cenchants";
    }

    @Override
    public @NotNull String getDescription() {
        return "Main command for cEnchants";
    }

    @Override
    public @Nullable String getPermission() {
        return "cenchants.use";
    }

    @Override
    public @Nullable String getPermissionMessage() {
        return "§cNo permission.";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        execute(commandSender, strings);
        return true;
    }
}
