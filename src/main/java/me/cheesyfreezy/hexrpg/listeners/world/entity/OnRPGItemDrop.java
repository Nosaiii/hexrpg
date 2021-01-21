package me.cheesyfreezy.hexrpg.listeners.world.entity;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class OnRPGItemDrop implements Listener {
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Item item = event.getItemDrop();
		
		NBTItem nbtItem = new NBTItem(item.getItemStack());
		if(!nbtItem.hasKey("rpgdata_combatitem")) {
			return;
		}
		//RPGCombatItem rpgItem = nbtItem.getObject("rpgdata_combatitem", RPGCombatItem.class);
		
		if(item.getItemStack().hasItemMeta() && item.getItemStack().getItemMeta().hasDisplayName()) {
			item.setCustomName(item.getItemStack().getItemMeta().getDisplayName());
			item.setCustomNameVisible(true);
		}
	}
}