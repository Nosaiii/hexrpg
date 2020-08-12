package me.cheesyfreezy.hexrpg.rpg.mechanics;

import java.util.ArrayList;
import java.util.Arrays;

import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import de.tr7zw.hexrpg.nbtapi.NBTItem;
import me.cheesyfreezy.hexrpg.main.Plugin;
import me.cheesyfreezy.hexrpg.rpg.items.combatitem.RPGCombatItem;
import me.cheesyfreezy.hexrpg.tools.PrimitiveTypeTools;
import me.cheesyfreezy.hexrpg.tools.VectorUtils;

public class EffectSocketService {
	public static final String FILE_NAME = "effect_sockets.yml";
	
	public void start() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin.getMain(), () -> {
			for(Player player : Bukkit.getOnlinePlayers()) {
				ArrayList<ItemStack> items = new ArrayList<>();
				items.add(player.getInventory().getItemInMainHand());
				items.add(player.getInventory().getItemInOffHand());
				items.addAll(Arrays.asList(player.getInventory().getArmorContents()));
				
				for(ItemStack item : items) {
					if(item == null || item.getType().equals(Material.AIR)) {
						continue;
					}
					
					NBTItem nbtItem = new NBTItem(item);
					if(!nbtItem.hasKey("rpgdata_combatitem")) {
						continue;
					}
					RPGCombatItem rpgItem = nbtItem.getObject("rpgdata_combatitem", RPGCombatItem.class);
					
					playEffect(player, rpgItem);
				}
			}
		}, 0, 20);
	}
	
	private void playEffect(Player player, RPGCombatItem rpgItem) {
		if(rpgItem.getEffectSocketKey() == null) {
			return;
		}
		
		String parsingError = ChatColor.RED + "An error occured while trying to parse an effect socket. Please validate your " + FILE_NAME + " file.";

		ConfigFile c = ConfigFile.getConfig(FILE_NAME);
		String cPath = rpgItem.getEffectSocketKey() + ".";
		
		for(String particleData : c.getStringList(cPath + "particles")) {
			String[] particleDataSplitted = particleData.split(",");
			
			if(!PrimitiveTypeTools.isInt(particleDataSplitted[5])) {
				throw new IllegalArgumentException(parsingError);
			}
			int tickDelay = Integer.parseInt(particleDataSplitted[5]);
			
			Bukkit.getServer().getScheduler().runTaskLater(Plugin.getMain(), () -> {
				Particle particle = null;
				int count = 0;
				double xOffset = 0d, yOffset = 0d, zOffset = 0d;
				
				try {
					particle = Particle.valueOf(particleDataSplitted[0].toUpperCase());
					count = Integer.parseInt(particleDataSplitted[1]);
					
					xOffset = Double.parseDouble(particleDataSplitted[2]);
					yOffset = Double.parseDouble(particleDataSplitted[3]);
					zOffset = Double.parseDouble(particleDataSplitted[4]);
				} catch(Exception e) {
					throw new IllegalArgumentException(parsingError);
				}
				
				Location playerLocation = player.getLocation();
				Vector vectorOffset = VectorUtils.rotateVector(new Vector(zOffset, 0, xOffset), playerLocation.getYaw(), 0);
				Location particleLocation = playerLocation.clone().add(vectorOffset).add(0, yOffset, 0);
				player.getWorld().spawnParticle(particle, particleLocation, count);
			}, tickDelay);
		}
	}
}