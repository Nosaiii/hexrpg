package me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue;

import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.UUID;

public class QuestDialogue {
    private final String speakerName;
    private final HashMap<String, String> localizedMessages;
    private int speakerNamePadding = 8; // Default padding is '8'

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
            /*OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(uuid);
            speakerName = offlinePlayer.getName();*/
            speakerName = LanguageManager.getMessage("literal-translations.you", uuid);
        }

        String message = localizedMessages.get(LanguageManager.getLocalization(uuid));
        return ChatColor.BLUE + "" + ChatColor.BOLD + StringUtils.rightPad(speakerName, speakerNamePadding) + " " + ChatColor.WHITE + message;
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

    /**
     * Sets the padding on the right side from the name of the speaker to align it correctly visualy
     * @param padding The padding to set
     */
    public void setSpeakerNamePadding(int padding) {
        if(padding < 4) {
            padding = 4;
        }

        speakerNamePadding = padding;
    }
}