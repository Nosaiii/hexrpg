package me.cheesyfreezy.hexrpg.rpg.mechanics.lootdrop;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.cheesyfreezy.hexrpg.main.Plugin;
import me.cheesyfreezy.hexrpg.rpg.items.combatitem.RPGCombatItem;
import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import me.cheesyfreezy.hexrpg.tools.PrimitiveTypeTools;
import me.cheesyfreezy.hexrpg.tools.RandomTools;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LootDrop {
	private Location location;
	private LootDropTier tier;

	private int yGoal;
	private boolean dropped;
	private int dropTaskId;

	public static LootDrop create(Location location, @Nullable LootDropTier tier) {
		ConfigFile configFile = ConfigFile.getConfig("loot_drop.yml");

		if (tier == null) {
			double r = RandomTools.getRandomPercentage();
			double rOffset = 0;

			for (LootDropTier t : LootDropTier.values()) {
				double rate = configFile.getDouble("rates." + t.toString().toLowerCase());

				if (r > rOffset && r <= rOffset + rate) {
					tier = t;
					break;
				}

				rOffset += rate;
			}
		}

		int yGoal = location.getWorld().getMaxHeight();
		while (yGoal > 1) {
			yGoal--;
			
			Block block = location.getWorld().getBlockAt(location.getBlockX(), yGoal - 1, location.getBlockZ());
			if (!block.getType().equals(Material.AIR) && !block.getType().equals(Material.WATER)) {
				break;
			}
		}

		File f = getFile();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);

		Location keyLocation = location.clone();
		keyLocation.setY(yGoal);
		String key = getKeyByLocation(keyLocation) + ".";
		c.set(key + "tier", tier.toString().toLowerCase());
		c.set(key + "y-goal", yGoal);
		c.set(key + "dropped", false);

		List<Integer> slotIndexes = IntStream.range(0, tier.getRows() * 9 - 1).boxed().collect(Collectors.toList());
		Collections.shuffle(slotIndexes);

		int[] lootAmount = new int[tier.getRows()];
		for (int i = 0; i < lootAmount.length; i++) {
			lootAmount[i] = new Random().nextInt(2) + 1;
		}

		int[] fillSlots = slotIndexes.stream().limit(IntStream.of(lootAmount).sum()).mapToInt(n -> n).toArray();
		for (int i = 0; i < tier.getRows() * 9; i++) {
			int iFinal = i;
			if (Arrays.stream(fillSlots).anyMatch(slot -> iFinal == slot)) {
				List<String> possibleLoot = configFile.getStringList("loot");
				String[] lootData = possibleLoot.get(new Random().nextInt(possibleLoot.size())).split(":");
				
				ItemStack lootItem = null;
				
				if(Arrays.asList(RPGCombatItem.getCollection()).contains(lootData[0])) {
					ItemStack item = RPGCombatItem.build(lootData[0]);
					NBTItem nbtItem = new NBTItem(item);
					RPGCombatItem rpgCombatItem = nbtItem.getObject("rpgdata_combatitem", RPGCombatItem.class);
					
					rpgCombatItem.setIdentified(configFile.getBoolean("identify-rpg-items." + tier.toString().toLowerCase()), true, null, item);
					
					lootItem = rpgCombatItem.update(item);
				} else {
					try {
						lootItem = new ItemStack(Material.matchMaterial(lootData[0]));
					} catch(IllegalArgumentException e) {
						throw new IllegalArgumentException("An error occured trying to fill the loot drop inventory. The item '" + lootData[0] + "' does not exist!");
					}
				}
				
				String[] amountData = lootData[1].split("-", 2);
				if(amountData.length > 1) {
					if(PrimitiveTypeTools.isInt(amountData[0]) && PrimitiveTypeTools.isInt(amountData[1])) {
						int r1 = Integer.parseInt(amountData[0]), r2 = Integer.parseInt(amountData[1]);
						lootItem.setAmount((int) RandomTools.getRandomRange(Math.min(r1, r2), Math.max(r1, r2)));
					} else {
						throw new ClassCastException("An error occured trying to fill the loot drop inventory. The amount for the item '" + lootData[0] + "' is invalid!");
					}
				} else {
					if(PrimitiveTypeTools.isInt(lootData[1])) {
						lootItem.setAmount(Integer.parseInt(lootData[1]));
					} else {
						throw new ClassCastException("An error occured trying to fill the loot drop inventory. The amount for the item '" + lootData[0] + "' is invalid!");
					}
				}
				
				c.set(key + "content." + i, lootItem);
			} else {
				c.set(key + "content." + i, new ItemStack(Material.AIR));
			}
		}

		try {
			c.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new LootDrop(keyLocation);
	}
	
	public static LootDrop getLootDrop(Location location) {
		File f = LootDrop.getFile();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		
		if(!c.isConfigurationSection(getKeyByLocation(location))) {
			return null;
		}
		
		return new LootDrop(location);
	}
	
	private LootDrop(Location location) {
		this.location = location;

		ConfigurationSection lootDropData = getLootDropData();
		this.tier = LootDropTier.valueOf(lootDropData.getString("tier").toUpperCase());
		this.yGoal = lootDropData.getInt("y-goal");
		this.dropped = lootDropData.getBoolean("dropped");
	}

	public void drop() {
		ConfigFile configFile = ConfigFile.getConfig("loot_drop.yml");

		if(configFile.getBoolean("announce-drop")) {
			String msg = ChatColor.DARK_AQUA + "A " + tier.getColor() + ChatColor.BOLD + WordUtils.capitalizeFully(tier.toString().toLowerCase()) + ChatColor.DARK_AQUA + " loot drop is incoming";
			if(configFile.getBoolean("announce-coordinates")) {
				msg += " at " + ChatColor.GOLD + ChatColor.BOLD + location.getBlockX() + ChatColor.DARK_AQUA + ", " + ChatColor.GOLD + ChatColor.BOLD + location.getBlockY() + ChatColor.DARK_AQUA + ", " + ChatColor.GOLD + ChatColor.BOLD + location.getBlockZ();
			}
			msg += ChatColor.DARK_AQUA + "!";
			
			for(Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(msg);
			}
		}
		
		LootDropDropRunnable lddr = new LootDropDropRunnable(this, yGoal);
		dropTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin.getMain(), lddr, 0, 1);
	}
	
	public void loot() {
		Location locationCentered = location.clone().add(0.5, 0.5, 0.5);
		for(ItemStack item : getLoot()) {
			if(item.getType().equals(Material.AIR)) {
				continue;
			}
			location.getWorld().dropItemNaturally(locationCentered, item);
		}
		
		location.getBlock().setType(Material.AIR);
		location.getWorld().spawnParticle(Particle.BLOCK_CRACK, locationCentered, 1, Material.CHEST.createBlockData());
		
		remove();
	}
	
	public void remove() {
		File f = LootDrop.getFile();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		
		c.set(getKeyByLocation(location), null);
		try {
			c.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void playIdleParticle() {
		Location locationCentered = location.clone().add(0.5, 0.5, 0.5);
		location.getWorld().playEffect(locationCentered, Effect.MOBSPAWNER_FLAMES, null);
	}

	public Location getLocation() {
		return location;
	}

	public LootDropTier getTier() {
		return tier;
	}

	public int getYGoal() {
		return yGoal;
	}

	public boolean hasDropped() {
		return dropped;
	}
	
	private void setDropped(boolean dropped) {
		this.dropped = dropped;
		
		File f = getFile();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		c.set(getKeyByLocation(location) + ".dropped", dropped);
		
		try {
			c.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getDropTaskId() {
		return dropTaskId;
	}

	public ItemStack[] getLoot() {
		ConfigurationSection lootDropData = getLootDropData();
		Set<String> itemKeys = lootDropData.getConfigurationSection("content").getKeys(false);
		
		ItemStack[] loot = new ItemStack[itemKeys.size()];
		
		for (String itemKey : itemKeys) {
			int slot = Integer.parseInt(itemKey);
			loot[slot] = lootDropData.getItemStack("content." + itemKey);
		}
		
		return loot;
	}
	
	public void place() {
		Block b = location.getBlock();
		b.setType(Material.CHEST);
		
		setDropped(true);
	}

	private ConfigurationSection getLootDropData() {
		return YamlConfiguration.loadConfiguration(getFile()).getConfigurationSection(getKeyByLocation(location));
	}

	public static String getKeyByLocation(Location location) {
		return Integer.toString(location.getBlockX()) + ":" + Integer.toString(location.getBlockY()) + ":" + Integer.toString(location.getBlockZ()) + ":" + location.getWorld().getName();
	}
	
	public static Location getLocationByKey(String key) {
		String[] keySplit = key.split(":");
		return new Location(Bukkit.getServer().getWorld(keySplit[3]), Integer.parseInt(keySplit[0]), Integer.parseInt(keySplit[1]), Integer.parseInt(keySplit[2]));
	}

	public static File getFile() {
		return new File(Plugin.getMain().getDataFolder() + "/data/", "lootdrop_data.yml");
	}
}