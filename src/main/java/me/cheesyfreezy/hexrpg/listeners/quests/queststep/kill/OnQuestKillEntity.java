package me.cheesyfreezy.hexrpg.listeners.quests.queststep.kill;

import me.cheesyfreezy.hexrpg.rpg.quests.steps.kill.QuestKillEntityStep;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class OnQuestKillEntity implements Listener {
    private final QuestKillEntityStep questKillEntityStep;
    private final Player killer;
    private final EntityType entityType;
    private final int requiredKillCount;
    private int killCount;

    public OnQuestKillEntity(QuestKillEntityStep questKillEntityStep, Player killer, EntityType entityType, int requiredKillCount) {
        this.questKillEntityStep = questKillEntityStep;
        this.killer = killer;
        this.entityType = entityType;
        this.requiredKillCount = requiredKillCount;
        killCount = 0;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if(!entity.getType().equals(entityType)) {
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
        if(killCount >= requiredKillCount) {
            questKillEntityStep.onNext(this.killer);
        }
    }
}