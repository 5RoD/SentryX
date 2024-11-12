package rod.sentryx.events;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class TntDestroyer implements Listener {




    public Collection<Chunk> getNearTnt(Player player) {


        int[] offset = {-1, 0, 1};
        World world = player.getWorld();

        int playerX = player.getLocation().getChunk().getX();
        int playerZ = player.getLocation().getChunk().getZ();

        Collection<Chunk> chunkAroundPlayer = new HashSet<>();

        for (int x : offset) {
            for (int z : offset) {

                Chunk chunk = world.getChunkAt(playerX + x, playerZ + z);
                chunkAroundPlayer.add(chunk);
            }

        }
        return chunkAroundPlayer;
    }



    @EventHandler
    public void onTnt(PlayerMoveEvent e) {

        Player player = e.getPlayer();
        World world = player.getWorld();
        getNearTnt(player);



    }

}
