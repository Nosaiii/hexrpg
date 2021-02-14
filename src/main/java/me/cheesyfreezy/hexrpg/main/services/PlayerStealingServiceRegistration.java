package me.cheesyfreezy.hexrpg.main.services;

import com.google.inject.Injector;
import me.cheesyfreezy.hexrpg.dependencyinjection.PluginBinder;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.stealing.PlayerStealingService;

public class PlayerStealingServiceRegistration implements IServiceRegistration {
    @Override
    public boolean register(PluginBinder binder, Injector injector) {
        binder.addSingleton(PlayerStealingService.class);
        return true;
    }
}