package me.cheesyfreezy.hexrpg.main.services;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.cheesyfreezy.hexrpg.dependencyinjection.PluginBinder;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class ConfigurationApplyService implements IServiceRegistration {
    @Inject private HexRPGPlugin plugin;

    @Override
    public boolean register(PluginBinder binder, Injector injector) {
        if(ConfigFile.getConfig("config.yml").getBoolean("world-settings.disable-storm")) {
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                for(World world : Bukkit.getServer().getWorlds()) {
                    world.setStorm(false);
                }
            }, 0, 600);
        }

        return true;
    }
}