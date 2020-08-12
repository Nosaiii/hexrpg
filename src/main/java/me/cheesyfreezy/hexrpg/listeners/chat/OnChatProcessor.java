package me.cheesyfreezy.hexrpg.listeners.chat;

import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.cheesyfreezy.hexrpg.main.Plugin;
import me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.ChatProcessorService;

public class OnChatProcessor implements Listener {
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		
		ChatProcessorService cps = Plugin.getMain().getChatProcessorService();
		if(cps.hasStatus(player)) {
			event.setCancelled(true);
			
			if(message.equalsIgnoreCase(LanguageManager.getMessage("literal-translations.cancel", player.getUniqueId()))) {
				cps.removeStatus(player);
				player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.chat-processor.cancelled", player.getUniqueId(), true));
				
				return;
			}
			
			cps.getStatus(player).process(player, message);
		}
	}
}