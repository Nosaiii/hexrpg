package me.cheesyfreezy.hexrpg.commands.configuration;

import me.cheesyfreezy.hexrpg.main.Plugin;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.List;
import java.util.UUID;

public class HexRPGCmd implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String command = cmd.getName();

        if (command.equalsIgnoreCase("HexRPG")) {
            if(!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
                return true;
            }

            UUID uuid = sender instanceof Player ? ((Player) sender).getUniqueId() : null;

            if(sender instanceof Player && !sender.hasPermission("hexrpg.hexrpg")) {
                sender.sendMessage(LanguageManager.getMessage("command-and-chat-execution.not-allowed", uuid, true));
                return true;
            }

            if(args.length == 0) {
                PluginDescriptionFile descriptionFile = Plugin.getMain().getDescription();
                String version = descriptionFile.getVersion();
                List<String> authors = descriptionFile.getAuthors();

                sender.sendMessage(ChatColor.GOLD + "-=[" + ChatColor.RED + " HexRPG " + ChatColor.GOLD + "]=-");
                sender.sendMessage(ChatColor.GOLD + LanguageManager.getMessage("literal-translations.version", uuid) + ": " + ChatColor.RED + version);
                sender.sendMessage(ChatColor.GOLD + LanguageManager.getMessage("literal-translations.authors", uuid) + ": " + ChatColor.RED + String.join(", ", authors));
            } else if(args.length == 1) {
                if(args[0].equalsIgnoreCase("Reload")) {
                    Plugin.getMain().reloadConfig();
                    sender.sendMessage(LanguageManager.getMessage("plugin.reloaded", uuid, true));
                } else {
                    sender.sendMessage(LanguageManager.getMessage("command-and-chat-execution.invalid-command-argument", uuid, true));
                }
            }
        }

        return false;
    }
}