package rod.sentryx.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class ConfigManager {

    public JavaPlugin plugin;
    public FileConfiguration config;
    public String Permissions = "permissions.yml";


    public ConfigManager(JavaPlugin plugin, String ThisIsForTheMainThinginTheMainClassCuzItsStupid) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        plugin.saveDefaultConfig();
    }


        public void loadPermissionsFile() {

            File PermsFile = new File(plugin.getDataFolder(), Permissions);
            if(!PermsFile.exists()){
                plugin.saveResource(Permissions, false);

            }
        config = YamlConfiguration.loadConfiguration(PermsFile);
    }


}
