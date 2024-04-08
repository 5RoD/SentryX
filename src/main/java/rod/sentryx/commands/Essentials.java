package rod.sentryx.commands;

import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.api.statistic.types.DoubleStatistic;
import me.lucko.spark.api.statistic.types.GenericStatistic;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import rod.sentryx.util.CC;
import java.util.Objects;

public class Essentials implements CommandExecutor {



    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players are allowed to run this command"));
            return true;
        }

        Player player = (Player) sender;


        String noPermission = CC.translate("&cYou do not have permission to run this command!");

        switch (label.toLowerCase()) {
            case "gmc":
                if (player.hasPermission("rod.staff.gmc")) {
                    player.setGameMode(GameMode.CREATIVE);
                    player.sendMessage(CC.translate("&eYour gamemode has changed to &aCREATIVE"));
                } else if (!player.hasPermission("rod.staff.gmc")) {
                    player.sendMessage(noPermission);
                }
                break;

            case "gms":
                if (player.hasPermission("rod.staff.gms")) {
                    player.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage(CC.translate("&eYour gamemode has changed to &aSURVIVAL"));
                } else if (!player.hasPermission("rod.staff.gms")) {
                    player.sendMessage(noPermission);
                }
                break;

            case "gmsp":
                if (player.hasPermission("rod.staff.gmsp")) {
                    player.setGameMode(GameMode.SPECTATOR);
                    player.sendMessage(CC.translate("&eYour gamemode has changed to &aSPECTATOR"));
                } else if (!player.hasPermission("rod.staff.gmsp")) {
                    player.sendMessage(noPermission);
                }
                break;

            case "gma":
                if (player.hasPermission("rod.staff.gma")) {
                    player.setGameMode(GameMode.ADVENTURE);
                    player.sendMessage(CC.translate("&eYour gamemode has changed to &aADVENTURE"));
                } else if (!player.hasPermission("rod.staff.gma")) {
                    player.sendMessage(noPermission);
                }
                break;

            case "heal":
                if (player.hasPermission("rod.staff.heal")) {
                    player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
                    player.removePotionEffect(PotionEffectType.POISON);
                    player.setFireTicks(0);
                    player.sendMessage(CC.translate("&eYou have been fully &aHEALED!"));
                } else if (!player.hasPermission("rod.staff.heal")) {
                    player.sendMessage(noPermission);
                }
                break;

            case "feed":
                if (player.hasPermission("rod.feed")) {
                    player.setFoodLevel(20);
                    player.setExhaustion(0);
                    player.sendMessage(CC.translate("&eYou have been fully &aFED!"));
                } else if (!player.hasPermission("rod.feed")) {
                    player.sendMessage(noPermission);
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
                    player.sendMessage(noPermission);
                }
                break;

            case "more":
                if (player.hasPermission("rod.staff.more")) {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (item.getType() == Material.AIR) {
                        player.sendMessage(CC.translate("&cThere is no item in your hand!"));
                        return true;
                    }
                    item.setAmount(64);
                    player.sendMessage(CC.translate("&aYou have 64 items now!"));
                } else if (!player.hasPermission("rod.staff.more")) {
                    player.sendMessage(noPermission);
                }
                break;
        // soon
        //    case "ping":
        //    case "lag":
        //       int ping = player.getPing();
        //        if(ping <= 60) {
        //            player.sendMessage(CC.translate("&ePing&f: &a" + ping + "&aYour ping is amazing!"));
        //
        //        }else if(ping < 150) {
        //            player.sendMessage(CC.translate("&ePing&f: &e" + ping + "&eYour ping is okay"));
        //    }else if(ping >= 160){
        //            player.sendMessage(CC.translate("&ePing&f: &e" + ping + "&cYour ping is lagging fix asap!"));
        //        }
        //        break;

            case "tps":
                if (!player.hasPermission("rod.staff.tps")) {
                    player.sendMessage(noPermission);
                    return true;
                }
                double tps = Bukkit.getTPS()[0];
                double tps1m = Bukkit.getTPS()[1];
                String formattedTps = String.format("%.2f (5s), %.2f (1m)", tps, tps1m);

                if (tps >= 19.2) {
                    player.sendMessage(CC.translate("&eCurrent TPS&f: &a" + formattedTps + " &eThe server is running: &aSmoothly!"));
                } else if (tps >= 18.5) {
                    player.sendMessage(CC.translate("&eCurrent TPS&f: &e" + formattedTps + " &eThe server is running: Okay!"));
                } else {
                    player.sendMessage(CC.translate("&eCurrent TPS&f: &c" + formattedTps + " &eThe server is running: &cLaggy!!"));
                }
                break;
            case "server":
                if (!player.hasPermission("rod.staff.server")) {
                    player.sendMessage(noPermission);
                    return true;
                }
                Spark spark = SparkProvider.get();
                Server server = Bukkit.getServer();


                //GETTING THE TPS HERE
                DoubleStatistic<StatisticWindow.TicksPerSecond> tpsx = spark.tps();
                assert tpsx != null;
                String tpsformatted = String.format("%.2f", tpsx.poll(StatisticWindow.TicksPerSecond.SECONDS_10));

                //GETTING THE MSPT HERE
                GenericStatistic<DoubleAverageInfo, StatisticWindow.MillisPerTick> sparkmspt = spark.mspt();
                assert sparkmspt != null;
                DoubleAverageInfo mspt = sparkmspt.poll(StatisticWindow.MillisPerTick.SECONDS_10);
                double translatedmspt = mspt.mean();
                String formattedmspt = String.format("%.2f", translatedmspt);

                //GETTING THE CPU USAGE HERE
                DoubleStatistic<StatisticWindow.CpuUsage> cpuUsage = spark.cpuSystem();
                DoubleStatistic<StatisticWindow.CpuUsage> cpuProcess = spark.cpuProcess();

                double processlastMin = cpuProcess.poll(StatisticWindow.CpuUsage.SECONDS_10);
                double sysLastMin = cpuUsage.poll(StatisticWindow.CpuUsage.SECONDS_10);

                //SERVER INFORMATION
                String serverName = server.getVersion();
                int maxPlayers = server.getMaxPlayers();
                int onlinePlayers = server.getOnlinePlayers().size();
                String pw = player.getWorld().getName();
                int pingserver = player.getPing();


                player.sendMessage(CC.translate("\n&ePerformance/Server Information&f: \n " + "\n&e"+serverName +"\n&eOnline Players&f: &a"+ onlinePlayers +"&f/&c"+ maxPlayers + "\n&eEntities Count&f: &a"+ server.getWorld(pw).getEntities().size() + "\n&eCurrent TPS&f: &a " + tpsformatted + "\n&eCurrent MSPT&f: &a" + formattedmspt + "\n&eCurrent Ping&f: &a" + pingserver + "\n&eCurrent CPU process/system usage&f: &a" +
                        processlastMin +"% "+ sysLastMin+"%"));



                break;
            }
            return true;
        }


    }

