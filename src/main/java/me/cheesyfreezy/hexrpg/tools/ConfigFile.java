package me.cheesyfreezy.hexrpg.tools;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Set;

public class ConfigFile {
    @Inject
    private static HexRPGPlugin plugin;

    private String path;
    private YamlConfiguration config;

    private ConfigFile(String path) {
        this.path = path;
        this.config = YamlConfiguration.loadConfiguration(new File(this.path));
    }

    public static ConfigFile getConfig(String name) {
        return new ConfigFile(plugin.getDataFolder() + File.separator + "config" + File.separator + name);
    }

    public String getString(String path) {
        return this.config.getString(path);
    }

    public Integer getInteger(String path) {
        return this.config.getInt(path);
    }

    public Byte getByte(String path) {
        return (byte) this.config.getInt(path);
    }

    public double getDouble(String path) {
        return this.config.getDouble(path);
    }

    public boolean getBoolean(String path) {
        return this.config.getBoolean(path);
    }

    public Object getObject(String path) {
        return this.config.get(path);
    }

    public List<String> getStringList(String path) {
        return this.config.getStringList(path);
    }

    public Set<String> getRootKeys() {
        return this.config.getKeys(false);
    }

    public ConfigurationSection getConfigurationSection(String path) {
        return this.config.getConfigurationSection(path);
    }

    public boolean isConfigurationSection(String path) {
        return this.config.isConfigurationSection(path);
    }
}