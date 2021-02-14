package me.cheesyfreezy.hexrpg.main.services;

import com.google.inject.Injector;
import me.cheesyfreezy.hexrpg.dependencyinjection.PluginBinder;
import me.cheesyfreezy.hexrpg.rpg.mechanics.EffectSocketService;

public class EffectSocketServiceRegistration implements IServiceRegistration {
    @Override
    public boolean register(PluginBinder binder, Injector injector) {
        binder.addSingleton(EffectSocketService.class);
        return true;
    }
}