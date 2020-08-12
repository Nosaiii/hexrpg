package me.cheesyfreezy.hexrpg.rpg.mechanics.lootdrop;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import me.cheesyfreezy.hexrpg.main.Plugin;

public class LootDropService {
	public void start() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin.getMain(), () -> {
			File f = LootDrop.getFile();
			YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
			
			for(String key : c.getKeys(false)) {
				LootDrop ld = LootDrop.getLootDrop(LootDrop.getLocationByKey(key));
				if(!ld.hasDropped()) {
					continue;
				}
				
				ld.playIdleParticle();
			}
		}, 0, 10);
	}
}