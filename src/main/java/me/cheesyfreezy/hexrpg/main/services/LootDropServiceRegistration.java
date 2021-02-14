package me.cheesyfreezy.hexrpg.main.services;

import com.google.inject.Injector;
import me.cheesyfreezy.hexrpg.dependencyinjection.PluginBinder;
import me.cheesyfreezy.hexrpg.rpg.mechanics.lootdrop.LootDropService;

public class LootDropServiceRegistration implements IServiceRegistration {
    @Override
    public boolean register(PluginBinder binder, Injector injector) {
        binder.addSingleton(LootDropService.class);
        injector.getBinding(LootDropService.class).getProvider().get().start();
        return true;
    }
}