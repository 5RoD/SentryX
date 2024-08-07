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
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerGui implements CommandExecutor, Listener {

    private static final int REFRESH_INTERVAL = 20; // Refresh every 3 seconds (20 ticks * 3 seconds)
    private static final int GUI_SIZE = 9;
    private static final String API_KEY = ""; // Add Your Own Pastebin API Key
    private static final String API_URL = "https://pastebin.com/api/api_post.php";

    private final JavaPlugin plugin;

    public ServerGui(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players are allowed to run this command."));
            return false;
        }

        Player player = (Player) sender;

        if ("servergui".equalsIgnoreCase(label)) {
            openGui(player);
            return true;
        }

        return false;
    }

    public void openGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, GUI_SIZE, CC.translate("&eServer Information"));
        Bukkit.getScheduler().runTaskTimer(plugin, () -> refreshItem(player, gui), 0, REFRESH_INTERVAL);
        player.openInventory(gui);
    }

    private void refreshItem(Player player, Inventory gui) {
        ItemStack button = createInfoButton(player);
        gui.setItem(4, button);
    }

    private ItemStack createInfoButton(Player player) {
        ItemStack button = new ItemStack(Material.COMMAND_BLOCK_MINECART);
        ItemMeta buttonMeta = button.getItemMeta();

        if (buttonMeta != null) {
            buttonMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            buttonMeta.addEnchant(Enchantment.PROTECTION_FALL, 5, true);
            buttonMeta.setDisplayName(CC.translate("&e&lSERVER INFO"));

            List<String> lore = new ArrayList<>();
            lore.add(CC.translate("&7Click to view server information."));
            Collections.addAll(lore, getServerInfo().split("\n"));

            buttonMeta.setLore(lore);
            button.setItemMeta(buttonMeta);
        }

        return button;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInv = event.getClickedInventory();

        if (clickedInv != null && clickedInv.equals(player.getOpenInventory().getTopInventory())) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getType() == Material.COMMAND_BLOCK_MINECART) {
                player.closeInventory();
                String pastebinUrl = createPastebinPaste(getServerInfo());
                player.sendMessage(CC.translate("&aHere is the link to your server information: &f" + pastebinUrl));
            }
        }
    }

    private String createPastebinPaste(String pasteText) {
        HttpURLConnection connection = null;
        try {
            String pasteName = URLEncoder.encode("Server Info", "UTF-8");
            String pasteCode = URLEncoder.encode(pasteText, "UTF-8");
            String postData = String.format(
                    "api_dev_key=%s&api_option=paste&api_paste_code=%s&api_paste_name=%s&api_paste_expire_date=10M&api_paste_format=text",
                    API_KEY, pasteCode, pasteName
            );

            URL url = new URL(API_URL);
            connection = (HttpURLConnection) url.openConnection();
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

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return CC.translate("&cError creating paste.");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String getServerInfo() {
        Spark spark = SparkProvider.get();
        Server server = Bukkit.getServer();

        String tpsFormatted = formatStatistic(spark.tps(), StatisticWindow.TicksPerSecond.SECONDS_5);
        String msptFormatted = formatDoubleAverageInfo(spark.mspt(), StatisticWindow.MillisPerTick.SECONDS_10);
        String cpuUsageFormatted = formatCpuUsage(spark);
        String memoryUsageFormatted = formatMemoryUsage();

        int maxPlayers = server.getMaxPlayers();
        int onlinePlayers = server.getOnlinePlayers().size();
        int entitiesCount = server.getWorlds().get(0).getEntities().size();
        int ping = Bukkit.getOnlinePlayers().iterator().next().getPing(); // Example, change if needed

        // Use String.format to insert variables into the string
        String info = String.format(
                "&eServer Name: &a%s\n" +
                        "&eOnline players: &a%d/&c%d\n" +
                        "&eEntities count: &a%d\n" +
                        "&eCurrent TPS: &a%s\n" +
                        "&eCurrent MSPT: &a%s\n" +
                        "&eCurrent Ping: &a%d\n" +
                        "&eCPU/Memory info:\n" +
                        "&eSystem/Process CPU Usage: &a%s\n" +
                        "&eTotal Threads: &a%d\n" +
                        "&eMemory Usage: &a%s",
                server.getVersion(), onlinePlayers, maxPlayers, entitiesCount,
                tpsFormatted, msptFormatted, ping, cpuUsageFormatted,
                ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors(),
                memoryUsageFormatted
        );

        return CC.translate(info);
    }

    private String formatStatistic(DoubleStatistic<StatisticWindow.TicksPerSecond> statistic, StatisticWindow.TicksPerSecond window) {
        Double value = statistic.poll(window);
        return value != null ? String.format("%.2f", value) : CC.translate("&cN/A");
    }

    private String formatDoubleAverageInfo(GenericStatistic<DoubleAverageInfo, StatisticWindow.MillisPerTick> statistic, StatisticWindow.MillisPerTick window) {
        DoubleAverageInfo info = statistic.poll(window);
        return info != null ? String.format("%.2f", info.mean()) : CC.translate("&cN/A");
    }

    private String formatCpuUsage(Spark spark) {
        double processUsage = spark.cpuProcess().poll(StatisticWindow.CpuUsage.SECONDS_10);
        double systemUsage = spark.cpuSystem().poll(StatisticWindow.CpuUsage.SECONDS_10);
        return String.format("%.2f%%/%.2f%%", systemUsage, processUsage);
    }

    private String formatMemoryUsage() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = memoryBean.getHeapMemoryUsage();
        long usedMemory = memoryUsage.getUsed() / (1024 * 1024);
        long maxMemory = memoryUsage.getMax() / (1024 * 1024);
        return String.format("%d MB / %d MB", usedMemory, maxMemory);
    }
}
