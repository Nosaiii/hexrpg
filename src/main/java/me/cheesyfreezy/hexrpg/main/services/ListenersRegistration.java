package me.cheesyfreezy.hexrpg.main.services;

import com.codingforcookies.armorequip.ArmorListener;
import com.codingforcookies.armorequip.DispenserArmorListener;
import com.google.inject.Inject;
import com.google.inject.Injector;
import me.cheesyfreezy.hexrpg.dependencyinjection.PluginBinder;
import me.cheesyfreezy.hexrpg.listeners.chat.OnChatProcessor;
import me.cheesyfreezy.hexrpg.listeners.inventory.*;
import me.cheesyfreezy.hexrpg.listeners.item.*;
import me.cheesyfreezy.hexrpg.listeners.world.block.*;
import me.cheesyfreezy.hexrpg.listeners.world.entity.*;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import me.cheesyfreezy.hexrpg.rpg.tools.Feature;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class ListenersRegistration implements IServiceRegistration {
    @Inject private HexRPGPlugin plugin;

    @Override
    public boolean register(PluginBinder binder, Injector injector) {
        PluginManager pm = Bukkit.getServer().getPluginManager();

        // codingforcookies.armorequip
        pm.registerEvents(new ArmorListener(null), plugin);
        try{
            Class.forName("org.bukkit.event.block.BlockDispenseArmorEvent");
            pm.registerEvents(new DispenserArmorListener(), plugin);
        }catch(Exception ignored){}

        // listeners -> chat
        pm.registerEvents(new OnChatProcessor(), plugin);

        // listeners -> inventory
        pm.registerEvents(new OnCustomInventoryClose(), plugin);
        if(Feature.getFeature("backpacks").isEnabled()) {
            pm.registerEvents(new OnInBackpackClick(), plugin);
        }
        pm.registerEvents(new OnLanguageSelectorClick(), plugin);
        if(Feature.getFeature("player-menu.trading").isEnabled() || Feature.getFeature("player-menu.stealing").isEnabled()) {
            pm.registerEvents(new OnPlayerMenuClick(), plugin);
        }
        if(Feature.getFeature("player-shops").isEnabled()) {
            pm.registerEvents(new OnPlayerShopClick(), plugin);
        }

        // listeners -> item
        if(Feature.getFeature("backpacks").isEnabled()) {
            pm.registerEvents(new OnBackpackOpen(), plugin);
        }
        if(Feature.getFeature("scrolls").isEnabled() || Feature.getFeature("effect-sockets").isEnabled()) {
            pm.registerEvents(new OnRPGApplicableApply(), plugin);
        }
        if(Feature.getFeature("item-stats").isEnabled()) {
            pm.registerEvents(new OnRPGDamageApply(), plugin);
            pm.registerEvents(new OnRPGForcedStatsApply(), plugin);
            pm.registerEvents(new OnRPGItemDurabilityLoss(), plugin);
            pm.registerEvents(new OnRPGPersonalStatsApply(), plugin);
        }

        // listeners -> world -> block
        if(Feature.getFeature("loot-drops").isEnabled()) {
            pm.registerEvents(new OnBreakLootDrop(), plugin);
            pm.registerEvents(new OnOpenLootDrop(), plugin);
        }
        if(Feature.getFeature("player-shops").isEnabled()) {
            pm.registerEvents(new OnBreakPlayerShop(), plugin);
            pm.registerEvents(new OnOpenPlayerShop(), plugin);
        }
        pm.registerEvents(new OnDisableFarmlandRemoval(), plugin);

        // listeners -> world -> entity
        if(Feature.getFeature("item-stats").isEnabled()) {
            pm.registerEvents(new OnArrowStatsApply(), plugin);
            pm.registerEvents(new OnReobtainArrowStats(), plugin);
            pm.registerEvents(new OnRPGItemDrop(), plugin);
        }
        pm.registerEvents(new OnDisableDeathMessage(), plugin);
        pm.registerEvents(new OnDisableHostileBurn(), plugin);
        if(Feature.getFeature("drop-tables").isEnabled()) {
            pm.registerEvents(new OnDropTableRoll(), plugin);
        }
        pm.registerEvents(new OnEntityDeathExperienceGain(), plugin);
        if(Feature.getFeature("loot-drops").isEnabled()) {
            pm.registerEvents(new OnFireworkDamage(), plugin);
        }
        if(Feature.getFeature("player-menu.trading").isEnabled() || Feature.getFeature("player-menu.stealing").isEnabled()) {
            pm.registerEvents(new OnPlayerMenuOpen(), plugin);
        }
        if(Feature.getFeature("player-menu.stealing").isEnabled()) {
            pm.registerEvents(new OnStealingStop(), plugin);
        }

        return true;
    }
}
