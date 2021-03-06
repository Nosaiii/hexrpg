package me.cheesyfreezy.hexrpg.main;

import com.codingforcookies.armorequip.ArmorListener;
import com.codingforcookies.armorequip.DispenserArmorListener;
import com.google.inject.Inject;
import com.google.inject.Injector;
import me.cheesyfreezy.hexrpg.commands.configuration.HexRPGCmd;
import me.cheesyfreezy.hexrpg.commands.configuration.HexRPGLanguageCmd;
import me.cheesyfreezy.hexrpg.commands.items.*;
import me.cheesyfreezy.hexrpg.commands.shop.CreateshopCmd;
import me.cheesyfreezy.hexrpg.commands.world.SpawnLootDropCmd;
import me.cheesyfreezy.hexrpg.commands.world.SpawnQuestNpcCmd;
import me.cheesyfreezy.hexrpg.dependencyinjection.PluginBinder;
import me.cheesyfreezy.hexrpg.exceptions.quests.InvalidQuestJsonData;
import me.cheesyfreezy.hexrpg.exceptions.quests.QuestNotFoundException;
import me.cheesyfreezy.hexrpg.listeners.chat.OnChatProcessor;
import me.cheesyfreezy.hexrpg.listeners.inventory.*;
import me.cheesyfreezy.hexrpg.listeners.item.*;
import me.cheesyfreezy.hexrpg.listeners.quests.OnLateRewardReceive;
import me.cheesyfreezy.hexrpg.listeners.quests.OnPersistenceQuestSteps;
import me.cheesyfreezy.hexrpg.listeners.quests.OnQuestStart;
import me.cheesyfreezy.hexrpg.listeners.world.block.*;
import me.cheesyfreezy.hexrpg.listeners.world.entity.*;
import me.cheesyfreezy.hexrpg.rpg.items.applicable.ApplicableService;
import me.cheesyfreezy.hexrpg.rpg.mechanics.EffectSocketService;
import me.cheesyfreezy.hexrpg.rpg.mechanics.lootdrop.LootDropService;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.trading.PlayerTradingService;
import me.cheesyfreezy.hexrpg.rpg.quests.Quest;
import me.cheesyfreezy.hexrpg.rpg.quests.QuestParser;
import me.cheesyfreezy.hexrpg.rpg.quests.QuestService;
import me.cheesyfreezy.hexrpg.rpg.quests.npc.QuestNPC;
import me.cheesyfreezy.hexrpg.rpg.quests.npc.QuestNPCParser;
import me.cheesyfreezy.hexrpg.rpg.tools.Feature;
import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;

public class HexRPGPlugin extends JavaPlugin {
	public final static String PREFIX = ChatColor.GOLD + "[" + ChatColor.RED + "HexRPG" + ChatColor.GOLD + "] ";

	private Injector dependencyInjector;

	@Inject private HexRPGCmd hexRPGCmd;
	@Inject private HexRPGLanguageCmd hexRPGLanguageCmd;
	@Inject private GiveBackpackCmd giveBackpackCmd;
	@Inject private GiveRPGEffectSocket giveRPGEffectSocket;
	@Inject private GiveRPGItemCmd giveRPGItemCmd;
	@Inject private GiveRPGScrollCmd giveRPGScrollCmd;
	@Inject private CreateshopCmd createshopCmd;
	@Inject private SpawnLootDropCmd spawnLootDropCmd;
	@Inject private SpawnQuestNpcCmd spawnQuestNpcCmd;

	@Inject private ArmorListener armorListener;
	@Inject private DispenserArmorListener dispenserArmorListener;
	@Inject private OnChatProcessor onChatProcessor;
	@Inject private OnCustomInventoryClose onCustomInventoryClose;
	@Inject private OnInBackpackClick onInBackpackClick;
	@Inject private OnLanguageSelectorClick onLanguageSelectorClick;
	@Inject private OnPlayerMenuClick onPlayerMenuClick;
	@Inject private OnOpenPlayerMenu onOpenPlayerMenu;
	@Inject private OnPlayerShopClick onPlayerShopClick;
	@Inject private OnBackpackOpen onBackpackOpen;
	@Inject private OnRPGApplicableApply onRPGApplicableApply;
	@Inject private OnRPGDamageApply onRPGDamageApply;
	@Inject private OnRPGForcedStatsApply onRPGForcedStatsApply;
	@Inject private OnRPGItemDurabilityLoss onRPGItemDurabilityLoss;
	@Inject private OnRPGPersonalStatsApply onRPGPersonalStatsApply;
	@Inject private OnBreakLootDrop onBreakLootDrop;
	@Inject private OnBreakPlayerShop onBreakPlayerShop;
	@Inject private OnDisableFarmlandRemoval onDisableFarmlandRemoval;
	@Inject private OnOpenLootDrop onOpenLootDrop;
	@Inject private OnOpenPlayerShop onOpenPlayerShop;
	@Inject private OnArrowStatsApply onArrowStatsApply;
	@Inject private OnDisableDeathMessage onDisableDeathMessage;
	@Inject private OnDisableHostileBurn onDisableHostileBurn;
	@Inject private OnDropTableRoll onDropTableRoll;
	@Inject private OnEntityDeathExperienceGain onEntityDeathExperienceGain;
	@Inject private OnFireworkDamage onFireworkDamage;
	@Inject private OnReobtainArrowStats onReobtainArrowStats;
	@Inject private OnRPGItemDrop onRPGItemDrop;
	@Inject private OnStealingStop onStealingStop;
	@Inject private OnLateRewardReceive onLateRewardReceive;
	@Inject private OnPersistenceQuestSteps onPersistenceQuestSteps;
	@Inject private OnQuestStart onQuestStart;

	@Inject private QuestParser questParser;
	@Inject private QuestService questService;
	@Inject private QuestNPCParser questNPCParser;

	@Override
	public void onEnable() {
		new Metrics(this, 6929);

		setupDependencyInjection();
		setupFiles();
		setupCommands();
		setupListeners();

		if(Feature.getFeature("quests").isEnabled()) {
			setupQuestNPCs();
			setupQuests();
		}

		applyConfigurationSettings();

		if(Feature.getFeature("quests").isEnabled()) {
			for(Player player : Bukkit.getOnlinePlayers()) {
				onPersistenceQuestSteps.initializeQuests(player);
			}
		}
	}
	
	@Override
	public void onDisable() {
		if(Feature.getFeature("player-menu.trading").isEnabled()) {
			PlayerTradingService playerTradingService = dependencyInjector.getInstance(PlayerTradingService.class);
			playerTradingService.closePendingTrades();
		}

		if(Feature.getFeature("quests").isEnabled()) {
			for(Player player : Bukkit.getOnlinePlayers()) {
				onPersistenceQuestSteps.uninitializeQuests(player);
			}
		}
	}

	private void setupDependencyInjection() {
		PluginBinder binder = new PluginBinder(this);
		dependencyInjector = binder.createInjector();
		dependencyInjector.injectMembers(this);

		dependencyInjector.getProvider(ApplicableService.class).get().register();
		dependencyInjector.getProvider(LootDropService.class).get().start();
	}

	private void setupFiles() {
		List<String> fileNames = new ArrayList<>(Arrays.asList(
				"config/config.yml",
				"config/drop_table.yml",
				"config/" + EffectSocketService.FILE_NAME,
				"config/loot_drop.yml",
				"config/player_leveling.yml",
				"config/quest_npcs.json",
				"config/rpgitem.yml",
				"config/scrolls.yml",
				"lang/english.yml",
				"lang/global_lang.yml"
		));

		try {
			URI uri = getClass().getResource("/quests").toURI();
			Map<String, ?> env = new HashMap<>();
			try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
				Path path = zipfs.getPath("quests");
				Files.walk(path)
						.filter(p -> p.toString().matches("^.*\\.json$"))
						.forEach(p -> fileNames.add(p.toString().replaceAll("^/", "")));
			}
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}

		for(String fileName : fileNames) {
			File file = new File(getDataFolder() + File.separator + fileName);

			File parentFolder = file.getParentFile();
			if(!parentFolder.exists()) {
				parentFolder.mkdirs();
			}

			InputStream configInputStream = getResource(fileName);

			boolean replace = false;
			if(!file.exists() || (file.exists() && replace)) {
				try {
					file.createNewFile();

					try (FileOutputStream configOutputStream = new FileOutputStream(file)) {
						BufferedWriter configBufferedWriter = new BufferedWriter(new OutputStreamWriter(configOutputStream));
						Scanner scanner = new Scanner(configInputStream);

						while(scanner.hasNext()) {
							configBufferedWriter.write(scanner.nextLine());
							configBufferedWriter.newLine();
						}

						configBufferedWriter.close();
					} catch(Exception e) {
						if(e instanceof IOException) {
							Bukkit.getConsoleSender().sendMessage(HexRPGPlugin.PREFIX + ChatColor.RED + "An error occured while writing content to a configuration file!");
							return;
						} else {
							e.printStackTrace();
						}
					}
				} catch(IOException e) {
					Bukkit.getConsoleSender().sendMessage(HexRPGPlugin.PREFIX + ChatColor.RED + "An error occured while trying to overwrite an existing configuration file!");
					return;
				}
			}
		}
	}

	private void setupCommands() {
		getCommand("hexrpg").setExecutor(hexRPGCmd);
		getCommand("hexrpglanguage").setExecutor(hexRPGLanguageCmd);
		if(Feature.getFeature("backpacks").isEnabled()) {
			getCommand("givebackpack").setExecutor(giveBackpackCmd);
		}
		if(Feature.getFeature("effect-sockets").isEnabled()) {
			getCommand("giverpgeffectsocket").setExecutor(giveRPGEffectSocket);
		}
		if(Feature.getFeature("item-stats").isEnabled()) {
			getCommand("giverpgitem").setExecutor(giveRPGItemCmd);
		}
		if(Feature.getFeature("scrolls").isEnabled()) {
			getCommand("giverpgscroll").setExecutor(giveRPGScrollCmd);
		}
		if(Feature.getFeature("player-shops").isEnabled()) {
			getCommand("createshop").setExecutor(createshopCmd);
		}
		if(Feature.getFeature("loot-drops").isEnabled()) {
			getCommand("spawnlootdrop").setExecutor(spawnLootDropCmd);
		}
		if(Feature.getFeature("quests").isEnabled()) {
			getCommand("spawnquestnpc").setExecutor(spawnQuestNpcCmd);
		}
	}

	private void setupListeners() {
		PluginManager pm = Bukkit.getServer().getPluginManager();

		// codingforcookies.armorequip
		pm.registerEvents(armorListener, this);
		pm.registerEvents(dispenserArmorListener, this);

		// listeners -> chat
		pm.registerEvents(onChatProcessor, this);

		// listeners -> inventory
		pm.registerEvents(onCustomInventoryClose, this);
		if(Feature.getFeature("backpacks").isEnabled()) {
			pm.registerEvents(onInBackpackClick, this);
		}
		pm.registerEvents(onLanguageSelectorClick, this);
		if(Feature.getFeature("player-menu.trading").isEnabled() || Feature.getFeature("player-menu.stealing").isEnabled()) {
			pm.registerEvents(onPlayerMenuClick, this);
		}
		if(Feature.getFeature("player-shops").isEnabled()) {
			pm.registerEvents(onPlayerShopClick, this);
		}

		// listeners -> item
		if(Feature.getFeature("backpacks").isEnabled()) {
			pm.registerEvents(onBackpackOpen, this);
		}
		if(Feature.getFeature("scrolls").isEnabled() || Feature.getFeature("effect-sockets").isEnabled()) {
			pm.registerEvents(onRPGApplicableApply, this);
		}
		if(Feature.getFeature("item-stats").isEnabled()) {
			pm.registerEvents(onRPGDamageApply, this);
			pm.registerEvents(onRPGForcedStatsApply, this);
			pm.registerEvents(onRPGItemDurabilityLoss, this);
			pm.registerEvents(onRPGPersonalStatsApply, this);
		}

		// listeners -> world -> block
		if(Feature.getFeature("loot-drops").isEnabled()) {
			pm.registerEvents(onBreakLootDrop, this);
			pm.registerEvents(onOpenLootDrop, this);
		}
		if(Feature.getFeature("player-shops").isEnabled()) {
			pm.registerEvents(onBreakPlayerShop, this);
			pm.registerEvents(onOpenPlayerShop, this);
		}
		pm.registerEvents(onDisableFarmlandRemoval, this);

		// listeners -> world -> entity
		if(Feature.getFeature("item-stats").isEnabled()) {
			pm.registerEvents(onArrowStatsApply, this);
			pm.registerEvents(onReobtainArrowStats, this);
			pm.registerEvents(onRPGItemDrop, this);
		}
		pm.registerEvents(onDisableDeathMessage, this);
		pm.registerEvents(onDisableHostileBurn, this);
		if(Feature.getFeature("drop-tables").isEnabled()) {
			pm.registerEvents(onDropTableRoll, this);
		}
		pm.registerEvents(onEntityDeathExperienceGain, this);
		if(Feature.getFeature("loot-drops").isEnabled()) {
			pm.registerEvents(onFireworkDamage, this);
		}
		if(Feature.getFeature("player-menu.trading").isEnabled() || Feature.getFeature("player-menu.stealing").isEnabled()) {
			pm.registerEvents(onOpenPlayerMenu, this);
		}
		if(Feature.getFeature("player-menu.stealing").isEnabled()) {
			pm.registerEvents(onStealingStop, this);
		}
		if(Feature.getFeature("quests").isEnabled()) {
			pm.registerEvents(onLateRewardReceive, this);
			pm.registerEvents(onPersistenceQuestSteps, this);
			pm.registerEvents(onQuestStart, this);
		}
	}

	private void setupQuestNPCs() {
		File questNpcsFile = new File(getDataFolder() + File.separator + "config" + File.separator + "quest_npcs.json");

		QuestNPC[] questNpcs = questNPCParser.parse(questNpcsFile);
		for(QuestNPC questNPC : questNpcs) {
			questService.registerNPC(questNPC);
		}
	}

	private void setupQuests() {
		File questsFolder = new File(getDataFolder() + File.separator + "quests");
		File[] jsonFiles = questsFolder.listFiles((dir, name) -> name.matches("^.*\\.json$"));

		for(File questFile : jsonFiles) {
			try {
				Quest quest = questParser.parse(questFile);
				questService.registerQuest(quest);
			} catch (InvalidQuestJsonData invalidQuestJsonData) {
				invalidQuestJsonData.printStackTrace();
			}
		}

		for(Quest quest : questService.getQuests()) {
			try {
				quest.validateQuestRequirements(questService);
			} catch (QuestNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private void applyConfigurationSettings() {
		if(ConfigFile.getConfig("config.yml").getBoolean("world-settings.disable-storm")) {
			Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
				for(World world : Bukkit.getServer().getWorlds()) {
					world.setStorm(false);
				}
			}, 0, 600);
		}
	}

	public Injector getDependencyInjector() {
		return dependencyInjector;
	}
}