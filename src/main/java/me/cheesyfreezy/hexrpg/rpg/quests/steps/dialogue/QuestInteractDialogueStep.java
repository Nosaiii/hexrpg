package me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue;

import me.cheesyfreezy.hexrpg.listeners.quests.queststep.dialogue.OnQuestDialogueInteract;
import me.cheesyfreezy.hexrpg.rpg.quests.constants.AbstractAction;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class QuestInteractDialogueStep extends QuestAbstractDialogueStep {
    private final QuestDialogue[] dialogue;
    private final Location location;
    private final AbstractAction action;

    public QuestInteractDialogueStep(int id, QuestDialogue[] dialogue, Location location, AbstractAction action) {
        super(id);

        this.dialogue = dialogue;
        this.location = location;
        this.action = action;
    }

    @Override
    public void start(Player player) {
        registerListener(player.getUniqueId(), new OnQuestDialogueInteract(player, this));
    }

    @Override
    public void finish(Player player) {
        unregisterListener(player.getUniqueId(), OnQuestDialogueInteract.class);
    }

    public void startDialogue(Player player) {
        startDialogueRunnable(player, dialogue, i -> {}, () -> onNext(player));
    }

    /**
     * The location to interact with for this quest step to complete
     * @return The location to interact with for this quest step to complete
     */
    public Location getLocation() {
        return location;
    }

    /**
     * The action required to be performed for this step to be completed
     * @return A {@link AbstractAction} that describes the action required to be performed for this step to be completed
     */
    public AbstractAction getAction() {
        return action;
    }
}
