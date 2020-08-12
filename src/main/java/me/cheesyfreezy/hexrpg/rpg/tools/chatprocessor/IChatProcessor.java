package me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor;

import org.bukkit.entity.Player;

public interface IChatProcessor {
	public void init(Player player);
	public void process(Player player, String message);
}