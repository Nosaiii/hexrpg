package me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.processors;

import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.cheesyfreezy.hexrpg.main.Plugin;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playershop.PlayerShop;
import me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.IChatProcessor;

public class PlayerShopRenamingChatProcessor implements IChatProcessor {
	private PlayerShop shop;
	
	public PlayerShopRenamingChatProcessor(PlayerShop shop) {
		this.shop = shop;
	}
	
	@Override
	public void init(Player player) {
		player.sendMessage(LanguageManager.getMessage("chat-processors.player-shop-renaming.initialize", player.getUniqueId(), true));
		player.sendMessage(LanguageManager.getMessage("chat-processors.cancel-instructions", player.getUniqueId(), true));
	}
	
	@Override
	public void process(Player player, String message) {
		if(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message)).length() > 20) {
			player.sendMessage(LanguageManager.getMessage("chat-processors.player-shop-renaming.shop-name-has-limit", player.getUniqueId(), true));
			return;
		}
		
		shop.setShopName(message);
		Bukkit.getServer().getScheduler().runTask(Plugin.getMain(), () -> {
			shop.open(player, shop.isEditing());
		});
		
		Plugin.getMain().getChatProcessorService().removeStatus(player);
	}
}