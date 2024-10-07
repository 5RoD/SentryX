package rod.sentryx.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import rod.sentryx.util.CC;

public class JoinMessage implements Listener {



    @EventHandler
    private void Join(PlayerJoinEvent e) {

        Player player = e.getPlayer();
        e.setJoinMessage(CC.translate(("&7(" + "&a+" + "&7) " + "&a" + player.getName())));

    }

    @EventHandler
    private void Leave(PlayerQuitEvent e) {

        Player player = e.getPlayer();
        e.setQuitMessage("&7(" + "&c-" + "&7) " + "&a" + player.getName());

    }

}
