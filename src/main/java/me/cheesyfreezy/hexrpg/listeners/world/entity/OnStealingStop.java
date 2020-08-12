package me.cheesyfreezy.hexrpg.listeners.world.entity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.cheesyfreezy.hexrpg.main.Plugin;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.stealing.PlayerStealingService;

public class OnStealingStop implements Listener {
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		PlayerStealingService pss = Plugin.getMain().getPlayerStealingService();
		
		if(pss.isStealing(player)) {
			pss.getStealProces(player).stop(true);
		} else if(pss.isBeingStolenFrom(player)) {
			pss.getStealProces(pss.getStealerByVictim(player)).stop(true);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerStealingService pss = Plugin.getMain().getPlayerStealingService();
		
		if(pss.isStealing(player)) {
			pss.getStealProces(player).stop(true);
		} else if(pss.isBeingStolenFrom(player)) {
			pss.getStealProces(pss.getStealerByVictim(player)).stop(true);
		}
	}
}