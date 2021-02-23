package me.cheesyfreezy.hexrpg.listeners.inventory;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.rpg.mechanics.backpack.BackPackInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import me.cheesyfreezy.hexrpg.rpg.mechanics.CustomInventory;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.menus.PlayerMenuTrade;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.trading.PlayerTradingService;

public class OnCustomInventoryClose implements Listener {
	@Inject private PlayerTradingService playerTradingService;

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		Inventory inv = event.getInventory();
		
		if(CustomInventory.hasCache(inv)) {
			CustomInventory invObj = (CustomInventory) CustomInventory.getInventoryObject(inv);
			if(invObj instanceof PlayerMenuTrade) {
				PlayerMenuTrade pmt = (PlayerMenuTrade) invObj;

				playerTradingService.closeTrade(player, pmt, true);
				
				return;
			} else if(invObj instanceof BackPackInventory) {
				BackPackInventory backPackInventory = (BackPackInventory) invObj;
				backPackInventory.save(event.getView().getTopInventory(), player);
			}
			
			CustomInventory.removeFromCache(inv);
		}
	}
}