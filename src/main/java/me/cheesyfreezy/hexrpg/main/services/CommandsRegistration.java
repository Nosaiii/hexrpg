package me.cheesyfreezy.hexrpg.main.services;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.cheesyfreezy.hexrpg.commands.configuration.HexRPGCmd;
import me.cheesyfreezy.hexrpg.commands.configuration.HexRPGLanguageCmd;
import me.cheesyfreezy.hexrpg.commands.items.*;
import me.cheesyfreezy.hexrpg.commands.shop.CreateshopCmd;
import me.cheesyfreezy.hexrpg.commands.world.SpawnLootDropCmd;
import me.cheesyfreezy.hexrpg.dependencyinjection.PluginBinder;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import me.cheesyfreezy.hexrpg.rpg.tools.Feature;
import me.cheesyfreezy.hexrpg.tools.ConfigFile;

public class CommandsRegistration implements IServiceRegistration {
    @Inject
    private HexRPGPlugin plugin;

    @Override
    public boolean register(PluginBinder binder, Injector injector) {
        plugin.getCommand("hexrpg").setExecutor(new HexRPGCmd());
        plugin.getCommand("hexrpglanguage").setExecutor(new HexRPGLanguageCmd());
        if(Feature.getFeature("backpacks").isEnabled()) {
            plugin.getCommand("givebackpack").setExecutor(new GiveBackpackCmd());
        }
        if(Feature.getFeature("effect-sockets").isEnabled()) {
            plugin.getCommand("giverpgeffectsocket").setExecutor(new GiveRPGEffectSocket());
        }
        if(Feature.getFeature("item-stats").isEnabled()) {
            plugin.getCommand("giverpgitem").setExecutor(new GiveRPGItemCmd());
        }
        if(Feature.getFeature("scrolls").isEnabled()) {
            plugin.getCommand("giverpgscroll").setExecutor(new GiveRPGScrollCmd());
        }
        if(!ConfigFile.getConfig("config.yml").getBoolean("economy-settings.vault")) {
            plugin.getCommand("giverupees").setExecutor(new GiveRupeesCmd());
        }
        if(Feature.getFeature("player-shops").isEnabled()) {
            plugin.getCommand("createshop").setExecutor(new CreateshopCmd());
        }
        if(Feature.getFeature("loot-drops").isEnabled()) {
            plugin.getCommand("spawnlootdrop").setExecutor(new SpawnLootDropCmd());
        }

        return true;
    }
}
