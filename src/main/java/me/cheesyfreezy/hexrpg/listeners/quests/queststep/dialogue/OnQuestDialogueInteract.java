package me.cheesyfreezy.hexrpg.listeners.quests.queststep.dialogue;

import me.cheesyfreezy.hexrpg.rpg.quests.constants.AbstractAction;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue.QuestInteractDialogueStep;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class OnQuestDialogueInteract implements Listener {
    private final Player player;
    private final QuestInteractDialogueStep dialogueStep;

    public OnQuestDialogueInteract(Player player, QuestInteractDialogueStep dialogueStep) {
        this.player = player;
        this.dialogueStep = dialogueStep;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (this.player != player) {
            return;
        }

        Action action = event.getAction();
        switch(dialogueStep.getAction()) {
            case LEFT_AND_RIGHT:
            case LEFT:
                if(!action.equals(Action.LEFT_CLICK_BLOCK)) return;
                if(dialogueStep.getAction().equals(AbstractAction.LEFT_AND_RIGHT)) break;
            case RIGHT:
                if(!action.equals(Action.RIGHT_CLICK_BLOCK)) return;
                break;
        }

        if(!event.getClickedBlock().getLocation().equals(dialogueStep.getLocation())) {
            return;
        }

        dialogueStep.startDialogue(player);
    }
}
