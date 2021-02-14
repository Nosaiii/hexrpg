package me.cheesyfreezy.hexrpg.main.services;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.cheesyfreezy.hexrpg.dependencyinjection.PluginBinder;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyRegistration implements IServiceRegistration {
    @Inject private HexRPGPlugin plugin;

    @Override
    public boolean register(PluginBinder binder, Injector injector) {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        binder.addSingletonInstance(Economy.class, rsp.getProvider());

        return true;
    }
}
