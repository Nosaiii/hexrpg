package me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue;

import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.UUID;

public class QuestDialogue {
    private final String speakerName;
    private final ChatColor prefixColor;
    private final HashMap<String, String> localizedMessages;

    public QuestDialogue(String speakerName, ChatColor prefixColor, HashMap<String, String> localizedMessages) {
        this.speakerName = speakerName;
        this.prefixColor = prefixColor;
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
            speakerName = LanguageManager.getMessage("literal-translations.you", uuid);
        }

        String message = localizedMessages.get(LanguageManager.getLocalization(uuid));
        return prefixColor + "" + ChatColor.BOLD + speakerName + " " + ChatColor.WHITE + message;
    }

    /**
     * Calculates the time to read the message of this dialogue
     * @param uuid The UUID of the player to calculate for the right localized message
     * @return The average time in seconds required to read the message
     */
    public double getReadingTime(UUID uuid) {
        String message = localizedMessages.get(LanguageManager.getLocalization(uuid));
        return Math.max(message.trim().split(" ").length * 0.375d, 2.5d);
    }

    /**
     * The name of the speaker of this dialogue
     * @return The name of the speaker of this dialogue
     */
    public String getSpeakerName() {
        return speakerName;
    }
}