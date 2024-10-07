package rod.sentryx.events;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import rod.sentryx.SentryX;
import rod.sentryx.util.CC;

import java.util.ArrayList;
import java.util.List;




//WHOLE THING DOESNT WORK IDK WHY FROM THE RTP CLASS
public class RtpManager implements Listener {

    public List<String> chunkList = new ArrayList<>();



    public void cacheChunks(World world, Player player) {

        Bukkit.getScheduler().runTaskAsynchronously(SentryX.getPlugin(SentryX.class), () -> {

            Bukkit.getLogger().info("World loaded: " + world.getName()); // Debugging line

            if ("world".equals(world.getName())) {
                Chunk[] chunks = world.getLoadedChunks();
                Bukkit.getLogger().info("Chunks loaded: " + chunks.length); // Debugging line

                if (chunks.length > 0) {
                    for (Chunk chunk : chunks) {
                        int chunkX = chunk.getX();
                        int chunkZ = chunk.getZ();
                        chunkList.add(chunkX + " " + chunkZ);
                    }
                    endActionBar(player);
                    // Bukkit.getLogger().info("Cached chunks: " + chunkList); // Debugging line
                }
            } else {
                 Bukkit.getLogger().info("Not the correct world, skipping chunk caching."); // Debugging line
            }
        });
    }

    private void endActionBar(Player player) {
        player.sendActionBar(CC.translate("&aDONE!"));

    }
}