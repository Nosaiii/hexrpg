package me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue;

import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.UUID;

public class QuestDialogue {
    private final String speakerName;
    private final HashMap<String, String> localizedMessages;

    public QuestDialogue(String speakerName, HashMap<String, String> localizedMessages) {
        this.speakerName = speakerName;
        this.localizedMessages = localizedMessages;
    }

    /**
     * Builds the localized string for this dialogue
     * @param uuid The UUID of the player to use localization for
     * @return The built localized string for this dialogue
     */
    public String getDialogue(UUID uuid) {
        String speakerName = this.speakerName;
        if(this.speakerName == null) {
            OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(uuid);
            speakerName = offlinePlayer.getName();
        }

        String message = localizedMessages.get(LanguageManager.getLocalization(uuid));
        return ChatColor.BLUE + "" + ChatColor.BOLD + speakerName + "   " + ChatColor.WHITE + message;
    }

    public double getReadingTime(UUID uuid) {
        String message = localizedMessages.get(LanguageManager.getLocalization(uuid));
        return message.trim().split(" ").length * 0.75d;
    }
}