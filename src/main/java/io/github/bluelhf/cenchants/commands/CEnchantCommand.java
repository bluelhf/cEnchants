package io.github.bluelhf.cenchants.commands;

import com.moderocky.mask.command.ArgInteger;
import com.moderocky.mask.command.ArgString;
import com.moderocky.mask.command.Commander;
import com.moderocky.mask.template.WrappedCommand;
import io.github.bluelhf.cenchants.cEnchants;
import io.github.bluelhf.cenchants.enchants.CEnchantment;
import io.github.bluelhf.cenchants.utilities.EnchantUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CEnchantCommand extends Commander<CommandSender> implements WrappedCommand {

    @Override
    protected CommandImpl create() {
        return command("cenchant")
                .arg((sender, input) -> {
                        String selector = (String) input[0];
                        String rawEnchantment = (String) input[1];
                        int level = (int) input[2];

                        boolean isForce = false;
                        if (input.length >= 4 && input[3] != null) {
                            String forceInput = (String)input[3];
                            isForce = forceInput.equalsIgnoreCase("-f"     )
                                   || forceInput.equalsIgnoreCase("/f"     )
                                   || forceInput.equalsIgnoreCase("-force" )
                                   || forceInput.equalsIgnoreCase("/force" )
                                   || forceInput.equalsIgnoreCase("--force")
                                   || forceInput.equalsIgnoreCase("--f"    );
                        }



                        // Get from selector
                        List<Entity> selected;
                        try {
                            selected = Bukkit.selectEntities(sender, selector);
                        } catch (IllegalArgumentException ex) {
                            sender.spigot().sendMessage(new ComponentBuilder()
                                    .append("Invalid selector!")
                                    .color(ChatColor.RED)
                                    .create());
                            return;
                        }
                        if (selected.stream().anyMatch(e -> !(e instanceof Player))) {
                            sender.spigot().sendMessage(new ComponentBuilder()
                                    .append("Selector can only include players!")
                                    .color(ChatColor.RED)
                                    .create());
                            return;
                        }
                        List<Player> selectedPlayers = selected.stream().map(e -> (Player)e).collect(Collectors.toList());

                        // Parse enchantment
                        String ench = rawEnchantment.replaceAll(" ", "_");
                        CEnchantment enchantment = CEnchantment.findByKey(cEnchants.getNamespacedKey(ench));
                        if (enchantment == null) {
                            sender.spigot().sendMessage(new ComponentBuilder()
                                    .append("That enchantment doesn't exist")
                                    .color(ChatColor.RED)
                                    .create());
                            return;
                        }

                        for (Player p : selectedPlayers) {
                            if (p.getEquipment().getItemInMainHand().getType() == Material.AIR
                                    || (!enchantment.canEnchantItem(p.getEquipment().getItemInMainHand()) && !isForce)) {
                                sender.spigot().sendMessage(new ComponentBuilder()
                                        .append(p.getName()).color(ChatColor.RED)
                                        .append(p.getName().endsWith("s") ? "' " : "'s ")

                                        .append("item can not be enchanted with ")
                                        .append(EnchantUtil.getName(enchantment))
                                        .create());
                                continue;
                            }


                            ItemMeta meta = p.getEquipment().getItemInMainHand().getItemMeta();
                            try {
                                meta = EnchantUtil.enchantItem(meta, enchantment, level, isForce);
                            } catch (IllegalArgumentException ex) {
                                sender.spigot().sendMessage(new ComponentBuilder()
                                    .append("Could not enchant ").color(ChatColor.GRAY)
                                    .append(p.getName())
                                    .append(p.getName().endsWith("s") ? "' " : "'s ")

                                    .append("tool with ")
                                    .append(EnchantUtil.getName(enchantment))
                                    .append(": ")
                                    .append(ex.getLocalizedMessage()).color(ChatColor.RED)
                                    .create());
                                continue;
                            }
                            p.getEquipment().getItemInMainHand().setItemMeta(meta);

                            sender.spigot().sendMessage(new ComponentBuilder()
                                    .append("Enchanted ").color(ChatColor.GREEN)
                                    .append(p.getName())
                                    .append(p.getName().endsWith("s") ? "' " : "'s ")
                                    .append("tool with ")

                                    .append(EnchantUtil.getName(enchantment))
                                    .create());
                        }
                    },  new ArgString().setLabel("selector"),
                        new ArgString().setLabel("enchantment"),
                        new ArgInteger().setLabel("level"),
                        new ArgString().setLabel("force").setRequired(false));
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            ArrayList<String> list = new ArrayList<>(Arrays.asList("@a", "@s", "@r", "@p"));
            list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
            return list;
        } else if (args.length == 2) {
            return CEnchantment.getEnchantments().stream().map(s -> s.getKey().getKey()).collect(Collectors.toList());
        } else if (args.length == 3) {
            return Arrays.asList("1", "2", "5");
        } else if (args.length == 4) {
            return Arrays.asList("-f", "/f", "-force", "/force", "--f", "--force");
        }
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
        return "cenchants.admin";
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
