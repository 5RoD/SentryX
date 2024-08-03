package rod.sentryx.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.UUID;

// This class implements the Listener interface, which means it listens for specific events
public class EntityTracker implements Listener {

    // This HashMap stores player statistics, mapping each player's UUID to an integer array
    // The integer array has two elements: [0] for blocks mined, [1] for blocks placed
    private final HashMap<UUID, int[]> playerStats = new HashMap<>();


    // Getter method to access the playerStats HashMap
    public HashMap<UUID, int[]> getPlayerStats() {
        return playerStats;
    }

    // This method is called when a BlockBreakEvent occurs (when a player mines a block)
    @EventHandler
    public void onMine(BlockBreakEvent e) {
        // Get the player who triggered the event
        Player player = e.getPlayer();
        // Get the player's unique ID
        UUID playerId = player.getUniqueId();
        // Get the player's current statistics (or a new array if they don't have any yet)
        int[] stats = playerStats.getOrDefault(playerId, new int[]{0, 0});

        // Increment the first element of the array (blocks mined)
        stats[0]++;
    }

    // This method is called when a BlockPlaceEvent occurs (when a player places a block)
    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        // Get the player who triggered the event
        Player player = e.getPlayer();
        // Get the player's unique ID
        UUID playerId = player.getUniqueId();
        // Get the player's current statistics (or a new array if they don't have any yet)
        int[] stats = playerStats.getOrDefault(playerId, new int[]{0, 0});

        // Increment the second element of the array (blocks placed)
        stats[1]++;
    }

    // This method is called when a PlayerLoginEvent occurs (when a player logs in)
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        // Get the player who triggered the event
        Player player = e.getPlayer();
        // Get the player's unique ID
        UUID playerId = player.getUniqueId();

        // If the player doesn't have any statistics yet
        if (!playerStats.containsKey(playerId)) {
            // Add a new entry to the map with their UUID and a new integer array [0, 0]
            playerStats.put(playerId, new int[]{0, 0});
        }
    }
}
