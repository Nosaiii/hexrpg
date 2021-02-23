package me.cheesyfreezy.hexrpg.listeners.world.block;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.cheesyfreezy.hexrpg.rpg.mechanics.lootdrop.LootDrop;

public class OnOpenLootDrop implements Listener {
	@Inject private HexRPGPlugin plugin;

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
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
		
		Chest chest = (Chest) block.getState();
		LootDrop ld = LootDrop.getLootDrop(plugin, chest.getLocation());
		
		if(ld == null) {
			return;
		}
		
		if(!ld.hasDropped()) {
			return;
		}
		
		event.setCancelled(true);
		ld.loot();
	}
}