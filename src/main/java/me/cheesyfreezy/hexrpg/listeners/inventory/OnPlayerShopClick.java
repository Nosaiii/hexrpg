package me.cheesyfreezy.hexrpg.listeners.inventory;

import com.google.inject.Inject;
import de.tr7zw.changeme.nbtapi.NBTItem;
import me.cheesyfreezy.hexrpg.rpg.mechanics.CustomInventory;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playershop.PlayerShop;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playershop.SoldItem;
import me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.ChatProcessorService;
import me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.processors.PlayerShopAddItemChatProcessor;
import me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.processors.PlayerShopConfirmItemRemoval;
import me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.processors.PlayerShopDeleteConfirmationChatProcessor;
import me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.processors.PlayerShopRenamingChatProcessor;
import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class OnPlayerShopClick implements Listener {
	@Inject private Economy economy;
	@Inject private ChatProcessorService chatProcessorService;

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		
		if(!CustomInventory.hasCache(event.getInventory()) || !(CustomInventory.getInventoryObject(event.getInventory()) instanceof PlayerShop)) {
			return;
		}

		PlayerShop shop = (PlayerShop) CustomInventory.getInventoryObject(event.getInventory());
		
		event.setCancelled(true);
		
		if(event.getView().getTopInventory().equals(event.getClickedInventory())) {
			int managingButtonsOffset = shop.getShopRows() * 9 + 9;
			
			// Sold items section
			if(event.getSlot() >= 0 && event.getSlot() < shop.getShopRows() * 9) {
				SoldItem clickedSoldItem = shop.getSoldItem(event.getSlot());
				if(clickedSoldItem != null && clickedSoldItem.getItem() != null && !clickedSoldItem.getItem().getType().equals(Material.AIR)) {
					if(!shop.isOwner(player)) {
						int price = clickedSoldItem.getPrice();
						int balance = (int) Math.floor(economy.getBalance(player));

						if(price <= balance) {
							if(!shop.isEditing()) {
								if(!shop.getUUID().equals(player.getUniqueId())) {
									player.sendMessage(LanguageManager.getMessage("personal-shop.item-purchased", player.getUniqueId(), true));
									player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
								}

								economy.withdrawPlayer(player, price);
							}
							
							shop.sellSoldItem(clickedSoldItem, event.getSlot(), player, shop.isOwner(player));
						} else {
							player.sendMessage(LanguageManager.getMessage("personal-shop.not-enough-rupees-to-purchase-item", player.getUniqueId(), true));
						}
					} else {
						chatProcessorService.setStatus(player, new PlayerShopConfirmItemRemoval(shop, event.getSlot()));
					}

					player.closeInventory();
				}
			}
			// Managing section
			else if(event.getSlot() >= managingButtonsOffset && event.getSlot() < managingButtonsOffset + 9 && event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR)) {
				if(event.getSlot() == shop.getRenameOptionSlot()) {
					player.closeInventory();
					chatProcessorService.setStatus(player, new PlayerShopRenamingChatProcessor(shop));
				} else if(event.getSlot() == shop.getDeleteOptionSlot()) {
					player.closeInventory();
					chatProcessorService.setStatus(player, new PlayerShopDeleteConfirmationChatProcessor(shop));
				} else if(event.getSlot() == shop.getUpgradeShopSizeSlot()) {
					player.closeInventory();

					int upgradeCost = ConfigFile.getConfig("config.yml").getInteger("player-shop-settings.upgrade-slots-cost");
					int balance = (int) Math.floor(economy.getBalance(player));

					if(upgradeCost <= balance) {
						economy.withdrawPlayer(player, upgradeCost);
						shop.upgradeSlots(player);
					} else {
						player.sendMessage(LanguageManager.getMessage("personal-shop.not-enough-rupees-to-upgrade-slots", player.getUniqueId(), true));
					}
				} else if(event.getSlot() == shop.getCollectRupeesOptionSlot()) {
					player.closeInventory();
					
					int storedBalance = shop.getStoredRupees();
					
					if(storedBalance > 0) {
						economy.depositPlayer(player, storedBalance);

						player.sendMessage(ChatColor.GREEN + "+" + storedBalance + " " + shop.getPriceLabel(player, storedBalance));
						player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
						
						shop.setStoredRupees(0);
					} else {
						player.sendMessage(ChatColor.RED + LanguageManager.getMessage("personal-shop.no-rupees-to-collect", player.getUniqueId()));
					}
				}
			}
		} else {
			if(shop.isEditing()) {
				if(event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR)) {
					player.closeInventory();
					
					NBTItem nbtItem = new NBTItem(event.getCurrentItem());
					if(nbtItem.hasKey("rpgdata_rupee")) {
						player.sendMessage(LanguageManager.getMessage("personal-shop.selling-rupees-not-allowed", player.getUniqueId(), true));
						return;
					}
					
					chatProcessorService.setStatus(player, new PlayerShopAddItemChatProcessor(shop, event.getSlot()));
				}
			}
		}
	}
}