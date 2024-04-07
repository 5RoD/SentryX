package rod.sentryx.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinMessage implements Listener {



    @EventHandler
    private void Join(PlayerJoinEvent e) {

        Player player = e.getPlayer();
        e.setJoinMessage(ChatColor.GRAY + "(" + ChatColor.GREEN + "+" + ChatColor.GRAY + ") " + ChatColor.GREEN + player.getName());

    }

    @EventHandler
    private void Leave(PlayerQuitEvent e) {

        Player player = e.getPlayer();
        e.setQuitMessage(ChatColor.GRAY + "(" + ChatColor.RED + "-" + ChatColor.GRAY + ") " + ChatColor.GREEN + player.getName());

    }



}
