package me.cheesyfreezy.hexrpg.rpg.items;

import org.bukkit.inventory.ItemStack;

public abstract class RPGItem {
	protected transient ItemStack tmpItem;
	
	protected abstract ItemStack update(ItemStack item);
	
	public ItemStack getTemporaryItem() {
		return tmpItem;
	}
}