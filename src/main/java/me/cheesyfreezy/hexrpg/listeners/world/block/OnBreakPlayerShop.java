package me.cheesyfreezy.hexrpg.listeners.world.block;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.cheesyfreezy.hexrpg.rpg.mechanics.playershop.PlayerShop;

public class OnBreakPlayerShop implements Listener {
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Location blockLocation = event.getBlock().getLocation();
		
		if(PlayerShop.getPlayerShop(blockLocation) == null) {
			return;
		}
		
		event.setCancelled(true);
	}
}