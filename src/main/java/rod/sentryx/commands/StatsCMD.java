package rod.sentryx.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import rod.sentryx.events.EntityTracker;
import rod.sentryx.util.CC;

import java.util.HashMap;
import java.util.UUID;

public class StatsCMD implements CommandExecutor {

    private final EntityTracker entityTracker;

    /**
     * Constructor for the Stats command executor.
     *
     * @param entityTracker The EntityTracker instance that keeps track of player statistics.
     */
    public StatsCMD(EntityTracker entityTracker) {
        this.entityTracker = entityTracker;
    }

    /**
     * Handles the command execution.
     *
     * @param sender The sender of the command (should be a player in this case).
     * @param command The command that was executed.
     * @param label The alias of the command used.
     * @param args The arguments passed with the command.
     * @return true if the command was handled successfully, false otherwise.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players are allowed to run this command"));
            return true;
        }

        Player player = (Player) sender;

        switch (label.toLowerCase()) {
            case "stats":
                // Check if the player has permission to view stats
                if (!player.hasPermission("rod.stats")) {
                    player.sendMessage(CC.translate("&cSorry, you do not have permission to run this command"));
                    return true;
                }

                // Ensure the player provided a target player name
                if (args.length == 0) {
                    player.sendMessage(CC.translate("&cPlease check player stats with &a/stats <playername>"));
                    return true;
                }

                // Retrieve the target player by name
                String statsName = args[0];
                Player statsTarget = player.getServer().getPlayerExact(statsName);

                // Check if the target player is online
                if (statsTarget != null) {
                    UUID playerId = statsTarget.getUniqueId();
                    int[] stats = entityTracker.getHashStats().getOrDefault(playerId, new int[]{0, 0, 0});

                    // Send the stats to the requesting player
                    player.sendMessage(CC.translate("&e" + statsName + "'s stats:\n" +
                            "&fBlocks mined: &a" + stats[0] +
                            "\n&fBlocks placed: &a" + stats[1] +
                            "\n&fDeaths: &a" + stats[2]));
                } else {
                    player.sendMessage(CC.translate("&cPlayer not found. Make sure the player is online."));
                }
                break;

            case "resetstats":
                // Check if the player has permission to reset stats
                if (!player.hasPermission("rod.admin")) {
                    player.sendMessage(CC.translate("&cSorry, you do not have permission to run this command"));
                    return true;
                }

                // Ensure the player provided a target player name
                if (args.length == 0) {
                    player.sendMessage(CC.translate("&cPlease reset player stats with &a/resetstats <playername>"));
                    return true;
                }

                // Retrieve the target player by name
                String resetName = args[0];
                Player resetStats = player.getServer().getPlayerExact(resetName);

                // Check if the target player is online
                if (resetStats != null) {
                    UUID playerId = resetStats.getUniqueId();
                    // Reset the stats of the target player
                    entityTracker.getHashStats().put(playerId, new int[]{0, 0, 0});
                    player.sendMessage(CC.translate("&Stats of " + resetName + " have been reset!"));
                } else {
                    player.sendMessage(CC.translate("&cPlayer not found. Make sure the player is online."));
                }
                break;

            case "topstats":
                // Check if the player has permission to view top stats
                if (!player.hasPermission("rod.admin")) {
                    player.sendMessage(CC.translate("&cSorry, you do not have permission to run this command"));
                    return true;
                }

                // Ensure the player did not provide extra arguments
                if (args.length > 0) {
                    player.sendMessage(CC.translate("&cNo arguments expected for this command."));
                    return true;
                }


                int count = 0;
                int limit = 10;

                // Display the top stats in descending order
                for (HashMap.Entry<String, int[]> entry : entityTracker.getTreeStats().descendingMap().entrySet()) {
                    if (count >= limit) {
                        break;
                    }
                    String playerName = entry.getKey();   // Player's name
                    int[] playerScore = entry.getValue(); // Stats array [blocks mined, blocks placed, deaths]
                    player.sendMessage(CC.translate("&e" + playerName + " \n&fBlocks mined: &a" + playerScore[0] + " \n&fBlocks placed: &a" + playerScore[1] + " \n&fDeaths: &a" + playerScore[2]));
                }
                break;

            default:
                // Handle unknown command labels
                player.sendMessage(CC.translate("&cUnknown command: " + label));
                break;
        }

        return true;
    }
}
