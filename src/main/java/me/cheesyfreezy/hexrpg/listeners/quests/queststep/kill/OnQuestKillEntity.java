package me.cheesyfreezy.hexrpg.listeners.quests.queststep.kill;

import me.cheesyfreezy.hexrpg.rpg.quests.steps.kill.QuestKillEntityStep;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class OnQuestKillEntity implements Listener {
    private final QuestKillEntityStep questStep;
    private final Player killer;
    private int killCount;

    public OnQuestKillEntity(QuestKillEntityStep questStep, Player killer) {
        this.questStep = questStep;
        this.killer = killer;
        killCount = 0;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if(!entity.getType().equals(questStep.getEntityType())) {
            return;
        }

        if(entity.getKiller() == null) {
            return;
        }
        Player killer = entity.getKiller();

        if(this.killer != killer) {
            return;
        }

        killCount++;

        String killCountString = Integer.toString(killCount);
        String requiredKillCountString = Integer.toString(questStep.getRequiredKillCount());
        String entityTypeString = WordUtils.capitalizeFully(questStep.getEntityType().toString().toLowerCase().replace("_", ""));
        killer.sendMessage(LanguageManager.getMessage("quests.steps.kill-entity.progress", killer.getUniqueId(),true, killCountString, requiredKillCountString, entityTypeString));

        if(killCount >= questStep.getRequiredKillCount()) {
            questStep.onNext(this.killer);
        }
    }
}