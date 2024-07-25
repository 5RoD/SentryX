package rod.sentryx.commands;

import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.api.statistic.types.DoubleStatistic;
import me.lucko.spark.api.statistic.types.GenericStatistic;
import org.apache.commons.text.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import rod.sentryx.util.CC;
import rod.sentryx.util.discordWebHook;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.util.Objects;

import static java.lang.String.format;


public class Essentials implements CommandExecutor, Listener {


    private static final int MAX_ITEM_AMOUNT = 64;
    private static final int MAX_FOOD_LEVEL = 20;
    private static final String NO_PERMISSION_MESSAGE = CC.translate("&cYou do not have permission to run this command!");


    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players are allowed to run this command"));
            return true;
        }


        Player player = (Player) sender;


        switch (label.toLowerCase()) {
            case "gmc":
                if (player.hasPermission("rod.staff.gmc")) {
                    player.setGameMode(GameMode.CREATIVE);
                    player.sendMessage(CC.translate("&eYour gamemode has changed to &aCREATIVE"));
                } else if (!player.hasPermission("rod.staff.gmc")) {
                    player.sendMessage(NO_PERMISSION_MESSAGE);
                }
                break;

            case "gms":
                if (player.hasPermission("rod.staff.gms")) {
                    player.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage(CC.translate("&eYour gamemode has changed to &aSURVIVAL"));
                } else if (!player.hasPermission("rod.staff.gms")) {
                    player.sendMessage(NO_PERMISSION_MESSAGE);
                }
                break;

            case "gmsp":
                if (player.hasPermission("rod.staff.gmsp")) {
                    player.setGameMode(GameMode.SPECTATOR);
                    player.sendMessage(CC.translate("&eYour gamemode has changed to &aSPECTATOR"));
                } else if (!player.hasPermission("rod.staff.gmsp")) {
                    player.sendMessage(NO_PERMISSION_MESSAGE);
                }
                break;

            case "gma":
                if (player.hasPermission("rod.staff.gma")) {
                    player.setGameMode(GameMode.ADVENTURE);
                    player.sendMessage(CC.translate("&eYour gamemode has changed to &aADVENTURE"));
                } else if (!player.hasPermission("rod.staff.gma")) {
                    player.sendMessage(NO_PERMISSION_MESSAGE);
                }
                break;

            case "heal":
                if (player.hasPermission("rod.staff.heal")) {
                    player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
                    player.removePotionEffect(PotionEffectType.POISON);
                    player.setFireTicks(0);
                    player.sendMessage(CC.translate("&eYou have been fully &aHEALED!"));
                } else if (!player.hasPermission("rod.staff.heal")) {
                    player.sendMessage(NO_PERMISSION_MESSAGE);
                }
                break;

            case "feed":
                if (player.hasPermission("rod.feed")) {
                    player.setFoodLevel(MAX_FOOD_LEVEL);
                    player.setExhaustion(0);
                    player.sendMessage(CC.translate("&eYou have been fully &aFED!"));
                } else if (!player.hasPermission("rod.feed")) {
                    player.sendMessage(NO_PERMISSION_MESSAGE);
                }
                break;

            case "cc":
            case "clearchat":
                if (player.hasPermission("rod.staff.clearchat")) {
                    final String emptyspaces = " \n".repeat(150);
                    final String clearmessage = CC.translate("&aChat has been cleared!");
                    Bukkit.getOnlinePlayers().forEach(noob -> noob.sendMessage(emptyspaces));
                    Bukkit.getOnlinePlayers().forEach(noob -> noob.sendMessage(clearmessage));
                } else if (!player.hasPermission("rod.staff.clearchat")) {
                    player.sendMessage(NO_PERMISSION_MESSAGE);
                }
                break;

            case "more":
                if (player.hasPermission("rod.staff.more")) {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (item.getType() == Material.AIR) {
                        player.sendMessage(CC.translate("&cThere is no item in your hand!"));
                        return true;
                    }
                    item.setAmount(MAX_ITEM_AMOUNT);
                    player.sendMessage(CC.translate("&aYou have 64 items now!"));
                } else if (!player.hasPermission("rod.staff.more")) {
                    player.sendMessage(NO_PERMISSION_MESSAGE);
                }
                break;

            case "ping":
                int ping = player.getPing();
                if (ping <= 60) {
                    player.sendMessage(CC.translate("&ePing&f: &a" + ping + "&aYour ping is amazing!"));

                } else if (ping < 150) {
                    player.sendMessage(CC.translate("&ePing&f: &e" + ping + "&eYour ping is okay"));
                } else if (ping >= 160) {
                    player.sendMessage(CC.translate("&ePing&f: &e" + ping + "&cYour ping is lagging fix asap!"));
                }
                break;

            case "tps":
                if (!player.hasPermission("rod.staff.tps")) {
                    player.sendMessage(NO_PERMISSION_MESSAGE);
                    return true;
                }
                Spark sparktps = SparkProvider.get();
                DoubleStatistic<StatisticWindow.TicksPerSecond> sparkTps = sparktps.tps();
                double sparktpsformatted5s = sparkTps.poll(StatisticWindow.TicksPerSecond.SECONDS_5);
                double sparktpsformatted1m = sparkTps.poll(StatisticWindow.TicksPerSecond.MINUTES_1);


                String formattedTps = String.format("%.2f (5s), %.2f (1m)", sparktpsformatted5s, sparktpsformatted1m);

                if (sparktpsformatted5s >= 19.2) {
                    player.sendMessage(CC.translate("&eCurrent TPS&f: &a" + formattedTps + " &eThe server is running: &aSmoothly!"));
                } else if (sparktpsformatted5s >= 18.5) {
                    player.sendMessage(CC.translate("&eCurrent TPS&f: &e" + formattedTps + " &eThe server is running: Okay!"));
                } else {
                    player.sendMessage(CC.translate("&eCurrent TPS&f: &c" + formattedTps + " &eThe server is running: &cLaggy!!"));
                }
                break;
        }
        return false;
    }
}
