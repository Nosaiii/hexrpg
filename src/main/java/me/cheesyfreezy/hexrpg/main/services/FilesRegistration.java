package me.cheesyfreezy.hexrpg.main.services;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.cheesyfreezy.hexrpg.dependencyinjection.PluginBinder;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import me.cheesyfreezy.hexrpg.rpg.mechanics.EffectSocketService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.*;
import java.util.Scanner;

public class FilesRegistration implements IServiceRegistration {
    @Inject private HexRPGPlugin plugin;

    @Override
    public boolean register(PluginBinder binder, Injector injector) {
        String[] fileNames = new String[] {
                "config/config.yml",
                "config/drop_table.yml",
                "config/" + EffectSocketService.FILE_NAME,
                "config/loot_drop.yml",
                "config/player_leveling.yml",
                "config/rpgitem.yml",
                "config/scrolls.yml",
                "lang/english.yml",
                "lang/global_lang.yml",
                "quests/the-plague.json"
        };

        for(String fileName : fileNames) {
            File configFile = new File(plugin.getDataFolder() + File.separator + fileName);

            File parentFolder = configFile.getParentFile();
            if(!parentFolder.exists()) {
                parentFolder.mkdirs();
            }

            InputStream configInputStream = plugin.getResource(fileName);

            boolean replace = false;
            if(!configFile.exists() || (configFile.exists() && replace)) {
                try {
                    configFile.createNewFile();

                    try (FileOutputStream configOutputStream = new FileOutputStream(configFile)) {
                        BufferedWriter configBufferedWriter = new BufferedWriter(new OutputStreamWriter(configOutputStream));
                        Scanner scanner = new Scanner(configInputStream);

                        while(scanner.hasNext()) {
                            configBufferedWriter.write(scanner.nextLine());
                            configBufferedWriter.newLine();
                        }

                        configBufferedWriter.close();
                    } catch(Exception e) {
                        if(e instanceof IOException) {
                            Bukkit.getConsoleSender().sendMessage(HexRPGPlugin.PREFIX + ChatColor.RED + "An error occured while writing content to a configuration file!");
                            return false;
                        } else {
                            e.printStackTrace();
                        }
                    }
                } catch(IOException e) {
                    Bukkit.getConsoleSender().sendMessage(HexRPGPlugin.PREFIX + ChatColor.RED + "An error occured while trying to overwrite an existing configuration file!");
                    return false;
                }
            }
        }

        return true;
    }
}
