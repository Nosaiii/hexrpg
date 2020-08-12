package me.cheesyfreezy.hexrpg.commands.shop;

import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Directional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.cheesyfreezy.hexrpg.rpg.mechanics.playershop.PlayerShop;

public class CreateshopCmd implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String command = cmd.getName();
		
		if(command.equalsIgnoreCase("Createshop")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				
				if(!player.hasPermission("hexrpg.createshop")) {
					player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.not-allowed", player.getUniqueId(), true));
					return true;
				}
				
				if(args.length == 0) {
					player.getLocation().getBlock().setType(Material.CHEST);
					
					Chest chest = (Chest) player.getLocation().getBlock().getState();
					
					BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
					((Directional) chest.getBlockData()).setFacing(axis[Math.round(player.getLocation().getYaw() / 90f) & 0x3].getOppositeFace());
					
					PlayerShop ps = new PlayerShop(player.getUniqueId());
					ps.create();
					
					player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.personal-shop.created", player.getUniqueId(), true));
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