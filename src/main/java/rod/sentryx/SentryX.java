package rod.sentryx;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import rod.sentryx.commands.Essentials;
import rod.sentryx.events.*;
import rod.sentryx.security.AntiOP;
import rod.sentryx.security.Auth;
import rod.sentryx.security.AuthRegister;
import rod.sentryx.util.ConfigManager;

import java.util.logging.Level;


public final class SentryX extends JavaPlugin implements Listener, CommandExecutor {

    private final Essentials essX = new Essentials();
    private final TamedProtection tamedprotection = new TamedProtection();
    private final JoinMessage joinMessage = new JoinMessage();
    private final bossBar bossBar = new bossBar();
    private final XPMultiplier XPMultiplier = new XPMultiplier();
    private final Auth Auth = new Auth();
    private final DeathMessage DeathMessage = new DeathMessage();
    private final ServerGui ServerGui = new ServerGui(this);






    @Override
    public void onEnable() {



        ConfigManager configManager = new ConfigManager(this, "config.yml");
        configManager.loadConfig();

        ConfigManager permissions = new ConfigManager(this, "permissions.yml");
        configManager.loadPermissionsFile();

        // start op check bruh
        AntiOP antiOP = new AntiOP(this);
        antiOP.startOpCheckTask();

        getLogger().log(Level.SEVERE, "SentryX has loaded!");
        getServer().getPluginManager().registerEvents(new ServerGui(this), this);
        getServer().getPluginManager().registerEvents(bossBar, this);
        getServer().getPluginManager().registerEvents(joinMessage, this);
        getServer().getPluginManager().registerEvents(tamedprotection, this);
        getServer().getPluginManager().registerEvents(XPMultiplier, this);
        getServer().getPluginManager().registerEvents(DeathMessage, this);
        getServer().getPluginManager().registerEvents(Auth, this);



        getCommand("gmc").setExecutor(essX);
        getCommand("auth").setExecutor(Auth);
        getCommand("authregister").setExecutor(new AuthRegister(Auth.getAuthHash()));
        getCommand("servergui").setExecutor(ServerGui);
        getCommand("tps").setExecutor(essX);
        getCommand("server").setExecutor(essX);
        getCommand("gms").setExecutor(essX);
        getCommand("gma").setExecutor(essX);
        getCommand("gmsp").setExecutor(essX);
        getCommand("heal").setExecutor(essX);
        getCommand("feed").setExecutor(essX);
        getCommand("clearchat").setExecutor(essX);
        getCommand("more").setExecutor(essX);

    }

    @Override
    public void onDisable() {
        getLogger().log(Level.SEVERE, "Sentry has successfully shutdown");
    }

}






