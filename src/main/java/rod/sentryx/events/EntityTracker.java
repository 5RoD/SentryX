package rod.sentryx.events;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;

// This class implements the Listener interface, which means it listens for specific events
public class EntityTracker implements Listener {

    // This HashMap stores player statistics, mapping each player's UUID to an integer array
    // The integer array has two elements: [0] for blocks mined, [1] for blocks placed
    private HashMap<UUID, int[]> hashStats = new HashMap<>();
    private TreeMap<String, int[]> treeStats = new TreeMap<>();






    /**
     * Getter method to access the HashMap containing player statistics by UUID.
     *
     * @return The HashMap with player UUIDs as keys and integer arrays as values.
     */
    public HashMap<UUID, int[]> getHashStats() {
        return hashStats;
    }

    /**
     * Getter method to access the TreeMap containing player statistics by player name.
     *
     * @return The TreeMap with player names as keys and integer arrays as values.
     */
    public TreeMap<String, int[]> getTreeStats() {
        return treeStats;
    }




    /**
     * This method is called when a BlockBreakEvent occurs (when a player mines a block).
     *
     * @param e The BlockBreakEvent that contains information about the event.
     */
    @EventHandler
    public void onMine(BlockBreakEvent e) {
        // Get the player who triggered the event
        Player player = e.getPlayer();
        // Get the player's unique ID
        UUID playerId = player.getUniqueId();


        // Get the player's current statistics (or a new array if they don't have any yet)
        int[] hashStats = this.hashStats.containsKey(playerId) ? this.hashStats.get(playerId) : new int[]{0, 0, 0};
        int[] treeStats = this.treeStats.containsKey(player.getName()) ? this.treeStats.get(player.getName()) : new int[]{0, 0, 0};

        // Increment the first element of the array (blocks mined)
        hashStats[0]++;
        treeStats[0]++;
        this.hashStats.put(playerId, hashStats);
        this.treeStats.put(player.getName(), treeStats);
    }

    /**
     * This method is called when a BlockPlaceEvent occurs (when a player places a block).
     *
     * @param e The BlockPlaceEvent that contains information about the event.
     */
    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        // Get the player who triggered the event
        Player player = e.getPlayer();
        // Get the player's unique ID
        UUID playerId = player.getUniqueId();

        // Get the player's current statistics (or a new array if they don't have any yet)
        int[] hashStats = this.hashStats.containsKey(playerId) ? this.hashStats.get(playerId) : new int[]{0, 0};
        int[] treeStats = this.treeStats.containsKey(player.getName()) ? this.treeStats.get(player.getName()) : new int[]{0, 0};

        // Increment the second element of the array (blocks placed)
        hashStats[1]++;
        treeStats[1]++;
        this.hashStats.put(playerId, hashStats);
        this.treeStats.put(player.getName(), treeStats);
    }


    /**
     * This method is called when a PlayerDeathEvent occurs (when a player dies).
     *
     * @param e The PlayerDeathEvent that contains information about the event.
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        Player player = e.getEntity();

        UUID playerId = player.getUniqueId();

        int[] hashStats = this.hashStats.containsKey(playerId) ? this.hashStats.get(playerId) : new int[]{0, 0, 0};
        int[] treeStats = this.treeStats.containsKey(player.getName()) ? this.treeStats.get(player.getName()) : new int[]{0, 0, 0};

        hashStats[2]++;
        treeStats[2]++;
        this.hashStats.put(playerId, hashStats);
        this.treeStats.put(player.getName(), treeStats);

    }

    /**
     * This method is called when a PlayerJoinEvent occurs (when a player joins).
     *
     * @param e The PlayerJoinEvent that contains information about the event.
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        // Get the player who triggered the event
        Player player = e.getPlayer();
        // Get the player's unique ID
        UUID playerId = player.getUniqueId();

        // If the player's stats do not already exist, create new entries with initial values
        if (!hashStats.containsKey(playerId) && !treeStats.containsKey(player.getName())) {
            // Add a new entry to the HashMap with their UUID and a new integer array [0, 0, 0]
            this.hashStats.put(playerId, new int[]{0, 0, 0});
            // Add a new entry to the TreeMap with their name and a new integer array [0, 0, 0]
            this.treeStats.put(player.getName(), new int[]{0, 0, 0});
        }
    }
}
