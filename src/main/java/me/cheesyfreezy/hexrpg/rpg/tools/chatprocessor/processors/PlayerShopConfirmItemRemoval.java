package me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.processors;

import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.cheesyfreezy.hexrpg.main.Plugin;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playershop.PlayerShop;
import me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.IChatProcessor;

public class PlayerShopConfirmItemRemoval implements IChatProcessor {
	private PlayerShop shop;
	private int itemSlot;
	
	public PlayerShopConfirmItemRemoval(PlayerShop shop, int itemSlot) {
		this.shop = shop;
		this.itemSlot = itemSlot;
	}
	
	@Override
	public void init(Player player) {
		player.sendMessage(LanguageManager.getMessage("chat-processors.player-shop-confirmation-removal.initialize", player.getUniqueId(), true));
		player.sendMessage(LanguageManager.getMessage("chat-processors.cancel-instructions", player.getUniqueId(), true));
	}

	@Override
	public void process(Player player, String message) {
		if(message.equalsIgnoreCase(LanguageManager.getMessage("literal-translations.yes", player.getUniqueId()))) {
			Bukkit.getServer().getScheduler().runTask(Plugin.getMain(), () -> {
				if(shop.getSoldItem(itemSlot) != null) {
					shop.sellSoldItem(shop.getSoldItem(itemSlot), itemSlot, player, false);
				} else {
					player.sendMessage(LanguageManager.getMessage("chat-processors.player-shop-add-item.no-item-found", player.getUniqueId(), true));
				}

				shop.open(player, shop.isEditing());
			});
			Plugin.getMain().getChatProcessorService().removeStatus(player);
		} else if(message.equalsIgnoreCase(LanguageManager.getMessage("literal-translations.no", player.getUniqueId()))) {
			Bukkit.getServer().getScheduler().runTask(Plugin.getMain(), () -> {
				shop.open(player, shop.isEditing());
			});
		} else {
			player.sendMessage(LanguageManager.getMessage("chat-processors.player-shop-add-item.invalid-input", player.getUniqueId(), true));
		}
	}
}