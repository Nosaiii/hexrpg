package me.cheesyfreezy.hexrpg.main;

import com.google.inject.Injector;
import me.cheesyfreezy.hexrpg.dependencyinjection.PluginBinder;
import me.cheesyfreezy.hexrpg.main.services.*;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.trading.PlayerTradingService;
import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class HexRPGPlugin extends JavaPlugin {
	public final static String PREFIX = ChatColor.GOLD + "[" + ChatColor.RED + "HexRPG" + ChatColor.GOLD + "] ";

	private Injector dependencyInjector;

	@Override
	public void onEnable() {
		setupDependencyInjectionBinder();

		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this, 6929);
	}
	
	@Override
	public void onDisable() {
		PlayerTradingService playerTradingService = dependencyInjector.getInstance(PlayerTradingService.class);
		playerTradingService.closePendingTrades();
	}

	private void setupDependencyInjectionBinder() {
		PluginBinder pluginBinder = new PluginBinder(this);
		dependencyInjector = pluginBinder.createInjector();
		dependencyInjector.injectMembers(this);

		List<IServiceRegistration> serviceRegistrations = new ArrayList<>();
		serviceRegistrations.add(new ApplicableServiceRegistration());
		serviceRegistrations.add(new ChatProcessorServiceRegistration());
		serviceRegistrations.add(new CommandsRegistration());
		serviceRegistrations.add(new ConfigurationApplyService());
		serviceRegistrations.add(new EconomyRegistration());
		serviceRegistrations.add(new EffectSocketServiceRegistration());
		serviceRegistrations.add(new FilesRegistration());
		serviceRegistrations.add(new LootDropServiceRegistration());
		serviceRegistrations.add(new PlayerStealingServiceRegistration());
		serviceRegistrations.add(new PlayerTradingServiceRegistration());

		if(ConfigFile.getConfig("config.yml").getBoolean("economy-settings.vault")) {
			serviceRegistrations.add(new EconomyRegistration());
		}

		for(IServiceRegistration serviceRegistration : serviceRegistrations) {
			boolean result = serviceRegistration.register(pluginBinder, dependencyInjector);

			if(!result) {
				Bukkit.getConsoleSender().sendMessage(PREFIX + ChatColor.DARK_RED + "An error occured while registering the HexRPG services");
			}
		}
	}
}