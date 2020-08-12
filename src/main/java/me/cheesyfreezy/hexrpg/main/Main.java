package me.cheesyfreezy.hexrpg.main;

import java.io.*;
import java.util.Scanner;

import me.cheesyfreezy.hexrpg.commands.configuration.HexRPGCmd;
import me.cheesyfreezy.hexrpg.commands.configuration.HexRPGLanguageCmd;
import me.cheesyfreezy.hexrpg.commands.items.*;
import me.cheesyfreezy.hexrpg.listeners.inventory.*;
import me.cheesyfreezy.hexrpg.listeners.item.*;
import me.cheesyfreezy.hexrpg.listeners.world.entity.*;
import me.cheesyfreezy.hexrpg.rpg.tools.Feature;
import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import lowbrain.armorequip.ArmorListener;
import me.cheesyfreezy.hexrpg.bstats.Metrics;
import me.cheesyfreezy.hexrpg.commands.shop.CreateshopCmd;
import me.cheesyfreezy.hexrpg.commands.world.SpawnLootDropCmd;
import me.cheesyfreezy.hexrpg.listeners.chat.OnChatProcessor;
import me.cheesyfreezy.hexrpg.listeners.world.block.OnBreakLootDrop;
import me.cheesyfreezy.hexrpg.listeners.world.block.OnBreakPlayerShop;
import me.cheesyfreezy.hexrpg.listeners.world.block.OnDisableFarmlandRemoval;
import me.cheesyfreezy.hexrpg.listeners.world.block.OnOpenLootDrop;
import me.cheesyfreezy.hexrpg.listeners.world.block.OnOpenPlayerShop;
import me.cheesyfreezy.hexrpg.rpg.items.applicable.ApplicableService;
import me.cheesyfreezy.hexrpg.rpg.mechanics.CustomInventory;
import me.cheesyfreezy.hexrpg.rpg.mechanics.EffectSocketService;
import me.cheesyfreezy.hexrpg.rpg.mechanics.lootdrop.LootDropService;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.menus.PlayerMenuTrade;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.stealing.PlayerStealingService;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.trading.PlayerTradingService;
import me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.ChatProcessorService;

public class Main extends JavaPlugin {
	public final static String PREFIX = ChatColor.GOLD + "[" + ChatColor.RED + "HexRPG" + ChatColor.GOLD + "] ";

	private ChatProcessorService chatProcessorService;
	private LootDropService lootDropService;
	@SuppressWarnings("rawtypes")
	private ApplicableService applicableService;
	private EffectSocketService effectSocketService;
	private PlayerTradingService playerTradingService;
	private PlayerStealingService playerStealingService;

	private Economy vault;
	
	@Override
	public void onEnable() {
		Plugin.setMain(this);
		
		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this, 6929);
		
		registerFiles();
		
		if(ConfigFile.getConfig("config.yml").getBoolean("world-settings.disable-storm")) {
			Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
				for(World world : Bukkit.getServer().getWorlds()) {
					world.setStorm(false);
				}
			}, 0, 600);
		}
		
		registerServices();
		registerCommands();
		registerListeners();

		registerApplicables();

		if(Plugin.getMain().getConfig().getBoolean("economy-settings.vault")) {
			registerEconomy();
		}
	}
	
	@Override
	public void onDisable() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			Inventory inv = player.getOpenInventory().getTopInventory();
			if(inv == null) {
				continue;
			}

			if(CustomInventory.hasCache(inv)) {
				if(CustomInventory.getInventoryObject(inv) instanceof PlayerMenuTrade) {
					PlayerMenuTrade pmt = (PlayerMenuTrade) CustomInventory.getInventoryObject(inv);
					playerTradingService.closeTrade(player, pmt, true);
				} else {
					player.closeInventory();
				}
			}
		}
	}
	
	public ChatProcessorService getChatProcessorService() {
		return chatProcessorService;
	}
	
	@SuppressWarnings("rawtypes")
	public ApplicableService getApplicableService() {
		return applicableService;
	}
	
	public PlayerTradingService getPlayerTradingService() {
		return playerTradingService;
	}
	
	public PlayerStealingService getPlayerStealingService() {
		return playerStealingService;
	}
	
	private void registerFiles() {
		Bukkit.getConsoleSender().sendMessage(PREFIX + ChatColor.GRAY + "Registering files...");

		String[] fileNames = new String[] {
				"config/config.yml",
				"config/drop_table.yml",
				"config/" + EffectSocketService.FILE_NAME,
				"config/loot_drop.yml",
				"config/player_leveling.yml",
				"config/rpgitem.yml",
				"config/scrolls.yml",
				"lang/english.yml",
				"lang/global_lang.yml"
		};

		for(String fileName : fileNames) {
			File configFile = new File(getDataFolder() + File.separator + fileName);

			File parentFolder = configFile.getParentFile();
			if(!parentFolder.exists()) {
				parentFolder.mkdirs();
			}

			InputStream configInputStream = getResource(fileName);

			boolean replace = false;
			if(!configFile.exists() || (configFile.exists() && replace)) {
				try {
					configFile.createNewFile();

					try (FileOutputStream configOutputStream = new FileOutputStream(configFile)) {
						BufferedWriter configBufferedWriter = new BufferedWriter(new OutputStreamWriter(configOutputStream));
						Scanner scanner = new Scanner(configInputStream);

						while(scanner.hasNext()) {
							configBufferedWriter.write(scanner.nextLine());
							configBufferedWriter.newLine();
						}

						configBufferedWriter.close();
					} catch(Exception e) {
						if(e instanceof IOException) {
							Bukkit.getConsoleSender().sendMessage(PREFIX + ChatColor.RED + "An error occured while writing content to a configuration file!");
						} else {
							e.printStackTrace();
						}
					}
				} catch(IOException e) {
					Bukkit.getConsoleSender().sendMessage(PREFIX + ChatColor.RED + "An error occured while trying to overwrite an existing configuration file!");
					continue;
				}
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void registerServices() {
		Bukkit.getConsoleSender().sendMessage(PREFIX + ChatColor.GRAY + "Registering services...");
		
		chatProcessorService = new ChatProcessorService();
		
		lootDropService = new LootDropService();
		lootDropService.start();
		
		applicableService = new ApplicableService();
		
		effectSocketService = new EffectSocketService();
		effectSocketService.start();
		
		playerTradingService = new PlayerTradingService();
		
		playerStealingService = new PlayerStealingService();
	}
	
	private void registerCommands() {
		Bukkit.getConsoleSender().sendMessage(PREFIX + ChatColor.GRAY + "Registering commands...");

		getCommand("hexrpg").setExecutor(new HexRPGCmd());
		getCommand("hexrpglanguage").setExecutor(new HexRPGLanguageCmd());
		if(Feature.getFeature("backpacks").isEnabled()) {
			getCommand("givebackpack").setExecutor(new GiveBackpackCmd());
		}
		if(Feature.getFeature("effect-sockets").isEnabled()) {
			getCommand("giverpgeffectsocket").setExecutor(new GiveRPGEffectSocket());
		}
		if(Feature.getFeature("item-stats").isEnabled()) {
			getCommand("giverpgitem").setExecutor(new GiveRPGItemCmd());
		}
		if(Feature.getFeature("scrolls").isEnabled()) {
			getCommand("giverpgscroll").setExecutor(new GiveRPGScrollCmd());
		}
		if(!getConfig().getBoolean("economy-settings.vault")) {
			getCommand("giverupees").setExecutor(new GiveRupeesCmd());
		}
		if(Feature.getFeature("player-shops").isEnabled()) {
			getCommand("createshop").setExecutor(new CreateshopCmd());
		}
		if(Feature.getFeature("loot-drops").isEnabled()) {
			getCommand("spawnlootdrop").setExecutor(new SpawnLootDropCmd());
		}
	}
	
	private void registerListeners() {
		Bukkit.getConsoleSender().sendMessage(PREFIX + ChatColor.GRAY + "Registering listeners...");
		
		PluginManager pm = Bukkit.getServer().getPluginManager();
		
		// lowbrain.armorequip
		pm.registerEvents(new ArmorListener(null), this);
		
		// listeners -> chat
		pm.registerEvents(new OnChatProcessor(), this);
		
		// listeners -> inventory
		pm.registerEvents(new OnCustomInventoryClose(), this);
		if(Feature.getFeature("backpacks").isEnabled()) {
			pm.registerEvents(new OnInBackpackClick(), this);
		}
		pm.registerEvents(new OnLanguageSelectorClick(), this);
		if(Feature.getFeature("player-menu.trading").isEnabled() || Feature.getFeature("player-menu.stealing").isEnabled()) {
			pm.registerEvents(new OnPlayerMenuClick(), this);
		}
		if(Feature.getFeature("player-shops").isEnabled()) {
			pm.registerEvents(new OnPlayerShopClick(), this);
		}
		
		// listeners -> item
		if(Feature.getFeature("backpacks").isEnabled()) {
			pm.registerEvents(new OnBackpackOpen(), this);
		}
		if(Feature.getFeature("scrolls").isEnabled() || Feature.getFeature("effect-sockets").isEnabled()) {
			pm.registerEvents(new OnRPGApplicableApply(), this);
		}
		if(Feature.getFeature("item-stats").isEnabled()) {
			pm.registerEvents(new OnRPGDamageApply(), this);
			pm.registerEvents(new OnRPGForcedStatsApply(), this);
			pm.registerEvents(new OnRPGItemDurabilityLoss(), this);
			pm.registerEvents(new OnRPGPersonalStatsApply(), this);
		}
		
		// listeners -> world -> block
		if(Feature.getFeature("loot-drops").isEnabled()) {
			pm.registerEvents(new OnBreakLootDrop(), this);
			pm.registerEvents(new OnOpenLootDrop(), this);
		}
		if(Feature.getFeature("player-shops").isEnabled()) {
			pm.registerEvents(new OnBreakPlayerShop(), this);
			pm.registerEvents(new OnOpenPlayerShop(), this);
		}
		pm.registerEvents(new OnDisableFarmlandRemoval(), this);
		
		// listeners -> world -> entity
		if(Feature.getFeature("item-stats").isEnabled()) {
			pm.registerEvents(new OnArrowStatsApply(), this);
			pm.registerEvents(new OnReobtainArrowStats(), this);
			pm.registerEvents(new OnRPGItemDrop(), this);
		}
		pm.registerEvents(new OnDisableDeathMessage(), this);
		pm.registerEvents(new OnDisableHostileBurn(), this);
		if(Feature.getFeature("drop-tables").isEnabled()) {
			pm.registerEvents(new OnDropTableRoll(), this);
		}
		pm.registerEvents(new OnEntityDeathExperienceGain(), this);
		if(Feature.getFeature("loot-drops").isEnabled()) {
			pm.registerEvents(new OnFireworkDamage(), this);
		}
		if(Feature.getFeature("player-menu.trading").isEnabled() || Feature.getFeature("player-menu.stealing").isEnabled()) {
			pm.registerEvents(new OnPlayerMenuOpen(), this);
		}
		if(Feature.getFeature("player-menu.stealing").isEnabled()) {
			pm.registerEvents(new OnStealingStop(), this);
		}
	}
	
	private void registerApplicables() {
		Bukkit.getConsoleSender().sendMessage(PREFIX + ChatColor.GRAY + "Registering applicables...");
		applicableService.register();
	}

	private boolean registerEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		vault = rsp.getProvider();
		return vault != null;
	}

	public Economy getVault() {
		return vault;
	}
}