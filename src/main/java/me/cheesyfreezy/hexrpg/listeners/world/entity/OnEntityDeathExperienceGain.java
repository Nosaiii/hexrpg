package me.cheesyfreezy.hexrpg.listeners.world.entity;

import java.util.Random;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import me.cheesyfreezy.hexrpg.rpg.mechanics.PlayerLevel;

public class OnEntityDeathExperienceGain implements Listener {
	@Inject private HexRPGPlugin plugin;

	@EventHandler
	public void onEntityDeathExperienceGain(EntityDeathEvent event) {
		if(event.getEntity().getKiller() == null) {
			return;
		}
		Player killer = event.getEntity().getKiller();
		int expGained = PlayerLevel.getEntityExperienceRate(event.getEntityType());
		
		if(expGained == 0) {
			return;
		}
		
		PlayerLevel playerLevel = new PlayerLevel(plugin, killer.getUniqueId());
		playerLevel.setExperience(playerLevel.getExperience() + expGained);
		
		Random r = new Random();
		float rPitch = (r.nextInt(20 - 5) + 5) / 10;
		killer.playSound(killer.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3.0f, rPitch);

		killer.sendMessage(LanguageManager.getMessage("player-leveling.experience-gained", killer.getUniqueId(), true, Integer.toString(expGained)));
	}
}