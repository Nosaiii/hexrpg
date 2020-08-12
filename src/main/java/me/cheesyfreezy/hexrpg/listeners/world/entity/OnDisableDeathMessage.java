package me.cheesyfreezy.hexrpg.listeners.world.entity;

import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.cheesyfreezy.hexrpg.main.Plugin;

public class OnDisableDeathMessage implements Listener {
	@EventHandler
	public void onEntityDeath(PlayerDeathEvent event) {
		if(ConfigFile.getConfig("config.yml").getBoolean("disable-death-message")) {
			event.setDeathMessage(null);
		}
	}
}