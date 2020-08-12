package me.cheesyfreezy.hexrpg.rpg.mechanics.playershop;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.cheesyfreezy.hexrpg.rpg.mechanics.CustomInventory;

public class PlayerShopInventoryUpdater {
	private int taskId;
	private Player player;
	
	public PlayerShopInventoryUpdater(Player player) {
		this.player = player;
	}
	
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	
	public Runnable getRunnable() {
		return () -> {
			if(player.getOpenInventory().getTopInventory() != null) {
				return;
			}
			Inventory inv = player.getOpenInventory().getTopInventory();
			
			if(!player.isOnline() || !CustomInventory.hasCache(inv)) {
				Bukkit.getServer().getScheduler().cancelTask(taskId);
				return;
			}
			
			Object inventoryObject = CustomInventory.getInventoryObject(inv);
			if(inventoryObject instanceof PlayerShop) {
				return;
			}
			
			PlayerShop shop = (PlayerShop) inventoryObject;
			shop.update(player);
		};
	}
}