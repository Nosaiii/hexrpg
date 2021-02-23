package me.cheesyfreezy.hexrpg.listeners.quests;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.rpg.quests.Quest;
import me.cheesyfreezy.hexrpg.rpg.quests.QuestService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPersistenceQuestSteps implements Listener {
    @Inject private QuestService questService;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        initializeQuests(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        uninitializeQuests(player);
    }

    public void initializeQuests(Player player) {
        for(Quest quest : questService.getQuests()) {
            if(!quest.hasStarted(player.getUniqueId())) {
                continue;
            }

            if(quest.hasFinished(player.getUniqueId())) {
                continue;
            }

            quest.callCurrentStep(player);
        }
    }

    public void uninitializeQuests(Player player) {
        for(Quest quest : questService.getQuests()) {
            if(!quest.hasStarted(player.getUniqueId())) {
                continue;
            }

            quest.getCurrentStep(player).forceQuit(player);
        }
    }
}