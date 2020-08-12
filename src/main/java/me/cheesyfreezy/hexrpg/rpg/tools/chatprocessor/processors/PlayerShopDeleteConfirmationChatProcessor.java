package me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.processors;

import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.cheesyfreezy.hexrpg.main.Plugin;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playershop.PlayerShop;
import me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.IChatProcessor;

public class PlayerShopDeleteConfirmationChatProcessor implements IChatProcessor {
	private PlayerShop shop;
	
	public PlayerShopDeleteConfirmationChatProcessor(PlayerShop shop) {
		this.shop = shop;
	}
	
	@Override
	public void init(Player player) {
		player.sendMessage(LanguageManager.getMessage("chat-processors.player-shop-delete-confirmation.initialize", player.getUniqueId(), true));
		player.sendMessage(LanguageManager.getMessage("chat-processors.cancel-instructions", player.getUniqueId(), true));
	}

	@Override
	public void process(Player player, String message) {
		switch(message.toUpperCase()) {
			case "YES":
				Bukkit.getServer().getScheduler().runTask(Plugin.getMain(), () -> {
					shop.delete();
				});
				Plugin.getMain().getChatProcessorService().removeStatus(player);
				
				break;
			case "NO":
				Bukkit.getServer().getScheduler().runTask(Plugin.getMain(), () -> {
					shop.open(player, shop.isEditing());
				});
				break;
			default:
				player.sendMessage(LanguageManager.getMessage("chat-processors.player-shop-delete-confirmation.invalid-value", player.getUniqueId(), true));
				break;
		}
	}
}