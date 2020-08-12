package me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerMenuOption {
	private ItemStack item;
	private Consumer<Player> consumer;
	private Predicate<Player> predicate;
	
	public PlayerMenuOption(ItemStack item) {
		this(item, null, null);
	}
	
	public PlayerMenuOption(ItemStack item, Consumer<Player> consumer) {
		this(item, consumer, null);
	}
	
	public PlayerMenuOption(ItemStack item, Predicate<Player> predicate) {
		this(item, null, predicate);
	}
	
	public PlayerMenuOption(ItemStack item, Consumer<Player> consumer, Predicate<Player> predicate) {
		this.item = item;
		this.consumer = consumer;
		this.predicate = predicate;
	}

	public ItemStack getItem() {
		return item;
	}

	public void callEvent(Player player) {
		if(consumer == null) {
			return;
		}
		
		if(predicate != null && !predicate.test(player)) {
			return;
		}
		
		consumer.accept(player);
	}
}