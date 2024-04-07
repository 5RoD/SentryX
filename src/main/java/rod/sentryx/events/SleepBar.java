package rod.sentryx.events;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

public class SleepBar implements Listener {

    private BossBar boss;

    public SleepBar() {
        boss = Bukkit.createBossBar("You are sleeping now!", BarColor.GREEN, BarStyle.SOLID);
    }


    @EventHandler
    private void test(PlayerBedEnterEvent e) {

        if (e.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK)) {

            Player player = e.getPlayer();
            boss.addPlayer(player);


        }
    }

    @EventHandler
    private void leaveBed(PlayerBedLeaveEvent e) {
        Player player = e.getPlayer();

        if (boss != null) {
            boss.removePlayer(player);


        }
    }

}
