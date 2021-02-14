package me.cheesyfreezy.hexrpg.main.services;

import com.google.inject.Injector;
import me.cheesyfreezy.hexrpg.dependencyinjection.PluginBinder;
import me.cheesyfreezy.hexrpg.rpg.items.applicable.ApplicableService;

public class ApplicableServiceRegistration implements IServiceRegistration {
    @Override
    public boolean register(PluginBinder binder, Injector injector) {
        binder.addSingleton(ApplicableService.class);
        injector.getBinding(ApplicableService.class).getProvider().get().register();
        return true;
    }
}