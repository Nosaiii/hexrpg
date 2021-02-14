package me.cheesyfreezy.hexrpg.listeners.world.block;

import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class OnDisableFarmlandRemoval implements Listener {
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(!event.getAction().equals(Action.PHYSICAL)) {
			return;
		}
		
		if(event.getClickedBlock() == null) {
			return;
		}
		Block block = event.getClickedBlock();
		
		if(block.getType() == Material.FARMLAND) {
			if(!ConfigFile.getConfig("config.yml").getBoolean("world-settings.remove-farmland-on-step")) {
				event.setUseInteractedBlock(Result.DENY);
				event.setCancelled(true);
			}
		}
	}
}