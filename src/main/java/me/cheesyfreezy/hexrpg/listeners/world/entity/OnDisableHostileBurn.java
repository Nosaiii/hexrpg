package me.cheesyfreezy.hexrpg.listeners.world.entity;

import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;

import me.cheesyfreezy.hexrpg.main.Plugin;

public class OnDisableHostileBurn implements Listener {
	@EventHandler
	public void onEntityCombust(EntityCombustEvent event) {
		if(!ConfigFile.getConfig("config.yml").getBoolean("world-settings.burn-hostiles")) {
			event.setCancelled(true);
		}
	}
}
