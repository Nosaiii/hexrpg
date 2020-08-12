package me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.trading;

import org.bukkit.entity.Player;

public class TradingPlayer {
	private Player player;
	private boolean accepted;
	
	public TradingPlayer(Player player) {
		this.player = player;
		accepted = false;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public boolean hasAccepted() {
		return accepted;
	}
	
	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
}