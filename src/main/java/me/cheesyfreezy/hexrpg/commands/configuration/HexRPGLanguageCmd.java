package me.cheesyfreezy.hexrpg.commands.configuration;

import me.cheesyfreezy.hexrpg.rpg.mechanics.languageselector.LanguageSelector;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;


public class HexRPGLanguageCmd implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName();

		if (command.equalsIgnoreCase("HexRPGLanguage")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;

				if (!player.hasPermission("hexrpg.sethexrpglanguage")) {
					player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.not-allowed", player.getUniqueId(), true));
					return true;
				}

				if (args.length == 0) {
					LanguageSelector languageSelector = new LanguageSelector();
					languageSelector.open(player, 1);
				} else {
					player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.too-many-arguments", player.getUniqueId(), true));
				}
			} else if (sender instanceof ConsoleCommandSender) {
				sender.sendMessage(LanguageManager.getMessage("command-and-chat-execution.player-only-command", null, true));
			}
		}

		return false;
	}
}