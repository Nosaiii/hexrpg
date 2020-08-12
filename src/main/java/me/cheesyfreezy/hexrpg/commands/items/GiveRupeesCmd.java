package me.cheesyfreezy.hexrpg.commands.items;

import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.cheesyfreezy.hexrpg.rpg.items.other.Rupee;
import me.cheesyfreezy.hexrpg.tools.PrimitiveTypeTools;

public class GiveRupeesCmd implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName();

		if (command.equalsIgnoreCase("GiveRupees")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;

				if (!player.hasPermission("hexrpg.giverupees")) {
					player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.not-allowed", player.getUniqueId(), true));
					return true;
				}

				if (args.length <= 2) {
					int amount = 1;
					Player target = player;

					if (args.length >= 1) {
						if (!PrimitiveTypeTools.isInt(args[0])) {
							player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.invalid-amount", player.getUniqueId(), true));
							return true;
						}
						amount = Integer.parseInt(args[0]);
					}
					
					if(args.length >= 2) {
						Player targetAttempt = Bukkit.getServer().getPlayer(args[1]);
						if(targetAttempt == null || !targetAttempt.isOnline()) {
							player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.invalid-player", player.getUniqueId(), true));
							return true;
						}
						target = targetAttempt;
					}

					for (int i = 0; i < amount; i++) {
						target.getInventory().addItem(new Rupee().getTemporaryItem());
					}

					if(player != target) {
						player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.rupees.sent", player.getUniqueId(), true, Integer.toString(amount)));
					}
					target.sendMessage(LanguageManager.getMessage("command-and-chat-execution.rupees.received", target.getUniqueId(), true, Integer.toString(amount)));
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