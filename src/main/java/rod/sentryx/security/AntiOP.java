package rod.sentryx.security;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rod.sentryx.util.CC;
import rod.sentryx.util.ConfigManager;

public class AntiOP implements Listener {

    public final ConfigManager config;
    public final JavaPlugin plugin;

    public AntiOP(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = new ConfigManager(plugin, "config.yml");
        startOpCheckTask();
    }

    public void startOpCheckTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!config.getOPList("oplist").contains(p.getName()) &&
                        !config.getOPList("oplist").contains(p.getUniqueId().toString()) &&
                        p.isOp()) {
                    p.setOp(false);
                }
            }
        }, 200000L, 200000L);
    }



    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!config.getOPList("oplist").contains(player.getName()) &&
                !config.getOPList("oplist").contains(player.getUniqueId().toString()) &&
                player.isOp()) {
            player.setOp(false);
            player.sendMessage(CC.translate("&cYour op perms have been removed 2"));
        }
    }
}
