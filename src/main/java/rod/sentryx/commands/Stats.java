package rod.sentryx.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import rod.sentryx.events.EntityTracker;
import rod.sentryx.util.CC;

import java.util.UUID;

public class Stats implements CommandExecutor {

    private final EntityTracker entityTracker;

    public Stats(EntityTracker entityTracker) {
        this.entityTracker = entityTracker;
    }

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
                // Check if the player has permission to check stats
                if (!player.hasPermission("rod.stats")) {
                    player.sendMessage(CC.translate("&cSorry, you do not have permission to run this command"));
                    return true;
                }

                // Ensure the player provided a target player name
                if (args.length == 0) {
                    player.sendMessage(CC.translate("&cPlease check player stats with &a/stats <playername>"));
                    return true;
                }

                String playerName = args[0];
                Player targetPlayer = player.getServer().getPlayerExact(playerName);

                // Check if the target player is online
                if (targetPlayer != null) {
                    UUID playerId = targetPlayer.getUniqueId();
                    int[] stats = entityTracker.getPlayerStats().getOrDefault(playerId, new int[]{0, 0});

                    player.sendMessage(CC.translate("&a" + playerName + "'s stats:\n" +
                            "&fBlocks mined: &a" + stats[0] + "\n" +
                            "&fBlocks placed: &a" + stats[1]));
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

                String playerName2 = args[0];
                Player targetPlayer2 = player.getServer().getPlayerExact(playerName2);

                // Check if the target player is online
                if (targetPlayer2 != null) {
                    UUID playerId = targetPlayer2.getUniqueId();
                    entityTracker.getPlayerStats().put(playerId, new int[]{0, 0});
                    player.sendMessage(CC.translate("&aStats of " + playerName2 + " have been reset!"));
                } else {
                    player.sendMessage(CC.translate("&cPlayer not found. Make sure the player is online."));
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
