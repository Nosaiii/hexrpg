package me.cheesyfreezy.hexrpg.listeners.world.entity;

import com.google.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.stealing.PlayerStealingService;

public class OnStealingStop implements Listener {
	@Inject private PlayerStealingService playerStealingService;

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		stopStealProcess(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		stopStealProcess(event.getPlayer());
	}

	private void stopStealProcess(Player player) {
		if(playerStealingService.isStealing(player)) {
			playerStealingService.getStealProcess(player).stop(true);
		} else if(playerStealingService.isBeingStolenFrom(player)) {
			playerStealingService.getStealProcess(playerStealingService.getStealerByVictim(player)).stop(true);
		}
	}
}