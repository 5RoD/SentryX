package rod.sentryx.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class TamedProtection implements Listener {


    @EventHandler
    private void onEntityDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();

        if (entity instanceof Tameable) {
            Tameable tameable = (Tameable) entity;

            if (tameable.isTamed()) {
                e.setCancelled(true);
            }
        }
    }



}
