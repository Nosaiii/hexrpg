package me.cheesyfreezy.hexrpg.listeners.inventory;

import java.util.Arrays;

import me.cheesyfreezy.hexrpg.rpg.tools.Feature;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.cheesyfreezy.hexrpg.rpg.mechanics.CustomInventory;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.PlayerMenuInventory;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.menus.PlayerMenuTrade;

public class OnPlayerMenuClick implements Listener {
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		
		if(!CustomInventory.hasCache(event.getInventory()) || !(CustomInventory.getInventoryObject(event.getInventory()) instanceof PlayerMenuInventory)) {
			return;
		}
		PlayerMenuInventory pmi = (PlayerMenuInventory) CustomInventory.getInventoryObject(event.getInventory());
		
		if(pmi instanceof PlayerMenuTrade) {
			handlePlayerTradingMenu(event, (PlayerMenuTrade) pmi);
		}
		
		event.setCancelled(true);
		
		int slot = event.getSlot();
		if(pmi.hasOption(slot)) {
			pmi.getOption(slot).callEvent(player);
		}
	}
	
	private void handlePlayerTradingMenu(InventoryClickEvent event, PlayerMenuTrade pmt) {
		if(event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) {
			return;
		}
		
		Player player = (Player) event.getWhoClicked();
		
		if(event.getView().getBottomInventory().equals(event.getClickedInventory())) {
			pmt.addItem(player, event.getCurrentItem(), event.getSlot());
		} else {
			int playerIndex = pmt.getTradingPlayers()[0].getPlayer() == player ? 0 : 1;
			if(Arrays.stream(PlayerMenuTrade.TRADING_SLOTS[playerIndex]).anyMatch(i -> i == event.getSlot())) {
				pmt.removeItem(player, event.getSlot());
			}
		}
	}
}