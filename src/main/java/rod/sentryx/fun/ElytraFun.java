package rod.sentryx.fun;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import rod.sentryx.util.CC;


import java.util.HashSet;
import java.util.UUID;



public class ElytraFun implements CommandExecutor, Listener {

    private static final String NO_PERMISSION_MESSAGE = CC.translate("&cYou do not have permission to run this command!");
    private final HashSet<UUID> elytraFly = new HashSet<>();


    //Checks if the player is gliding and has the hashset if so it will boost them
    @EventHandler
    public void onFly(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        UUID playerUUID = player.getUniqueId();

        if (player.isGliding() && elytraFly.contains(playerUUID)) {
            player.setVelocity(player.getLocation().getDirection().toBlockVector().multiply(1.5F));

        }

    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.FALL) {

            Player player = (Player) e.getEntity();

            if (elytraFly.contains(player.getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }





    //Removes the uuid from the hashset on leave!
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        elytraFly.remove(e.getPlayer().getUniqueId());
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {


        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players are allowed to run this command"));
            return false;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();
        String commandLabel = label.toLowerCase();


        //The main command, when ran checks for hashset if it exists it will remove it if not it will add it
        switch (commandLabel) {
            case "infiniteelytra":

                if (!sender.hasPermission("rod.fun")) {
                    sender.sendMessage(NO_PERMISSION_MESSAGE);
                    return false;
                }

                if (player.hasPermission("rod.fun") && !elytraFly.contains(playerUUID)) {
                    elytraFly.add(playerUUID);
                    player.sendMessage(CC.translate("&eYou have activated infinite elytra"));
                    return true;


                } else if (elytraFly.contains(playerUUID)) {
                    elytraFly.remove(playerUUID);
                    player.sendMessage(CC.translate("&eYou have deactivated infinite elytra"));
                    return true;


                } else {
                    player.sendMessage(NO_PERMISSION_MESSAGE);
                    return true;


                }

        }
        return true;
    }
}


