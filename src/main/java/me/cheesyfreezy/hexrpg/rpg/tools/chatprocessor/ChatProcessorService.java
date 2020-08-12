package me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class ChatProcessorService {
	private HashMap<Player, IChatProcessor> statusses;
	
	public ChatProcessorService() {
		statusses = new HashMap<Player, IChatProcessor>();
	}
	
	public void setStatus(Player player, IChatProcessor processor) {
		statusses.put(player, processor);
		processor.init(player);
	}
	
	public void removeStatus(Player player) {
		statusses.remove(player);
	}
	
	public IChatProcessor getStatus(Player player) {
		return statusses.get(player);
	}
	
	public boolean hasStatus(Player player) {
		return statusses.containsKey(player);
	}
}