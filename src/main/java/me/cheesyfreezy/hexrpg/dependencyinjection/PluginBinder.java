package me.cheesyfreezy.hexrpg.dependencyinjection;

import com.codingforcookies.armorequip.ArmorListener;
import com.codingforcookies.armorequip.DispenserArmorListener;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import me.cheesyfreezy.hexrpg.commands.configuration.HexRPGCmd;
import me.cheesyfreezy.hexrpg.commands.configuration.HexRPGLanguageCmd;
import me.cheesyfreezy.hexrpg.commands.items.*;
import me.cheesyfreezy.hexrpg.commands.shop.CreateshopCmd;
import me.cheesyfreezy.hexrpg.commands.world.SpawnLootDropCmd;
import me.cheesyfreezy.hexrpg.listeners.chat.OnChatProcessor;
import me.cheesyfreezy.hexrpg.listeners.inventory.*;
import me.cheesyfreezy.hexrpg.listeners.item.*;
import me.cheesyfreezy.hexrpg.listeners.world.block.*;
import me.cheesyfreezy.hexrpg.listeners.world.entity.*;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import me.cheesyfreezy.hexrpg.rpg.items.applicable.ApplicableService;
import me.cheesyfreezy.hexrpg.rpg.mechanics.EffectSocketService;
import me.cheesyfreezy.hexrpg.rpg.mechanics.lootdrop.LootDrop;
import me.cheesyfreezy.hexrpg.rpg.mechanics.lootdrop.LootDropService;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.stealing.PlayerStealingService;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.trading.PlayerTradingService;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playershop.PlayerShop;
import me.cheesyfreezy.hexrpg.rpg.quests.QuestParser;
import me.cheesyfreezy.hexrpg.rpg.quests.QuestService;
import me.cheesyfreezy.hexrpg.rpg.quests.reward.factory.QuestRewardFactory;
import me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.ChatProcessorService;
import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PluginBinder extends AbstractModule {
    private final HexRPGPlugin plugin;

    public PluginBinder(HexRPGPlugin plugin) {
        this.plugin = plugin;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    private <T> void addSingleton(Class<T> clazz) {
        bind(clazz).asEagerSingleton();
    }

    private void bindVault() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }

        bind(Economy.class).toInstance(rsp.getProvider());
    }

    @Override
    protected void configure() {
        // Plugin binding
        bind(HexRPGPlugin.class).toInstance(plugin);

        // Library bindings
        bindVault();

        // Command bindings
        addSingleton(HexRPGCmd.class);
        addSingleton(HexRPGLanguageCmd.class);
        addSingleton(GiveBackpackCmd.class);
        addSingleton(GiveRPGEffectSocket.class);
        addSingleton(GiveRPGItemCmd.class);
        addSingleton(GiveRPGScrollCmd.class);
        addSingleton(CreateshopCmd.class);
        addSingleton(SpawnLootDropCmd.class);

        // Listener bindings
        bind(ArmorListener.class).toInstance(new ArmorListener(null));
        addSingleton(DispenserArmorListener.class);
        addSingleton(OnChatProcessor.class);
        addSingleton(OnCustomInventoryClose.class);
        addSingleton(OnInBackpackClick.class);
        addSingleton(OnLanguageSelectorClick.class);
        addSingleton(OnPlayerMenuClick.class);
        addSingleton(OnOpenPlayerMenu.class);
        addSingleton(OnPlayerShopClick.class);
        addSingleton(OnBackpackOpen.class);
        addSingleton(OnRPGApplicableApply.class);
        addSingleton(OnRPGDamageApply.class);
        addSingleton(OnRPGForcedStatsApply.class);
        addSingleton(OnRPGItemDurabilityLoss.class);
        addSingleton(OnRPGPersonalStatsApply.class);
        addSingleton(OnBreakLootDrop.class);
        addSingleton(OnBreakPlayerShop.class);
        addSingleton(OnDisableFarmlandRemoval.class);
        addSingleton(OnOpenLootDrop.class);
        addSingleton(OnOpenPlayerShop.class);
        addSingleton(OnArrowStatsApply.class);
        addSingleton(OnDisableDeathMessage.class);
        addSingleton(OnDisableHostileBurn.class);
        addSingleton(OnDropTableRoll.class);
        addSingleton(OnEntityDeathExperienceGain.class);
        addSingleton(OnFireworkDamage.class);
        addSingleton(OnReobtainArrowStats.class);
        addSingleton(OnRPGItemDrop.class);
        addSingleton(OnStealingStop.class);

        // Service bindings
        addSingleton(ApplicableService.class);
        addSingleton(ChatProcessorService.class);
        addSingleton(EffectSocketService.class);
        addSingleton(LootDropService.class);
        addSingleton(PlayerTradingService.class);
        addSingleton(PlayerStealingService.class);

        // Quest bindings
        addSingleton(QuestRewardFactory.class);
        addSingleton(QuestParser.class);
        addSingleton(QuestService.class);

        // Static bindings
        requestStaticInjection(ConfigFile.class);
        requestStaticInjection(LanguageManager.class);
        requestStaticInjection(PlayerShop.class);
        requestStaticInjection(LootDrop.class);
    }
}
