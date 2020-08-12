package me.cheesyfreezy.hexrpg.rpg.mechanics.playershop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.cheesyfreezy.hexrpg.main.Plugin;
import me.cheesyfreezy.hexrpg.rpg.mechanics.CustomInventory;

public class PlayerShop extends CustomInventory {
	public static PlayerShop getPlayerShop(Location location) {
		File f = getFile();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		
		for(String uuid : c.getKeys(false)) {
			String locationPath = uuid + ".location.";
			World world = Bukkit.getServer().getWorld(c.getString(locationPath + "world"));
			int x = c.getInt(locationPath + "x"), y = c.getInt(locationPath + "y"), z = c.getInt(locationPath + "z");
			Location loc = new Location(world, x, y, z);
			
			if((int) location.distance(loc) == 0) {
				return new PlayerShop(UUID.fromString(uuid));
			}
		}
		
		return null;
	}

	private UUID uuid;
	private boolean editing;
	private Inventory inv;
	
	private final static int RENAME_SLOT = 0, DELETE_SLOT = 1, UPGRADE_SHOP_SIZE_SLOT = 3, COLLECT_RUPEES_SLOT = 8;

	public PlayerShop(UUID uuid) {
		this.uuid = uuid;
	}

	public void create() {
		File f = getFile();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		
		Player player = Bukkit.getServer().getPlayer(uuid);
		
		int defaultRows = 1;

		c.set(uuid.toString() + ".appearance.name", "Shop of " + Bukkit.getServer().getPlayer(uuid).getName());
		c.set(uuid.toString() + ".appearance.rows", defaultRows);
		c.set(uuid.toString() + ".data.stored-rupees", 0);

		for (int i = 0; i < defaultRows * 9; i++) {
			c.set(uuid.toString() + ".data.content." + i + ".itemstack", new ItemStack(Material.AIR));
			c.set(uuid.toString() + ".data.content." + i + ".price", 0);
		}
		
		Location pLocation = player.getLocation();
		c.set(uuid.toString() + ".location.world", pLocation.getWorld().getName());
		c.set(uuid.toString() + ".location.x", pLocation.getBlockX());
		c.set(uuid.toString() + ".location.y", pLocation.getBlockY());
		c.set(uuid.toString() + ".location.z", pLocation.getBlockZ());
		
		try {
			c.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Location getLocation() {
		ConfigurationSection shopData = getConfigurationData();
		
		String locationPath = "location.";
		World world = Bukkit.getServer().getWorld(shopData.getString(locationPath + "world"));
		int x = shopData.getInt(locationPath + "x"), y = shopData.getInt(locationPath + "y"), z = shopData.getInt(locationPath + "z");
		return new Location(world, x, y, z);
	}
	
	public boolean addSoldItem(ItemStack item, int price) {
		ConfigurationSection shopData = getConfigurationData();
		
		int freeSlot = -1;
		for(int i = 0; i < getShopRows() * 9; i++) {
			if(shopData.getItemStack("data.content." + i + ".itemstack").getType().equals(Material.AIR)) {
				freeSlot = i;
				break;
			}
		}
		
		if(freeSlot == -1) {
			return false;
		}
		
		File f = getFile();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		
		String cPath = uuid.toString() + ".data.content." + freeSlot + ".";
		c.set(cPath + "itemstack", item);
		c.set(cPath + "price", price);
		
		try {
			c.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public void sellSoldItem(SoldItem soldItem, int slot, Player player, boolean addToStoredRupees) {
		Location dropLocation = getLocation();
		for(ItemStack remainingItem : player.getInventory().addItem(soldItem.getItem()).values()) {
			player.getWorld().dropItem(dropLocation, remainingItem);
		}
		
		File f = getFile();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		
		String cPath = uuid.toString() + ".data.content." + slot + ".";
		c.set(cPath + "itemstack", new ItemStack(Material.AIR));
		c.set(cPath + "price", 0);
		
		if(addToStoredRupees) {
			c.set(uuid.toString() + ".data.stored-rupees", getStoredRupees() + soldItem.getPrice());
		}
		
		try {
			c.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void open(Player player, boolean editing) {
		this.editing = editing;
		
		String shopName = ChatColor.translateAlternateColorCodes('&', getShopName());
		int shopRows = getShopRows();
		if(editing) {
			shopName += ChatColor.RED + " (" + LanguageManager.getMessage("literal-translations.editing", player.getUniqueId()) + ")";
			shopRows += 2;
		}
		
		inv = Bukkit.getServer().createInventory(player, shopRows * 9, shopName);

		update(player);

		player.openInventory(inv);
		addToCache(inv, this);
		
		PlayerShopInventoryUpdater psiu = new PlayerShopInventoryUpdater(player);
		Runnable psiuRunnable = psiu.getRunnable();
		psiu.setTaskId(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin.getMain(), psiuRunnable, 0, 2));
	}
	
	public void update(Player player) {
		updateSoldItemsSection(player);
		
		if(editing) {
			updateManagingSection(player);
		}
	}
	
	public void delete() {
		Block block = getLocation().getBlock();

		Location dropLocation = block.getLocation().clone().add(0.5, 0.5, 0.5);
		for(ItemStack item : Arrays.stream(getSoldItems())
				.map(soldItem -> soldItem.getItem())
				.filter(item -> item != null && !item.getType().equals(Material.AIR))
				.collect(Collectors.toList())) {
			block.getWorld().dropItem(dropLocation, item);
		}

		BlockData blockBreakData = block.getType().createBlockData();
		block.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation(), 5, 0.5, 0.5, 0.5, blockBreakData);
		block.setType(Material.AIR);
		
		File f = getFile();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);

		c.set(uuid.toString(), null);

		try {
			c.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void updateSoldItemsSection(Player player) {
		SoldItem[] soldItems = getSoldItems();
		for (int i = 0; i < getShopRows() * 9; i++) {
			if (soldItems[i] == null || soldItems[i].getItem() == null || soldItems[i].getItem().getType().equals(Material.AIR)) {
				inv.setItem(i, new ItemStack(Material.AIR));
				continue;
			}
			
			ItemStack item = soldItems[i].getItem();
			ItemMeta meta = item.getItemMeta();

			ArrayList<String> lore = meta.hasLore() ? (ArrayList<String>) meta.getLore() : new ArrayList<>();
			lore.add("");
			lore.add(ChatColor.GRAY + LanguageManager.getMessage("literal-translations.price", player.getUniqueId()) + ": " + ChatColor.BLUE + soldItems[i].getPrice() + " " + getPriceLabel(player, soldItems[i].getPrice()));
			
			meta.setLore(lore);
			item.setItemMeta(meta);
			
			inv.setItem(i, item);
		}
	}

	private void updateManagingSection(Player player) {
		int borderOffset = getShopRows() * 9;
		int managingButtonsOffset = borderOffset + 9;
		for(int i = borderOffset; i < borderOffset + 9; i++) {
			ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
			ItemMeta borderMeta = border.getItemMeta();
			borderMeta.setDisplayName(ChatColor.RESET + "");
			border.setItemMeta(borderMeta);
			
			inv.setItem(i, border);
		}

		int storedRupeesAmount = getStoredRupees();
		String priceLabel = getPriceLabel(player, storedRupeesAmount);

		ItemStack storedRupees = new ItemStack(Material.EMERALD);
		ItemMeta storedRupeesMeta = storedRupees.getItemMeta();
		storedRupeesMeta.setDisplayName("" + ChatColor.GREEN + storedRupeesAmount + " " + priceLabel);
		storedRupeesMeta.setLore(new ArrayList<String>() {{
			add(LanguageManager.getMessage("personal-shop.left-click-to-collect", player.getUniqueId(), true, Integer.toString(storedRupeesAmount), priceLabel));
		}});
		storedRupees.setItemMeta(storedRupeesMeta);
		inv.setItem(managingButtonsOffset + COLLECT_RUPEES_SLOT, storedRupees);
		
		ItemStack renameOption = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
		ItemStack deleteOption = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		ItemStack upgradeSlotsOption = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);

		ItemMeta renameOptionMeta = renameOption.getItemMeta();
		ItemMeta deleteOptionMeta = deleteOption.getItemMeta();
		ItemMeta upgradeSlotsOptionMeta = upgradeSlotsOption.getItemMeta();

		renameOptionMeta.setDisplayName(ChatColor.BLUE + LanguageManager.getMessage("literal-translations.rename", player.getUniqueId()));
		deleteOptionMeta.setDisplayName(ChatColor.RED + LanguageManager.getMessage("literal-translations.delete", player.getUniqueId()));
		upgradeSlotsOptionMeta.setDisplayName(ChatColor.YELLOW + LanguageManager.getMessage("literal-translations.upgrade-slots", player.getUniqueId()));

		upgradeSlotsOptionMeta.setLore(new ArrayList<String>() {{
			add(LanguageManager.getMessage("personal-shop.left-click-to-upgrade-slots", player.getUniqueId(), true,
					Integer.toString(ConfigFile.getConfig("config.yml").getInteger("player-shop-settings.upgrade-slots-cost")),
					priceLabel));
		}});

		renameOption.setItemMeta(renameOptionMeta);
		deleteOption.setItemMeta(deleteOptionMeta);
		upgradeSlotsOption.setItemMeta(upgradeSlotsOptionMeta);

		inv.setItem(managingButtonsOffset + RENAME_SLOT, renameOption);
		inv.setItem(managingButtonsOffset + DELETE_SLOT, deleteOption);
		if(getShopRows() < ConfigFile.getConfig("config.yml").getInteger("player-shop-settings.max-rows"))
			inv.setItem(managingButtonsOffset + UPGRADE_SHOP_SIZE_SLOT, upgradeSlotsOption);
	}

	public void upgradeSlots(Player player) {
		File f = getFile();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);

		c.set(uuid.toString() + ".appearance.rows", getShopRows() + 1);

		try {
			c.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}

		open(player, editing);
	}
	
	public boolean isOwner(Player player) {
		return player.getUniqueId().equals(uuid);
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public boolean isEditing() {
		return editing;
	}
	
	public int getRenameOptionSlot() {
		return getShopRows() * 9 + 9 + RENAME_SLOT;
	}
	
	public int getDeleteOptionSlot() {
		return getShopRows() * 9 + 9 + DELETE_SLOT;
	}

	public int getUpgradeShopSizeSlot() {
		return getShopRows() * 9 + 9 + UPGRADE_SHOP_SIZE_SLOT;
	}
	
	public int getCollectRupeesOptionSlot() {
		return getShopRows() * 9 + 9 + COLLECT_RUPEES_SLOT;
	}

	public void setShopName(String name) {
		File f = getFile();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);

		c.set(uuid.toString() + ".appearance.name", name);

		try {
			c.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getShopName() {
		return getConfigurationData().getString("appearance.name");
	}

	public int getShopRows() {
		return getConfigurationData().getInt("appearance.rows");
	}

	public void setStoredRupees(int amount) {
		File f = getFile();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);

		c.set(uuid.toString() + ".data.stored-rupees", amount);

		try {
			c.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getStoredRupees() {
		return getConfigurationData().getInt("data.stored-rupees");
	}

	public SoldItem[] getSoldItems() {
		int slots = getShopRows() * 9;
		SoldItem[] items = new SoldItem[slots];
		
		for (int i = 0; i < slots; i++) {
			String cPath = "data.content." + i + ".";
			ItemStack item = getConfigurationData().getItemStack(cPath + "itemstack");
			
			if (item != null) {
				items[i] = new SoldItem(item, getConfigurationData().getInt(cPath + "price"));
			}
		}

		return items;
	}
	
	public SoldItem getSoldItem(int slot) {
		String cPath = "data.content." + slot + ".";
		
		if(getConfigurationData().getItemStack(cPath + "itemstack") == null) {
			return null;
		}
		
		return new SoldItem(getConfigurationData().getItemStack(cPath + "itemstack"), getConfigurationData().getInt(cPath + "price"));
	}
	
	public ConfigurationSection getConfigurationData() {
		return YamlConfiguration.loadConfiguration(getFile()).getConfigurationSection(uuid.toString());
	}
	
	public static File getFile() {
		return new File(Plugin.getMain().getDataFolder() + "/data/", "shop_data.yml");
	}

	public static String getPriceLabel(Player player, int priceValue) {
		String priceLabel = "";
		boolean singularPrice = priceValue == 1;
		if(Plugin.getMain().getVault() == null) {
			priceLabel = singularPrice ? LanguageManager.getMessage("literal-translations.rupees", player.getUniqueId()) : LanguageManager.getMessage("literal-translations.rupee", player.getUniqueId());
		} else {
			priceLabel = singularPrice ? Plugin.getMain().getVault().currencyNameSingular() : Plugin.getMain().getVault().currencyNamePlural();
		}

		return priceLabel;
	}
}