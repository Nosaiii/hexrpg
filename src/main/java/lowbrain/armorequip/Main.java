package lowbrain.armorequip;

import org.bukkit.plugin.java.JavaPlugin;

public class Main
        extends JavaPlugin
{
    public static Main instance;

    public void onEnable()
    {
        instance = this;

        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new ArmorListener(getConfig().getStringList("blocked")), this);
        getServer().getPluginManager().registerEvents(new MainHandListener(getConfig().getStringList("blocked")), this);
    }
}
