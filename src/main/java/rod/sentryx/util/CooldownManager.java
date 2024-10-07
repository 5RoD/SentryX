package rod.sentryx.util;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {
    private final int COOLDOWN_TIME = 5000; // Cooldown time in milliseconds
    private HashMap<UUID, Long> cooldowns = new HashMap<>();

    // Check if a player is on cooldown and send a message if so
    public boolean isOnCooldown(Player player) {
        UUID playerUUID = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        // Check if the player is in the cooldown map
        if (cooldowns.containsKey(playerUUID)) {
            long lastTime = cooldowns.get(playerUUID); // Get last command time

            // Check if the cooldown is still active
            if (currentTime - lastTime < COOLDOWN_TIME) {
                long timeleft = (COOLDOWN_TIME - (currentTime - lastTime)) / 1000; // Time left in seconds
                player.sendMessage(CC.translate("&cPlease wait &a" + timeleft + " &cseconds!"));
                return true; // Player is still on cooldown
            }
        }
        return false; // Player is not on cooldown
    }

    // Update the cooldown for a player
    public void setCooldown(Player player) {
        UUID playerUUID = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        cooldowns.put(playerUUID, currentTime); // Set the current time as last command time
    }

    // Optionally, you could provide a method to clear a player's cooldown
    public void clearCooldown(Player player) {
        cooldowns.remove(player.getUniqueId());
    }
}
