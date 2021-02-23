package me.cheesyfreezy.hexrpg.listeners.chat;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.ChatProcessorService;

public class OnChatProcessor implements Listener {
	@Inject private ChatProcessorService chatProcessorService;

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();

		if(chatProcessorService.hasStatus(player)) {
			event.setCancelled(true);
			
			if(message.equalsIgnoreCase(LanguageManager.getMessage("literal-translations.cancel", player.getUniqueId()))) {
				chatProcessorService.removeStatus(player);
				player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.chat-processor.cancelled", player.getUniqueId(), true));
				
				return;
			}

			chatProcessorService.getStatus(player).process(player, message);
		}
	}
}