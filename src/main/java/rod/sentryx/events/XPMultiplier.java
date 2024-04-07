package rod.sentryx.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import java.util.List;

public class XPMultiplier implements Listener {

    @EventHandler
    private void onEXP(PlayerExpChangeEvent e) {


        int amount = e.getAmount();

        if (amount > 0) {
            e.setAmount(amount * 2);
        }

    }
}
