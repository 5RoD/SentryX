package rod.sentryx;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import rod.sentryx.commands.Essentials;
import rod.sentryx.commands.Stats;
import rod.sentryx.events.*;
import rod.sentryx.fun.ElytraFun;
import rod.sentryx.security.Auth;
import rod.sentryx.security.AuthRegister;
import rod.sentryx.util.ConfigManager;

import java.util.logging.Level;

//Version 1.6
public final class SentryX extends JavaPlugin implements Listener, CommandExecutor {

    private final Essentials essX = new Essentials();
    private final ElytraFun elytraFun = new ElytraFun();
    private final TamedProtection tamedprotection = new TamedProtection();
    private final JoinMessage joinMessage = new JoinMessage();
    private final bossBar bossBar = new bossBar();
    private final XPMultiplier XPMultiplier = new XPMultiplier();
    private final Auth Auth = new Auth();
    private final DeathMessage DeathMessage = new DeathMessage();
    private final EntityTracker entityTracker = new EntityTracker();
    private final Stats stats = new Stats(entityTracker);
    private final ServerGui ServerGui = new ServerGui(this);






    @Override
    public void onEnable() {



        ConfigManager permissions = new ConfigManager(this, "permissions.yml");


        getLogger().log(Level.SEVERE, "SentryX 1.7 has loaded!");
        getServer().getPluginManager().registerEvents(new ServerGui(this), this);
        getServer().getPluginManager().registerEvents(bossBar, this);
        getServer().getPluginManager().registerEvents(joinMessage, this);
        getServer().getPluginManager().registerEvents(tamedprotection, this);
        getServer().getPluginManager().registerEvents(XPMultiplier, this);
        getServer().getPluginManager().registerEvents(DeathMessage, this);
        getServer().getPluginManager().registerEvents(Auth, this);
        getServer().getPluginManager().registerEvents(entityTracker, this);
        getServer().getPluginManager().registerEvents(elytraFun, this);





        getCommand("gmc").setExecutor(essX);
        getCommand("stats").setExecutor(stats);
        getCommand("resetstats").setExecutor(stats);
        getCommand("topstats").setExecutor(stats);
        getCommand("auth").setExecutor(Auth);
        getCommand("authregister").setExecutor(new AuthRegister(Auth.getAuthHash()));
        getCommand("authreset").setExecutor(Auth);
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
        getCommand("infiniteelytra").setExecutor(elytraFun);

    }

    @Override
    public void onDisable() {
        getLogger().log(Level.SEVERE, "Sentry has shutdown");
    }

}






