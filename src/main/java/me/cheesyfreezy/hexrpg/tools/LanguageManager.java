package me.cheesyfreezy.hexrpg.tools;

import me.cheesyfreezy.hexrpg.main.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LanguageManager {
    public static String getMessage(String path, UUID uuid, String... replacements) {
        return getMessage(path, uuid, false, replacements);
    }

    public static String getMessage(String path, UUID uuid, boolean colors, String... replacements) {
        YamlConfiguration languageConfig;
        if(uuid == null) {
            languageConfig = YamlConfiguration.loadConfiguration(new File(Plugin.getMain().getDataFolder() + File.separator + "lang", "global_lang.yml"));
        } else {
            languageConfig = getLanguageConfig(uuid);
        }

        String msg = languageConfig.getString(path);
        if (colors) msg = ChatColor.translateAlternateColorCodes('&', msg);

        if (replacements.length > 0) {
            for (int i = 0; i < replacements.length; i++) {
                msg = msg.replace("{" + i + "}", replacements[i]);
            }
        }

        return msg;
    }

    public static String getGlobalMessage(String path, boolean colors, String... replacements) {
        return getMessage(path, null, colors, replacements);
    }

    public static String getGlobalMessage(String path, String... replacements) {
        return getGlobalMessage(path, false, replacements);
    }

    public static ArrayList<String> getMessageList(String path, UUID uuid, String... replacements) {
        return getMessageList(path, uuid, false, replacements);
    }

    public static ArrayList<String> getMessageList(String path, UUID uuid, boolean colors, String... replacements) {
        List<String> msgList = getLanguageConfig(uuid).getStringList(path);
        ArrayList<String> newMsgList = new ArrayList<>();

        for(String line : msgList) {
            if (colors) line = ChatColor.translateAlternateColorCodes('&', line);

            if (replacements.length > 0) {
                for (int i = 0; i < replacements.length; i++) {
                    line = line.replace("{" + i + "}", replacements[i]);
                }
            }

            newMsgList.add(line);
        }

        return (ArrayList<String>) msgList;
    }

    private static YamlConfiguration getLanguageConfig(UUID uuid) {
        File languageDataFile = new File(Plugin.getMain().getDataFolder() + File.separator + "data", "language_data.yml");
        YamlConfiguration languageDataConfig = YamlConfiguration.loadConfiguration(languageDataFile);

        String languageFileName = ConfigFile.getConfig("config.yml").getString("language-settings.default");
        if (uuid != null && languageDataFile.exists() && languageDataConfig.isSet(uuid.toString())) {
            languageFileName = languageDataConfig.getString(uuid.toString());
        }

        File languageFile = new File(Plugin.getMain().getDataFolder() + File.separator + "lang", languageFileName);
        return YamlConfiguration.loadConfiguration(languageFile);
    }
}