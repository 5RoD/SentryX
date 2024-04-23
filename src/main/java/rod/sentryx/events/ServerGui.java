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

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerGui implements CommandExecutor, Listener {


    private static final int REFRESH_INTERVAL = 20; // Refresh every 3 seconds (20 ticks * 3 seconds)
    private static final int GUI_SIZE = 9;


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
        double processlastMin = cpuProcess.poll(StatisticWindow.CpuUsage.SECONDS_10);
        double sysLastMin = cpuUsage.poll(StatisticWindow.CpuUsage.SECONDS_10);

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
                + "\n" + ChatColor.YELLOW + "Process/System load" + ChatColor.WHITE + ": " + ChatColor.GREEN + processlastMin + "% " + sysLastMin + "%"
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
                player.sendMessage("pastelink soon"); //Change for later gonna make it open a pastelink with the server info
            }
        }
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
