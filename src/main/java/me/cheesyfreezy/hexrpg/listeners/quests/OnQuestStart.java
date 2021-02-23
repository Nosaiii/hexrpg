package me.cheesyfreezy.hexrpg.listeners.quests;

import com.google.inject.Inject;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import me.cheesyfreezy.hexrpg.rpg.quests.Quest;
import me.cheesyfreezy.hexrpg.rpg.quests.QuestService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.Arrays;
import java.util.Optional;

public class OnQuestStart implements Listener {
    @Inject private QuestService questService;

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        Entity entity = event.getRightClicked();
        NBTEntity nbtEntity = new NBTEntity(entity);

        if(!nbtEntity.getPersistentDataContainer().hasKey("quest_npc_id")) {
            return;
        }
        int questNpcId = nbtEntity.getPersistentDataContainer().getInteger("quest_npc_id");

        event.setCancelled(true);

        Optional<Quest> optionalQuest = Arrays.stream(questService.getQuests())
                .filter(q -> !q.hasStarted(player.getUniqueId()))
                .filter(q -> q.getStartNPC().getId() == questNpcId)
                .findFirst();

        if(!optionalQuest.isPresent()) {
            return;
        }

        Quest quest = optionalQuest.get();

        quest.start(player);
    }
}