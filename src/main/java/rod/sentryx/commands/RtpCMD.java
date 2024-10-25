package rod.sentryx.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import rod.sentryx.events.RtpManager;
import rod.sentryx.util.CC;
import rod.sentryx.util.CooldownManager;

import java.util.Random;

public class RtpCMD implements CommandExecutor, Listener {

    private final RtpManager rtpManager; // Store reference to RtpManager
    private final String PERMISSION = "sentryx.rtp";
    private CooldownManager cooldownManager = new CooldownManager();
//    private final int attempts = 1;
//    private final int max_attempts = 10;


    public RtpCMD(RtpManager rtpManager) {
        this.rtpManager = rtpManager; // Initialize with the provided instance
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cYou're not allowed to use this command!"));
            return true;
        }


        Player player = (Player) sender;


        if (!player.hasPermission(PERMISSION)) {
            player.sendMessage(CC.translate("&cYou're not allowed to use this command!"));
            return true;
        }


        Location playerLoc = player.getLocation();
        World playerWorld = player.getWorld();

        switch (label.toLowerCase()) {
            case "rtp":
                if (cooldownManager.isOnCooldown(player)) {
                    return true;
                }

                if (playerWorld == null) {
                    player.sendMessage(CC.translate("&cYou are not in a world >> &eERROR NOT VALID WORLD"));
                } else {
                    cooldownManager.setCooldown(player);
                    RandomTeleport(player, playerWorld);
                    return true;
                }
                break;

            case "loadchunks":
                if (cooldownManager.isOnCooldown(player)) {
                    return true;

                } else if (playerWorld != null) {
                   cooldownManager.setCooldown(player);
                    rtpManager.cacheChunks(playerWorld);
                }
                break;
        }
        return true;
    }


    public void RandomTeleport(Player player, World world) {


//        while (attempts <= max_attempts) {
        if (rtpManager.sortedChunks.isEmpty()) {
            player.sendMessage(CC.translate("&cThere is no available spots!"));
            return;

        } else {

            Random random = new Random();

            // Select a random index from the chunkList
            int randomIndex = random.nextInt(rtpManager.sortedChunks.size());

            // Get the random chunk coordinates
            String selectedChunk = rtpManager.sortedChunks.get(randomIndex);

            //Splits the chunks
            String[] cords = selectedChunk.split(" ");

            //Gets the X and Y pos
            int ChunkX = Integer.parseInt(cords[0]);
            int ChunkZ = Integer.parseInt(cords[1]);

            //Multiplies the X and Z by 16 because chunks are 16 blocks wide :D
            int BlockX = ChunkX * 16;
            int BlockZ = ChunkZ * 16;
            //Gets the highest block from x and z
            int HighestY = world.getHighestBlockYAt(BlockX, BlockZ);

            //adds offsets to make sure the player is in the middle of block
            Location location = new Location(world, BlockX + 0.5, HighestY + 1, BlockZ + 0.5);


//                //if location is not safe try again
//                if (!isSafeLocation(location)) {
//                    player.sendMessage(CC.translate("&cUnsafe location trying again..."));
//                    RandomTeleport(player, world);
//                    break;
//                }


            player.teleport(location);
            sendActionBar(player);
            player.sendMessage(CC.translate("&aTeleported to &e" + location.getBlockX() + "&a, &e" + location.getBlockZ()));
            endActionBar(player);


        }

    }


    protected void sendActionBar(Player player) {

        player.sendActionBar(CC.translate("&aTeleporting..."));
    }

    protected void endActionBar(Player player) {
        player.sendActionBar(CC.translate("&aDONE!"));

    }
}

//    //returns a true or false value based on the block the player is teleporting to if not solid try again
//    private boolean isSafeLocation(Location location) {
//
//        Material material = location.getWorld().getBlockAt(location).getType();
//        return material.isSolid();
//
//
//    }
//}

