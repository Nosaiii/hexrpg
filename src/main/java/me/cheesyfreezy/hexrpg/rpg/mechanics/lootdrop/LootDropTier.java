package me.cheesyfreezy.hexrpg.rpg.mechanics.lootdrop;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public enum LootDropTier {
	COMMON(ChatColor.WHITE, Color.WHITE, 1), RARE(ChatColor.GOLD, Color.ORANGE, 2), SUPERIOR(ChatColor.LIGHT_PURPLE, Color.PURPLE, 3);
	
	private final ChatColor color;
	private final Color fireworkColor;
	private final int rows;
	
	private LootDropTier(ChatColor color, Color fireworkColor, int rows) {
		this.color = color;
		this.fireworkColor = fireworkColor;
		this.rows = rows;
	}
	
	public ChatColor getColor() {
		return color;
	}
	
	public Color getFireworkColor() {
		return fireworkColor;
	}
	
	public int getRows() {
		return rows;
	}
}