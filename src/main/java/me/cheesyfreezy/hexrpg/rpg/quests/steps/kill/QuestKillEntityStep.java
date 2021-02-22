package me.cheesyfreezy.hexrpg.rpg.quests.steps.kill;

import me.cheesyfreezy.hexrpg.listeners.quests.queststep.kill.OnQuestKillEntity;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.QuestStep;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class QuestKillEntityStep extends QuestStep {
    private final EntityType entityType;
    private final int requiredKillCount;

    public QuestKillEntityStep(int id, EntityType entityType, int requiredKillCount) {
        super(id);

        this.entityType = entityType;
        this.requiredKillCount = requiredKillCount;
    }

    @Override
    public void start(Player player) {
        registerListener(player.getUniqueId(), new OnQuestKillEntity(this, player));
    }

    @Override
    public void finish(Player player) {
        unregisterListener(player.getUniqueId(), OnQuestKillEntity.class);
    }

    /**
     * The type of entity to kill to increase the kill count towards this quest step
     * @return
     */
    public EntityType getEntityType() {
        return entityType;
    }

    /**
     * The required amount of kills required to complete this quest step
     * @return
     */
    public int getRequiredKillCount() {
        return requiredKillCount;
    }
}
