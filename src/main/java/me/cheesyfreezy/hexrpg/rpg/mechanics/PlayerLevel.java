package me.cheesyfreezy.hexrpg.rpg.mechanics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

public class PlayerLevel {
	@Inject private HexRPGPlugin plugin;

	private UUID uuid;

	public PlayerLevel(UUID uuid) {
		this.uuid = uuid;
	}

	private void setLevel(int level) {
		File f = getPlayerLevelFile();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);

		c.set(uuid.toString() + ".level", level);
		try {
			c.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getLevel() {
		return YamlConfiguration.loadConfiguration(getPlayerLevelFile()).getInt(uuid.toString() + ".level");
	}

	public void setExperience(int experience) {
		File f = getPlayerLevelFile();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);

		ArrayList<String> levelsString = new ArrayList<>(ConfigFile.getConfig("player_leveling.yml").getConfigurationSection("required-exp-per-level").getKeys(false));
		
		int[] levels = new int[levelsString.size()];
		for(int i = 0; i < levels.length; i++) {
			try {
				levels[i] = Integer.parseInt(levelsString.get(i));
			} catch(NumberFormatException e) {
				continue;
			}
		}
		
		for (int i = levels.length - 1; i >= 0; i--) {
			int level = levels[i];
			if(experience >= getRequiredExperience(level)) {
				if(getLevel() != level) {
					setLevel(level);
				}
				break;
			}
		}

		c.set(uuid.toString() + ".experience", experience);
		try {
			c.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getExperience() {
		return YamlConfiguration.loadConfiguration(getPlayerLevelFile()).getInt(uuid.toString() + ".experience");
	}

	public int getRequiredExperience(int level) {
		return ConfigFile.getConfig("player_leveling.yml").getInteger("required-exp-per-level." + level);
	}

	public File getPlayerLevelFile() {
		return new File(plugin.getDataFolder() + "/data/", "player_level.dat");
	}

	public static int getEntityExperienceRate(EntityType entityType) {
		try {
			return ConfigFile.getConfig("player_leveling.yml").getInteger("exp-rates." + entityType.toString().toLowerCase());
		} catch (NullPointerException e) {
			return 0;
		}
	}
}