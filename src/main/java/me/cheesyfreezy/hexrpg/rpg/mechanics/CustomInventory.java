package me.cheesyfreezy.hexrpg.rpg.mechanics;

import java.util.HashMap;

import org.bukkit.inventory.Inventory;

public abstract class CustomInventory {
	private static HashMap<Inventory, Object> cache = new HashMap<Inventory, Object>();
	
	public static void removeFromCache(Inventory inventory) {
		cache.remove(inventory);
	}
	
	public static boolean hasCache(Inventory inventory) {
		return cache.containsKey(inventory);
	}
	
	public static void addToCache(Inventory inventory, Object inventoryObject) {
		cache.put(inventory, inventoryObject);
	}
	
	public static Object getInventoryObject(Inventory inventoryKey) {
		return cache.get(inventoryKey);
	}
}