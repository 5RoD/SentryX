package rod.sentryx.events;


import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import org.apache.commons.text.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import rod.sentryx.util.discordWebHook;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;

public class DiscordLag implements Listener {

    private static final double LAG_THRESHOLD = 17.0; // Configurable TPS threshold
    private static final String IMAGE_URL = "https://i.ibb.co/QDV3dqX/6072516.png"; // Predefined image URL
    private static final String TITLE = "Server Info"; // Predefined title for the message

    @EventHandler
    public void onLag() {

        // Server and Java server info
        String serverName = Bukkit.getServer().getVersion(); // Use getServer() for cleaner access
        int maxPlayers = Bukkit.getServer().getMaxPlayers(); // Use getServer() for cleaner access
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        int allThreads = osBean.getAvailableProcessors();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = memoryBean.getHeapMemoryUsage();

        Spark sparktps = SparkProvider.get();

        double currentTps = sparktps.tps().poll(StatisticWindow.TicksPerSecond.SECONDS_5);

        // Calculate average MSPT directly
        double averageMspt = sparktps.mspt().poll(StatisticWindow.MillisPerTick.SECONDS_10).mean();
        String formattedMspt = String.format("%.2f", averageMspt);

        final long memMax = memoryUsage.getMax() / (1024 * 1024);
        final long memUsed = memoryUsage.getUsed() / (1024 * 1024);

        double processLoad = sparktps.cpuProcess().poll(StatisticWindow.CpuUsage.SECONDS_10);
        double systemLoad = sparktps.cpuSystem().poll(StatisticWindow.CpuUsage.SECONDS_10);

        // Calculate average ping more efficiently
        int totalPing = Bukkit.getOnlinePlayers().stream().mapToInt(Player::getPing).sum();
        int playerCount = Bukkit.getOnlinePlayers().size();
        int averagePing = playerCount > 0 ? totalPing / playerCount : 0; // Avoid division by zero

        String serverInfo = buildServerInfo(serverName, onlinePlayers, maxPlayers,
                averagePing, currentTps, formattedMspt,
                processLoad, systemLoad, allThreads, memUsed, memMax);

        String escapedServerInfo = StringEscapeUtils.escapeJson(serverInfo);

        if (currentTps <= LAG_THRESHOLD) {
            sendLagNotification(escapedServerInfo);
        }
    }

    private String buildServerInfo(String serverName, int onlinePlayers, int maxPlayers, int averagePing, double currentTps, String formattedMspt, double processLoad, double systemLoad, int allThreads, long memUsed, long memMax) {
        // Improved server info string building with String builder
        StringBuilder sb = new StringBuilder();
        sb.append("**Server Name:** ").append(serverName).append("\n\n");
        sb.append("**Online Players:** ").append(onlinePlayers).append("/").append(maxPlayers).append(" ").append(":busts_in_silhouette: \n\n");
        // ... (similarly append other info)
        sb.append("**Memory Usage:** ").append(memUsed).append("/").append(memMax).append(" ").append(":thermometer:");
        return sb.toString();
    }

    private void sendLagNotification(String escapedServerInfo) {
        String message = String.format("{\"content\": \"%s\", \"embeds\": [{\"title\": \"%s\", \"description\": \"%s\", \"thumbnail\": {\"url\": \"%s\"}}]}",
                ":warning: Server is lagging! :warning:", TITLE, escapedServerInfo, IMAGE_URL);
        discordWebHook.sendWebhook(message);
    }
}
