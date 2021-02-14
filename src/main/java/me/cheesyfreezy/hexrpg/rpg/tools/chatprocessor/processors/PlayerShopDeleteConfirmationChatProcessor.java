package me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.processors;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.ChatProcessorService;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.cheesyfreezy.hexrpg.rpg.mechanics.playershop.PlayerShop;
import me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.IChatProcessor;

public class PlayerShopDeleteConfirmationChatProcessor implements IChatProcessor {
	@Inject private HexRPGPlugin plugin;
	@Inject private ChatProcessorService chatProcessorService;

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
				Bukkit.getServer().getScheduler().runTask(plugin, () -> {
					shop.delete();
				});
				chatProcessorService.removeStatus(player);
				
				break;
			case "NO":
				Bukkit.getServer().getScheduler().runTask(plugin, () -> {
					shop.open(player, shop.isEditing());
				});
				break;
			default:
				player.sendMessage(LanguageManager.getMessage("chat-processors.player-shop-delete-confirmation.invalid-value", player.getUniqueId(), true));
				break;
		}
	}
}