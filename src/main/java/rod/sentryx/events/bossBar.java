package rod.sentryx.events;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

/**
 * Manages the visibility of a BossBar based on player bed events.
 */
public class bossBar implements Listener {

    private final BossBar bossBar;

    /**
     * Creates a new instance of the BossBarManager.
     */
    public bossBar() {
        bossBar = Bukkit.createBossBar("You are sleeping now!", BarColor.GREEN, BarStyle.SOLID);
    }

    /**
     * Handles the PlayerBedEnterEvent by adding the player to the BossBar.
     *
     * @param event The PlayerBedEnterEvent.
     */
    @EventHandler
    private void handlePlayerBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK)) {
            Player player = event.getPlayer();
            bossBar.addPlayer(player);
        }
    }

    /**
     * Handles the PlayerBedLeaveEvent by removing the player from the BossBar.
     *
     * @param event The PlayerBedLeaveEvent.
     */
    @EventHandler
    private void handlePlayerBedLeave(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();
        bossBar.removePlayer(player);
    }
}
