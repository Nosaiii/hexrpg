package me.cheesyfreezy.hexrpg.listeners.quests.queststep.dialogue;

import me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue.QuestAtLocationDialogueStep;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class OnQuestDialogueAtLocation implements Listener {
    private final Player player;
    private final QuestAtLocationDialogueStep dialogueStep;

    public OnQuestDialogueAtLocation(Player player, QuestAtLocationDialogueStep dialogueStep) {
        this.player = player;
        this.dialogueStep = dialogueStep;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (this.player != player) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null) {
            return;
        }

        if(!to.isWorldLoaded() || !to.getWorld().equals(dialogueStep.getLocation().getWorld())) {
            return;
        }

        int fromX = from.getBlockX(), fromY = from.getBlockY(), fromZ = from.getBlockZ();
        int toX = to.getBlockX(), toY = to.getBlockY(), toZ = to.getBlockZ();

        if (fromX == toX && fromY == toY && fromZ == toZ) {
            return;
        }

        if (
                toX != dialogueStep.getLocation().getBlockX() ||
                toY != dialogueStep.getLocation().getBlockY() ||
                toZ != dialogueStep.getLocation().getBlockZ()) {
            return;
        }

        dialogueStep.startDialogue(player);
    }
}
