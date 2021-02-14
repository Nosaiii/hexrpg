package me.cheesyfreezy.hexrpg.commands.items;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.rpg.items.applicable.ApplicableService;
import me.cheesyfreezy.hexrpg.rpg.items.applicable.ApplicableType;
import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import me.cheesyfreezy.hexrpg.tools.PrimitiveTypeTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class GiveRPGScrollCmd implements CommandExecutor {
	@Inject private ApplicableService applicableService;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName();
		
		if(command.equalsIgnoreCase("GiveRPGScroll")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				
				if(!player.hasPermission("hexrpg.giverpgscroll")) {
					player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.not-allowed", player.getUniqueId(), true));
					return true;
				}

				ConfigFile c = ConfigFile.getConfig("scrolls.yml");
				String[] availableScrolls = c.getRootKeys().toArray(new String[c.getRootKeys().size()]);
				
				if(args.length == 0) {
					player.sendMessage(ChatColor.RED + "/GiveRPGScroll " +
							"[" + LanguageManager.getMessage("literal-translations.name", player.getUniqueId()) + "] " +
							"<" + LanguageManager.getMessage("literal-translations.amount", player.getUniqueId()) + "> " +
							"<" + LanguageManager.getMessage("literal-translations.player", player.getUniqueId()) + ">");

					player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.scrolls.available", player.getUniqueId(), true));
					player.sendMessage(ChatColor.YELLOW + String.join(", ", availableScrolls));
				} else if(args.length >= 1 && args.length <= 4) {
					if(!Arrays.asList(availableScrolls).contains(args[0])) {
						player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.scrolls.invalid-scroll", player.getUniqueId(), true, args[0]));
						return true;
					}
					ItemStack scrollItem = applicableService.getReference(ApplicableType.SCROLL, args[0]).getTemporaryItem();
					
					int amount = 1;
					if(args.length >= 2) {
						if(!PrimitiveTypeTools.isInt(args[1])) {
							player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.invalid-amount", player.getUniqueId(), true));
							return true;
						}
						amount = Integer.parseInt(args[1]);
					}

					Player target = player;
					if (args.length >= 3) {
						Player targetAttempt = Bukkit.getServer().getPlayer(args[1]);
						if (targetAttempt == null || !targetAttempt.isOnline()) {
							player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.invalid-player", player.getUniqueId(), true));
							return true;
						}
						target = targetAttempt;
					}

					for (int i = 0; i < amount; i++) {
						target.getInventory().addItem(scrollItem);
					}

					if(player != target) {
						player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.scrolls.sent", player.getUniqueId(), true, Integer.toString(amount)));
					}
					target.sendMessage(LanguageManager.getMessage("command-and-chat-execution.scrolls.received", target.getUniqueId(), true, Integer.toString(amount)));
				} else {
					player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.too-many-arguments", player.getUniqueId(), true));
				}
			} else if(sender instanceof ConsoleCommandSender) {
				sender.sendMessage(LanguageManager.getMessage("command-and-chat-execution.player-only-command", null, true));
			}
		}
		return false;
	}
}