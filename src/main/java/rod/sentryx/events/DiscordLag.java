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
        int onlinePlayers = Bukkit.getOnlinePlayers().size(); // Get number of online players

        // Obtain OS and memory usage information
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        int allThreads = osBean.getAvailableProcessors(); // Number of available processors
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = memoryBean.getHeapMemoryUsage(); // Heap memory usage

        // Get Spark instance for TPS, MSPT, and CPU usage
        Spark sparktps = SparkProvider.get();

        // Poll current TPS (Ticks Per Second) over the last 5 seconds
        double currentTps = sparktps.tps().poll(StatisticWindow.TicksPerSecond.SECONDS_5);

        // Calculate average MSPT (Milliseconds Per Tick) over the last 10 seconds
        double averageMspt = sparktps.mspt().poll(StatisticWindow.MillisPerTick.SECONDS_10).mean();
        String formattedMspt = String.format("%.2f", averageMspt); // Format MSPT to 2 decimal places

        // Get maximum and used memory in MB
        final long memMax = memoryUsage.getMax() / (1024 * 1024);
        final long memUsed = memoryUsage.getUsed() / (1024 * 1024);

        // Poll CPU usage for process and system over the last 10 seconds
        double processLoad = sparktps.cpuProcess().poll(StatisticWindow.CpuUsage.SECONDS_10);
        double systemLoad = sparktps.cpuSystem().poll(StatisticWindow.CpuUsage.SECONDS_10);

        // Calculate average ping more efficiently
        int totalPing = Bukkit.getOnlinePlayers().stream().mapToInt(Player::getPing).sum();
        int playerCount = Bukkit.getOnlinePlayers().size();
        int averagePing = playerCount > 0 ? totalPing / playerCount : 0; // Avoid division by zero

        // Build server information string
        String serverInfo = buildServerInfo(
                serverName, // Server name
                onlinePlayers, // Number of online players
                maxPlayers, // Maximum number of players allowed
                averagePing, // Average player ping
                currentTps, // Current Ticks Per Second
                formattedMspt, // Formatted Milliseconds Per Tick
                processLoad, // CPU process load
                systemLoad, // System load
                allThreads, // Number of available threads
                memUsed, // Memory used in MB
                memMax // Maximum memory in MB
        );

        // Escape JSON special characters in server information
        String escapedServerInfo = StringEscapeUtils.escapeJson(serverInfo);

        // Check if TPS is below threshold and send lag notification if so
        if (currentTps <= LAG_THRESHOLD) {
            sendLagNotification(escapedServerInfo); // Send notification with escaped server info
        }
    }

    /**
     * Builds the server information string.
     *
     * @param serverName   The name of the server.
     * @param onlinePlayers The current number of online players.
     * @param maxPlayers    The maximum number of players allowed.
     * @param averagePing   The average ping of the players.
     * @param currentTps    The current Ticks Per Second of the server.
     * @param formattedMspt The formatted Milliseconds Per Tick of the server.
     * @param processLoad   The CPU process load of the server.
     * @param systemLoad    The system load of the server.
     * @param allThreads    The number of available processor threads.
     * @param memUsed       The amount of memory used in MB.
     * @param memMax        The maximum memory available in MB.
     * @return A string containing the server information.
     */
    private String buildServerInfo(
            String serverName,
            int onlinePlayers,
            int maxPlayers,
            int averagePing,
            double currentTps,
            String formattedMspt,
            double processLoad,
            double systemLoad,
            int allThreads,
            long memUsed,
            long memMax
    ) {
        // Improved server info string building with StringBuilder
        StringBuilder sb = new StringBuilder();
        sb.append("**Server Name:** ").append(serverName).append("\n\n");
        sb.append("**Online Players:** ").append(onlinePlayers).append("/").append(maxPlayers).append(" ").append(":busts_in_silhouette: \n\n");
        // ... (similarly append other info)
        sb.append("**Memory Usage:** ").append(memUsed).append("/").append(memMax).append(" ").append(":thermometer:");
        return sb.toString();
    }

    /**
     * Sends a lag notification to a Discord webhook.
     *
     * @param escapedServerInfo The JSON-escaped server information string.
     */
    private void sendLagNotification(String escapedServerInfo) {
        String message = String.format(
                "{\"content\": \"%s\", \"embeds\": [{\"title\": \"%s\", \"description\": \"%s\", \"thumbnail\": {\"url\": \"%s\"}}]}",
                ":warning: Server is lagging! :warning:", // Content of the message
                TITLE, // Title of the embed
                escapedServerInfo, // Description of the embed
                IMAGE_URL // URL of the thumbnail image
        );
        discordWebHook.sendWebhook(message); // Send the webhook message
    }
}
