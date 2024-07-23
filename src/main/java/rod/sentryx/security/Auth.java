package rod.sentryx.security;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import rod.sentryx.util.CC;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Auth implements CommandExecutor, Listener {

    private final HashMap<UUID, Location> AuthHash = new HashMap<>();

    private final Component mainTitle = Component.text("Please login with /auth", NamedTextColor.YELLOW);
    private final Component subTitle = Component.text("Sub text test", NamedTextColor.WHITE);
    Title title = Title.title(mainTitle, subTitle);

    private static final String NO_PERMISSION_MESSAGE = CC.translate("&cYou do not have permission to run this command!");




    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        final Player player = e.getPlayer();

        if (player.hasPermission("rod.admin")) {

            Location PlayerJoinLocation = player.getLocation();
            AuthHash.put(player.getUniqueId(), PlayerJoinLocation);
            player.setWalkSpeed(0.0F);
            player.sendMessage(CC.translate("&CPlease login with /auth before doing anything!"));

        }
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        if (AuthHash.remove(player.getUniqueId()) != null)
            player.setWalkSpeed(0.2F);

    }


    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        final Player player = e.getPlayer();

        if (!player.hasPermission("rod.admin")) {
            return;
        }

            Location joinLocation = AuthHash.get(player.getUniqueId());
            if (joinLocation != null) {
                player.teleport(joinLocation);
                player.showTitle(title);
                player.sendMessage(CC.translate("&cYou need to login with /auth before doing that"));
            }
    }

    @EventHandler
    public void onIntract(PlayerInteractEvent e) {
        final Player player = e.getPlayer();

        if (!player.hasPermission("rod.admin")) {
            return;
        }
        e.setCancelled(true);
        player.sendMessage(CC.translate("&cYou need to login with /auth before doing that"));
    }
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        final Player player = e.getPlayer();
        if (!player.hasPermission("rod.admin")) {
            return;
        }
        e.setCancelled(true);
        player.sendMessage(CC.translate("&cYou need to login with /auth before doing that"));
    }



    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] strings) {
        Player player = (Player) sender;

        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players are allowed to run this command"));
            return true;
        }


     switch(label.toLowerCase()) {
            //needs work add a way to remove them from the hashmap and from the blockbreakevent and place and intract, etc..
         case "auth":
             if (player.hasPermission("rod.admin")){
                 player.setGameMode(GameMode.SURVIVAL);
                 player.sendMessage(CC.translate("&eYour gamemode has changed to &aSURVIVAL"));
             } else if (!player.hasPermission("rod.staff.gms")) {
                 player.sendMessage(NO_PERMISSION_MESSAGE);
             }
             break;


}




        return false;
    }
}