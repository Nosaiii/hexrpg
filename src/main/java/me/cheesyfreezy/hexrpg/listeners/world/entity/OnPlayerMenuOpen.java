package me.cheesyfreezy.hexrpg.listeners.world.entity;

import me.cheesyfreezy.hexrpg.rpg.tools.Feature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.menus.PlayerMenuMain;

public class OnPlayerMenuOpen implements Listener {
	@EventHandler
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
		if(!Feature.getFeature("player-menu.trading").isEnabled() && !Feature.getFeature("player-menu.stealing").isEnabled()) {
			return;
		}

		Player player = event.getPlayer();

		if(!(event.getRightClicked() instanceof Player)) {
			return;
		}
		
		PlayerMenuMain pmm = new PlayerMenuMain(player, (Player) event.getRightClicked());
		pmm.open(player);
	}
}