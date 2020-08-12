package me.cheesyfreezy.hexrpg.listeners.inventory;

import de.tr7zw.hexrpg.nbtapi.NBTItem;
import me.cheesyfreezy.hexrpg.main.Plugin;
import me.cheesyfreezy.hexrpg.rpg.items.other.Rupee;
import me.cheesyfreezy.hexrpg.rpg.mechanics.CustomInventory;
import me.cheesyfreezy.hexrpg.rpg.mechanics.languageselector.LanguageSelector;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playershop.PlayerShop;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playershop.SoldItem;
import me.cheesyfreezy.hexrpg.rpg.tools.RupeeTools;
import me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.processors.PlayerShopAddItemChatProcessor;
import me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.processors.PlayerShopConfirmItemRemoval;
import me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.processors.PlayerShopDeleteConfirmationChatProcessor;
import me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.processors.PlayerShopRenamingChatProcessor;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class OnLanguageSelectorClick implements Listener {
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		
		if(!CustomInventory.hasCache(event.getInventory()) || !(CustomInventory.getInventoryObject(event.getInventory()) instanceof LanguageSelector)) {
			return;
		}
		LanguageSelector languageSelector = (LanguageSelector) CustomInventory.getInventoryObject(event.getInventory());
		
		event.setCancelled(true);

		if(event.getView().getTopInventory().equals(event.getClickedInventory())) {
			int previousPageSlotOffset = LanguageSelector.PREVIOUS_PAGE_SLOT + (LanguageSelector.ROWS_PER_PAGE * 9);
			int nextPageSlotOffset = LanguageSelector.NEXT_PAGE_SLOT + (LanguageSelector.ROWS_PER_PAGE * 9);
			if(event.getSlot() == previousPageSlotOffset || event.getSlot() == nextPageSlotOffset) {
				int newPage = languageSelector.getCurrentPage();

				if(event.getSlot() == previousPageSlotOffset) {
					newPage--;
				} else if(event.getSlot() == nextPageSlotOffset) {
					newPage++;
				}

				int pageCount = languageSelector.getPageCount();

				if (newPage < 1) {
					newPage = 1;
				} else if (newPage > pageCount) {
					newPage = pageCount;
				}

				languageSelector.open(player, newPage);
			} else {
				if(event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) {
					return;
				}
				ItemStack clickedItem = event.getCurrentItem();

				NBTItem nbtItem = new NBTItem(clickedItem);

				if(!nbtItem.hasKey("hexrpg_language_file")) {
					return;
				}
				String languageFileName = nbtItem.getString("hexrpg_language_file");

				languageSelector.setSelectedLanguage(player.getUniqueId(), languageFileName);
				player.closeInventory();

				player.sendMessage(LanguageManager.getMessage("language-menu.language-changed", player.getUniqueId(), true, ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName())));
			}
		}
	}
}