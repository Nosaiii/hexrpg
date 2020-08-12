package me.cheesyfreezy.hexrpg.rpg.mechanics.playershop;

import org.bukkit.inventory.ItemStack;

public class SoldItem {
	private ItemStack item;
	private int price;
	
	public SoldItem(ItemStack item, int price) {
		this.item = item;
		this.price = price;
	}

	/**
	 * @return the item
	 */
	public ItemStack getItem() {
		return item;
	}

	/**
	 * @return the price
	 */
	public int getPrice() {
		return price;
	}
}