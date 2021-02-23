package me.cheesyfreezy.hexrpg.commands.items;

import me.cheesyfreezy.hexrpg.rpg.items.other.Backpack;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import me.cheesyfreezy.hexrpg.tools.PrimitiveTypeTools;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class GiveBackpackCmd implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName();

		if (command.equalsIgnoreCase("GiveBackpack")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;

				if (!player.hasPermission("hexrpg.givebackpack")) {
					player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.not-allowed", player.getUniqueId(), true));
					return true;
				}

				if (args.length <= 2) {
					int size = 3;
					Player target = player;

					if (args.length >= 1) {
						if (!PrimitiveTypeTools.isInt(args[0])) {
							player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.backpacks.invalid-size", player.getUniqueId(), true));
							return true;
						}
						size = Integer.parseInt(args[0]);
					}

					if(size < 1 || size > Backpack.MAX_SIZE) {
						player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.backpacks.size-out-of-range", player.getUniqueId(), true));
						return true;
					}

					if(args.length >= 2) {
						Player targetAttempt = Bukkit.getServer().getPlayer(args[1]);
						if(targetAttempt == null || !targetAttempt.isOnline()) {
							player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.invalid-player", player.getUniqueId(), true));
							return true;
						}
						target = targetAttempt;
					}

					target.getInventory().addItem(new Backpack(size).getTemporaryItem());

					target.sendMessage(LanguageManager.getMessage("command-and-chat-execution.backpacks.received", target.getUniqueId(), true));
					if(player != target) {
						player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.backpacks.sent", player.getUniqueId(), true));
					}
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