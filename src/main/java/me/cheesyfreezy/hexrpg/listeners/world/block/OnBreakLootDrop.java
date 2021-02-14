package me.cheesyfreezy.hexrpg.listeners.world.block;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.cheesyfreezy.hexrpg.rpg.mechanics.lootdrop.LootDrop;

public class OnBreakLootDrop implements Listener {
	@Inject private HexRPGPlugin plugin;

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Location blockLocation = event.getBlock().getLocation();
		
		LootDrop ld = LootDrop.getLootDrop(plugin, blockLocation);
		if(ld == null) {
			return;
		}
		
		if(!ld.hasDropped()) {
			return;
		}
		
		event.setCancelled(true);

		Player player = event.getPlayer();
		player.sendMessage(LanguageManager.getMessage("loot-drop.right-click-to-open", player.getUniqueId(), true));
	}
}