package me.cheesyfreezy.hexrpg.main.services;

import com.google.inject.Injector;
import me.cheesyfreezy.hexrpg.dependencyinjection.PluginBinder;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.trading.PlayerTradingService;

public class PlayerTradingServiceRegistration implements IServiceRegistration {
    @Override
    public boolean register(PluginBinder binder, Injector injector) {
        binder.addSingleton(PlayerTradingService.class);
        return true;
    }
}
