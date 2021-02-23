package me.cheesyfreezy.hexrpg.rpg.mechanics.lootdrop;

import java.io.File;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public class LootDropService {
	@Inject private HexRPGPlugin plugin;

	public void start() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			File f = new File(plugin.getDataFolder() + "/data/", "lootdrop_data.yml");
			YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
			
			for(String key : c.getKeys(false)) {
				LootDrop ld = LootDrop.getLootDrop(plugin, LootDrop.getLocationByKey(key));
				if(!ld.hasDropped()) {
					continue;
				}
				
				ld.playIdleParticle();
			}
		}, 0, 10);
	}
}