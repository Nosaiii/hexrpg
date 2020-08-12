package me.cheesyfreezy.hexrpg.listeners.world.entity;

import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class OnFireworkDamage implements Listener {
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(!(event.getDamager() instanceof Firework)) {
			return;
		}
		
		if(event.getDamager().hasMetadata("nodamage")) {
			event.setCancelled(true);
		}
	}
}