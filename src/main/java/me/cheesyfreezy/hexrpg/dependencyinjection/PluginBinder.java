package me.cheesyfreezy.hexrpg.dependencyinjection;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import me.cheesyfreezy.hexrpg.rpg.mechanics.lootdrop.LootDrop;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playershop.PlayerShop;
import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;

public class PluginBinder extends AbstractModule {
    private final HexRPGPlugin plugin;

    public PluginBinder(HexRPGPlugin plugin) {
        this.plugin = plugin;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    public <T> void addSingleton(Class<T> clazz) {
        bind(clazz).to(clazz).in(Scopes.SINGLETON);
    }

    public <T> void addSingletonInstance(Class<T> clazz, T instance) {
        bind(clazz).toInstance(instance);
    }

    @Override
    protected void configure() {
        bind(HexRPGPlugin.class).toInstance(plugin);

        requestStaticInjection(ConfigFile.class);
        requestStaticInjection(LanguageManager.class);
        requestStaticInjection(PlayerShop.class);
        requestStaticInjection(LootDrop.class);
    }
}
