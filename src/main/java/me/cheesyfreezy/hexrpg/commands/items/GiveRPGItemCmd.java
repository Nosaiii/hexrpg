package me.cheesyfreezy.hexrpg.commands.items;

import java.util.Arrays;

import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.hexrpg.nbtapi.NBTItem;
import me.cheesyfreezy.hexrpg.rpg.items.combatitem.RPGCombatItem;
import me.cheesyfreezy.hexrpg.rpg.items.combatitem.RPGCombatItemTier;
import me.cheesyfreezy.hexrpg.tools.PrimitiveTypeTools;

public class GiveRPGItemCmd implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName();
		
		if(command.equalsIgnoreCase("GiveRPGItem")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				
				if(!player.hasPermission("hexrpg.giverpgitem")) {
					player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.not-allowed", player.getUniqueId(), true));
					return true;
				}
				
				String[] rpgItemsCollection = RPGCombatItem.getCollection();
				
				if(args.length == 0) {
					player.sendMessage(ChatColor.RED + "/GiveRPGItem " +
							"[" + LanguageManager.getMessage("literal-translations.name", player.getUniqueId()) + "] " +
							"<" + LanguageManager.getMessage("literal-translations.amount", player.getUniqueId()) + "> " +
							"<" + LanguageManager.getMessage("literal-translations.identified", player.getUniqueId()) + ":" + LanguageManager.getMessage("literal-translations.y", player.getUniqueId()) + "/" + LanguageManager.getMessage("literal-translations.n", player.getUniqueId()) + "> " +
							"<" + LanguageManager.getMessage("literal-translations.tier", player.getUniqueId()) + "> " +
							"<" + LanguageManager.getMessage("literal-translations.durability", player.getUniqueId()) + ">");

					player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.rpg-items.available", player.getUniqueId(), true));
					player.sendMessage(ChatColor.YELLOW + String.join(", ", rpgItemsCollection));
				} else if(args.length >= 1 && args.length <= 5) {
					if(!Arrays.asList(rpgItemsCollection).contains(args[0])) {
						player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.rpg-items.invalid-rpg-item", player.getUniqueId(), true, args[0]));
						return true;
					}
					String itemKey = args[0];
					
					int amount = 1;
					if(args.length >= 2) {
						if(!PrimitiveTypeTools.isInt(args[1])) {
							player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.invalid-amount", player.getUniqueId(), true));
							return true;
						}
						amount = Integer.parseInt(args[1]);
					}
					
					boolean identified = false;
					if(args.length >= 3) {
						String ynValue = args[2];
						if(ynValue.equalsIgnoreCase(LanguageManager.getMessage("literal-translations.y", player.getUniqueId()))) {
							identified = true;
						} else if(ynValue.equalsIgnoreCase(LanguageManager.getMessage("literal-translations.y", player.getUniqueId()))) {
							identified = false;
						} else {
							player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.rpg-items.invalid-yes-no", player.getUniqueId(), true));
							return true;
						}
					}
					
					RPGCombatItemTier tier;
					if(args.length >= 4) {
						try {
							tier = RPGCombatItemTier.valueOf(args[3].toUpperCase());
						} catch(IllegalArgumentException e) {
							player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.rpg-items.invalid-tier", player.getUniqueId(), true, args[3]));
							return true;
						}
					} else {
						tier = RPGCombatItem.getRandomItemTier(itemKey);
					}
					
					int durability = -1;
					if(args.length >= 5) {
						if(!PrimitiveTypeTools.isInt(args[4])) {
							player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.rpg-items.invalid-durability", player.getUniqueId(), true));
							return true;
						}
						durability = Integer.parseInt(args[4]);
					}
					
					for(int i=0;i<amount;i++) {
						ItemStack item = RPGCombatItem.build(itemKey);

						NBTItem nbtItem = new NBTItem(item);
						RPGCombatItem rpgCombatItem = nbtItem.getObject("rpgdata_combatitem", RPGCombatItem.class);
						rpgCombatItem.setIdentified(identified, true, tier, item);
						
						if(durability != -1) {
							rpgCombatItem.setDurability(durability, item);
						}
						
						player.getInventory().addItem(rpgCombatItem.update(item));
					}

					player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.rpg-items.received", player.getUniqueId(), true));
				} else {
					player.sendMessage(ChatColor.RED + "You have entered too many arguments!");
				}
			} else if(sender instanceof ConsoleCommandSender) {
				sender.sendMessage(ChatColor.YELLOW + "This is a player-only command!");
			}
		}
		return false;
	}
}