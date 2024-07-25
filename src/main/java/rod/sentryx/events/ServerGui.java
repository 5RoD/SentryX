package rod.sentryx.events;

import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.api.statistic.types.DoubleStatistic;
import me.lucko.spark.api.statistic.types.GenericStatistic;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import rod.sentryx.util.CC;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ServerGui implements CommandExecutor, Listener {

    private static final int REFRESH_INTERVAL = 20; // Refresh every 3 seconds (20 ticks * 3 seconds)
    private static final int GUI_SIZE = 9;
    private static final String API_KEY = ""; // Add Your Own Pastebin API Key
    private static final String API_URL = "https://pastebin.com/api/api_post.php";

    private final JavaPlugin plugin;

    public ServerGui(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public @NotNull Inventory openGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, GUI_SIZE, "Server information");
        Bukkit.getScheduler().runTaskTimer(plugin, () -> refreshItem(player, gui), 0, REFRESH_INTERVAL);
        player.openInventory(gui);
        return gui;
    }

    private static void refreshItem(Player player, Inventory gui) {
        ItemStack button = new ItemStack(Material.COMMAND_BLOCK_MINECART);
        ItemMeta buttonMeta = button.getItemMeta();
        buttonMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        buttonMeta.addEnchant(Enchantment.PROTECTION_FALL, 5, true);
        buttonMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lSERVER INFO"));

        //ALL IMPORTS FOR THE GUI INFO:
        Spark spark = SparkProvider.get();
        Server server = Bukkit.getServer();

        //GETTING THE TPS HERE
        DoubleStatistic<StatisticWindow.TicksPerSecond> tpsx = spark.tps();
        assert tpsx != null;
        String tpsformatted = String.format("%.2f", tpsx.poll(StatisticWindow.TicksPerSecond.SECONDS_5));

        //GETTING THE MSPT HERE
        GenericStatistic<DoubleAverageInfo, StatisticWindow.MillisPerTick> sparkmspt = spark.mspt();
        DoubleAverageInfo mspt = sparkmspt.poll(StatisticWindow.MillisPerTick.SECONDS_10);
        double translatedmspt = mspt.mean();
        String formattedmspt = String.format("%.2f", translatedmspt);

        //GETTING THE CPU USAGE HERE
        DoubleStatistic<StatisticWindow.CpuUsage> cpuUsage = spark.cpuSystem();
        DoubleStatistic<StatisticWindow.CpuUsage> cpuProcess = spark.cpuProcess();

        // Poll CPU usage values
        double processLast10s = cpuProcess.poll(StatisticWindow.CpuUsage.SECONDS_10);
        double sysLast10s = cpuUsage.poll(StatisticWindow.CpuUsage.SECONDS_10);

        // Format the CPU usage values with 2 decimal places
        String formattedCpuUsage = String.format("%.2f%%/%.2f%%", sysLast10s, processLast10s);

        //BUKKIT SERVER INFORMATION
        String serverName = server.getVersion();
        int maxPlayers = server.getMaxPlayers();
        int onlinePlayers = server.getOnlinePlayers().size();
        String pw = player.getWorld().getName();
        int pingserver = player.getPing();

        //JAVA SERVER INFO
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        int allThreads = osBean.getAvailableProcessors();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = memoryBean.getHeapMemoryUsage();

        final long memMax = memoryUsage.getMax() / (1024 * 1024);
        final long memUsed = memoryUsage.getUsed() / (1024 * 1024);

        String serverInfo = "\n" + ChatColor.GREEN + serverName
                + "\n "
                + " \n" + ChatColor.YELLOW + "Online players" + ChatColor.WHITE + ": " + ChatColor.GREEN + onlinePlayers + ChatColor.WHITE + "/" + ChatColor.RED + maxPlayers
                + "\n" + ChatColor.YELLOW + "Entities count" + ChatColor.WHITE + ": " + ChatColor.GREEN + server.getWorld(pw).getEntities().size()
                + "\n" + ChatColor.YELLOW + "Current tps" + ChatColor.WHITE + ": " + ChatColor.GREEN + tpsformatted
                + "\n" + ChatColor.YELLOW + "Current mspt" + ChatColor.WHITE + ": " + ChatColor.GREEN + formattedmspt
                + "\n" + ChatColor.YELLOW + "Current ping" + ChatColor.WHITE + ": " + ChatColor.GREEN + pingserver
                + "\n "
                + " \n" + ChatColor.YELLOW + "CPU/RAM info" + ChatColor.WHITE + ": " + ChatColor.GREEN
                + "\n" + ChatColor.YELLOW + "System/Process" + ChatColor.WHITE + ": " + ChatColor.GREEN + formattedCpuUsage
                + "\n" + ChatColor.YELLOW + "Total threads" + ChatColor.WHITE + ": " + ChatColor.GREEN + allThreads
                + "\n" + ChatColor.YELLOW + "Memory usage" + ChatColor.WHITE + ": " + ChatColor.GREEN + memUsed + ChatColor.WHITE + "/" + ChatColor.RED + memMax;

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to view server information.");

        // Split the serverInfo string into lines and add each line to the lore
        Collections.addAll(lore, serverInfo.split("\n"));

        buttonMeta.setLore(lore);
        button.setItemMeta(buttonMeta);

        gui.setItem(4, button);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory clickedInv = e.getClickedInventory();

        if (clickedInv != null && clickedInv.equals(player.getOpenInventory().getTopInventory())) {
            e.setCancelled(true);
            ItemStack clickedItem = e.getCurrentItem();

            if (clickedItem != null && clickedItem.getType() == Material.COMMAND_BLOCK_MINECART) {
                player.closeInventory();

                // Create paste and get the URL
                String pastebinUrl = createPastebinPaste(getServerInfo());
                player.sendMessage("Here is the link to your server information: " + pastebinUrl);
            }
        }
    }

    private String createPastebinPaste(String pasteText) {
        try {
            String pasteName = URLEncoder.encode("Server Info", "UTF-8");
            String pasteCode = URLEncoder.encode(pasteText, "UTF-8");
            String postData = String.format("api_dev_key=%s&api_option=paste&api_paste_code=%s&api_paste_name=%s&api_paste_expire_date=10M&api_paste_format=text",
                    API_KEY, pasteCode, pasteName);

            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(postData.getBytes("UTF-8"));
                os.flush();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            return response.toString(); // This should return the URL of the paste
        } catch (Exception e) {
            e.printStackTrace();
            return "Error creating paste.";
        }
    }

    private String getServerInfo() {
        Spark spark = SparkProvider.get();
        Server server = Bukkit.getServer();

        //GETTING THE TPS HERE
        DoubleStatistic<StatisticWindow.TicksPerSecond> tpsx = spark.tps();
        assert tpsx != null;
        String tpsformatted = String.format("%.2f", tpsx.poll(StatisticWindow.TicksPerSecond.SECONDS_5));

        //GETTING THE MSPT HERE
        GenericStatistic<DoubleAverageInfo, StatisticWindow.MillisPerTick> sparkmspt = spark.mspt();
        DoubleAverageInfo mspt = sparkmspt.poll(StatisticWindow.MillisPerTick.SECONDS_10);
        double translatedmspt = mspt.mean();
        String formattedmspt = String.format("%.2f", translatedmspt);

        //GETTING THE CPU USAGE HERE
        DoubleStatistic<StatisticWindow.CpuUsage> cpuUsage = spark.cpuSystem();
        DoubleStatistic<StatisticWindow.CpuUsage> cpuProcess = spark.cpuProcess();

        // Poll CPU usage values
        double processLast10s = cpuProcess.poll(StatisticWindow.CpuUsage.SECONDS_10);
        double sysLast10s = cpuUsage.poll(StatisticWindow.CpuUsage.SECONDS_10);

        // Format the CPU usage values with 2 decimal places
        String formattedCpuUsage = String.format("%.2f%%/%.2f%%", sysLast10s, processLast10s);

        //BUKKIT SERVER INFORMATION
        String serverName = server.getVersion();
        int maxPlayers = server.getMaxPlayers();
        int onlinePlayers = server.getOnlinePlayers().size();
        String pw = Bukkit.getWorlds().get(0).getName();
        int pingserver = Bukkit.getPlayer(Bukkit.getOnlinePlayers().iterator().next().getUniqueId()).getPing(); // Just an example

        //JAVA SERVER INFO
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        int allThreads = osBean.getAvailableProcessors();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = memoryBean.getHeapMemoryUsage();

        final long memMax = memoryUsage.getMax() / (1024 * 1024);
        final long memUsed = memoryUsage.getUsed() / (1024 * 1024);

        return "Server Name: " + serverName
                + "\nOnline players: " + onlinePlayers + "/" + maxPlayers
                + "\nEntities count: " + server.getWorld(pw).getEntities().size()
                + "\nCurrent TPS: " + tpsformatted
                + "\nCurrent MSPT: " + formattedmspt
                + "\nCurrent Ping: " + pingserver
                + "\nCPU/Memory info:"
                + "\nSystem/Process CPU Usage: " + formattedCpuUsage
                + "\nTotal Threads: " + allThreads
                + "\nMemory Usage: " + memUsed + "/" + memMax + " MB";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players are allowed to run this command"));
            return false; //return false for the boolean value
        }
        try {
            Player player = (Player) sender;
            if ("servergui".equalsIgnoreCase(label)) {
                player.openInventory(openGui(player));
            }
            return false; //return false for the command to work

        } catch (Exception e) {
            System.err.println("An error occurred while executing the command: " + e.getMessage());
            return false;
        }
    }
}
