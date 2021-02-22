package me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue;

import me.cheesyfreezy.hexrpg.listeners.quests.queststep.dialogue.OnQuestDialogueAtLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class QuestAtLocationDialogueStep extends QuestAbstractDialogueStep {
    private final QuestDialogue[] dialogue;
    private final Location location;

    public QuestAtLocationDialogueStep(int id, QuestDialogue[] dialogue, Location location) {
        super(id);

        this.dialogue = dialogue;
        this.location = location;
    }

    @Override
    public void start(Player player) {
        registerListener(player.getUniqueId(), new OnQuestDialogueAtLocation(player, this));
    }

    @Override
    public void finish(Player player) {
        unregisterListener(player.getUniqueId(), OnQuestDialogueAtLocation.class);
    }

    public void startDialogue(Player player) {
        startDialogueRunnable(player, dialogue, i -> {}, () -> onNext(player));
    }

    /**
     * The location to reach for this quest step to complete
     * @return The location to reach for this quest step to complete
     */
    public Location getLocation() {
        return location;
    }
}
