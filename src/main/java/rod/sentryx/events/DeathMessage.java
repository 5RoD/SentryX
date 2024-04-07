package rod.sentryx.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathMessage implements Listener {



    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        EntityDamageEvent damageEvent = player.getLastDamageCause();

        if (damageEvent != null) {
            EntityDamageEvent.DamageCause cause = damageEvent.getCause();
            String deathMessage;

            if (damageEvent instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) damageEvent;
                if (entityDamageByEntityEvent.getDamager() instanceof Player) {
                    Player killer = (Player) entityDamageByEntityEvent.getDamager();
                    deathMessage = ChatColor.GREEN + player.getName() +ChatColor.YELLOW+ " was killed by " + ChatColor.RED + killer.getName();
                } else {
                    deathMessage = ChatColor.GREEN + player.getName() + ChatColor.YELLOW +" was killed by " + ChatColor.RED + ((EntityDamageByEntityEvent) damageEvent).getDamager().getName();
                }


                e.setDeathMessage(deathMessage);
            }
        }
    }
}

