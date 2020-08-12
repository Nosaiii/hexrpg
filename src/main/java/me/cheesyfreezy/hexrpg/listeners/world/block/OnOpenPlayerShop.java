package me.cheesyfreezy.hexrpg.listeners.world.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.cheesyfreezy.hexrpg.rpg.mechanics.playershop.PlayerShop;

public class OnOpenPlayerShop implements Listener {
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		
		if(event.getClickedBlock() == null || event.getClickedBlock().getType().equals(Material.AIR)) {
			return;
		}
		Block block = event.getClickedBlock();
		
		if(!(block.getState() instanceof Chest)) {
			return;
		}
		
		PlayerShop shop = PlayerShop.getPlayerShop(block.getLocation());
		if(shop == null) {
			return;
		}
		
		event.setCancelled(true);
		shop.open(player, shop.isOwner(player) && player.isSneaking());
	}
}