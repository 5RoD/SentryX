package rod.sentryx.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import rod.sentryx.util.CC;

import java.util.HashSet;
import java.util.UUID;


//DOESNT FREEZE ANYTHING FIXING LATER
public class FreezeCMD implements CommandExecutor, Listener {


    private static final String NO_PERMISSION_MESSAGE = CC.translate("&cYou are not allowed to run this command!");
    private final HashSet<UUID> isFrozen = new HashSet<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(NO_PERMISSION_MESSAGE);
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("sentryx.freeze")) {
            player.sendMessage(NO_PERMISSION_MESSAGE);
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(CC.translate("&cYou must specify a player to freeze!"));
            return true; // Stop here if no arguments are provided
        }

        String targetPlayer = args[0]; // Get the first argument
        Player playerName = Bukkit.getPlayer(targetPlayer);

        if (playerName == null) {
            sender.sendMessage(CC.translate("&cPlayer &a" + targetPlayer + " &cis not online!"));
            return true;
        }

//        // Check for self-freezing
//        if (targetPlayer.equals(sender.getName())) {
//            player.sendMessage(CC.translate("&cYou can't freeze yourself!"));
//            return true; // Stop here if trying to freeze self
//        }

        UUID playerUniqueId = playerName.getUniqueId();

        if (isFrozen.contains(playerUniqueId)) {
            isFrozen.remove(playerUniqueId);
            sender.sendMessage(CC.translate("&cYou have unfroze: &a" + targetPlayer));
        } else {
            isFrozen.add(playerUniqueId);
            sender.sendMessage(CC.translate("&cYou have froze: &a" + targetPlayer));
        }

        return true;
    }



    @EventHandler
    private void onFreezeIntract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        UUID playerUniqueId = player.getUniqueId();

        if (isFrozen.contains(playerUniqueId)) {
            e.setCancelled(true);
            player.sendMessage(CC.translate("&cYou are now frozen!" + " &aJoin our discord:&f www.gravemc.net/discord"));

        }
    }

    @EventHandler
    private void onFreezeMove(PlayerMoveEvent e) {

        Player player = e.getPlayer();
        UUID playerUniqueId = player.getUniqueId();

        if (isFrozen.contains(playerUniqueId)) {
            e.setCancelled(true);
            player.sendMessage(CC.translate("&cYou are now frozen!" + " &aJoin our discord:&f www.gravemc.net/discord"));
        }
    }
}