package io.github.bluelhf.cenchants.enchants;

import org.bukkit.ChatColor;

public enum Rarity {
    UNCOMMON(ChatColor.DARK_GRAY),
    RARE(ChatColor.BLUE),
    EPIC(ChatColor.DARK_PURPLE),
    LEGENDARY(ChatColor.GOLD),
    CURSED(ChatColor.RED);

    ChatColor colour;
    Rarity(ChatColor colour) {
        this.colour = colour;
    }

    public ChatColor getColour() {
        return colour;
    }
}
