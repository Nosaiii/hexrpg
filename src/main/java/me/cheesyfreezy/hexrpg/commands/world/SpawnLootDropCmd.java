package me.cheesyfreezy.hexrpg.commands.world;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.cheesyfreezy.hexrpg.rpg.mechanics.lootdrop.LootDrop;
import me.cheesyfreezy.hexrpg.tools.PrimitiveTypeTools;

import java.util.UUID;

public class SpawnLootDropCmd implements CommandExecutor {
	@Inject private HexRPGPlugin plugin;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String command = cmd.getName();

		if (command.equalsIgnoreCase("SpawnLootDrop")) {
			if (sender instanceof Player || sender instanceof ConsoleCommandSender) {
				Location location = null;
				UUID uuid = sender instanceof Player ? ((Player) sender).getUniqueId() : null;

				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (!player.hasPermission("hexrpg.spawnlootdrop")) {
						player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.not-allowed", player.getUniqueId(), true));
						return true;
					}
				}
				
				String usageString = "/SpawnLootDrop <x> <y> <z> <" + LanguageManager.getMessage("literal-translation.world", uuid) + ">";
				if(sender instanceof ConsoleCommandSender) {
					usageString = usageString.replace("<", "[").replace(">", "]");
				}
				
				if (args.length == 0 && sender instanceof Player) {
					location = ((Player) sender).getLocation();
				} else if((sender instanceof Player && args.length >= 3 && args.length <= 4) || (sender instanceof ConsoleCommandSender && args.length >= 4)) {
					String worldName = "";
					if(args.length >= 4) {
						worldName = args[3];
					} else if(args.length <= 3) {
						worldName = ((Player) sender).getLocation().getWorld().getName();
					}

					if(!validateLocationArguments(args[0], args[1], args[2], worldName)) {
						sender.sendMessage(LanguageManager.getMessage("command-and-chat-execution.loot-drop.invalid-coordinates", uuid, true));
						sender.sendMessage(ChatColor.RED + usageString);
						return true;
					}
					
					World world = Bukkit.getServer().getWorld(args[3]);
					int x = Integer.parseInt(args[0]), y = Integer.parseInt(args[1]), z = Integer.parseInt(args[2]);
					location = new Location(world, x, y, z);
				} else {
					sender.sendMessage(LanguageManager.getMessage("command-and-chat-execution.too-few-arguments", uuid, true));
					sender.sendMessage(ChatColor.RED + usageString);
					return true;
				}
				
				LootDrop ld = LootDrop.create(plugin, location, null);
				ld.drop();

				sender.sendMessage(LanguageManager.getMessage("command-and-chat-execution.loot-drop.dropped", uuid, true));
			}
		}

		return false;
	}
	
	private boolean validateLocationArguments(String x, String y, String z, String world) {
		if(!PrimitiveTypeTools.isInt(x)) return false;
		if(!PrimitiveTypeTools.isInt(y)) return false;
		if(!PrimitiveTypeTools.isInt(z)) return false;
		if(Bukkit.getServer().getWorld(world) == null) return false;
		
		return true;
	}
}