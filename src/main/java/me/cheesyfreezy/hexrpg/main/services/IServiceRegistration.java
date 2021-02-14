package me.cheesyfreezy.hexrpg.main.services;

import com.google.inject.Injector;
import me.cheesyfreezy.hexrpg.dependencyinjection.PluginBinder;

public interface IServiceRegistration {
    boolean register(PluginBinder binder, Injector injector);
}