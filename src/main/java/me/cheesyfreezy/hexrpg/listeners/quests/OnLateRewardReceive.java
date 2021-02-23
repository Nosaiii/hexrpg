package me.cheesyfreezy.hexrpg.listeners.quests;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.rpg.quests.Quest;
import me.cheesyfreezy.hexrpg.rpg.quests.QuestService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnLateRewardReceive implements Listener {
    @Inject private QuestService questService;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for(Quest quest : questService.getQuests()) {
            if(!quest.hasFinished(player.getUniqueId()) || quest.hasBeenRewarded(player.getUniqueId())) {
                continue;
            }

            quest.reward(player);
        }
    }
}