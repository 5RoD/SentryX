package rod.sentryx.security;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;

import org.bukkit.Location;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

import org.jetbrains.annotations.NotNull;
import rod.sentryx.util.CC;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import java.util.HashSet;
import java.util.UUID;

public class Auth implements CommandExecutor, Listener {



    // HashMaps and HashSets to keep track of authenticated players
    public HashMap<UUID, Location> authHash = new HashMap<>();
    public HashMap<UUID, Location> getAuthHash() {
        return authHash;
    }

    public HashSet<UUID> LoggedInHash = new HashSet<>();

    // Titles for players who need to authenticate and for successful authentication
    private final Component mainTitle = Component.text("Please login with", NamedTextColor.RED);
    private final Component subTitle = Component.text("/auth <password>", NamedTextColor.GREEN);
    private final Title title = Title.title(mainTitle, subTitle);

    private final Component loggedIn = Component.text("Successfully logged in!", NamedTextColor.GREEN);
    private final Component subLoggedIn = Component.text("Welcome back ", NamedTextColor.WHITE);
    private final Title loggedInTitle = Title.title(loggedIn, subLoggedIn);

    private final Component noRegisterTitle = Component.text("Please register with", NamedTextColor.RED);
    private final Component noRegisterSubTitle = Component.text("/authregister <password> ", NamedTextColor.GREEN);
    private final Title noRegister = Title.title(noRegisterTitle, noRegisterSubTitle);

    private static final String NO_PERMISSION_MESSAGE = CC.translate("&cYou do not have permission to run this command!");


    private File passwordFile;
    private FileConfiguration passwordsConfig;

    private void initPasswordsFile() {
        passwordFile = new File("plugins/SentryX/passwords.yml");
        if (!passwordFile.exists()) {
            try {
                passwordFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        passwordsConfig = YamlConfiguration.loadConfiguration(passwordFile);
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();

        initPasswordsFile();
        UUID playerUUID = player.getUniqueId();
        String registeredPassword = passwordsConfig.getString(playerUUID.toString());

        if (player.hasPermission("rod.admin") && registeredPassword != null) {
            Location playerJoinLocation = player.getLocation();
            authHash.put(player.getUniqueId(), playerJoinLocation);
            player.setWalkSpeed(0.0F);
            player.sendMessage(CC.translate("&CPlease login with &c/auth <password>&c before doing anything!"));

        } else if (player.hasPermission("rod.admin") && registeredPassword == null) {
            Location playerJoinLocation = player.getLocation();
            authHash.put(player.getUniqueId(), playerJoinLocation);
            player.setWalkSpeed(0.0F);
            player.sendMessage(CC.translate("&CPlease register with &a/authregister <password>&c before doing anything!"));

        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        final Player player = e.getPlayer();

        // Remove the player from AuthHash and LoggedInHash upon quitting
        if (authHash.remove(player.getUniqueId()) != null) {
            player.setWalkSpeed(0.2F);
        }
        LoggedInHash.remove(player.getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        final Player player = e.getPlayer();

        // If the player has admin permission and is not authenticated, restrict their movement
        if (!player.hasPermission("rod.admin")) {
            return;
        }


        initPasswordsFile();
        UUID playerUUID = player.getUniqueId();
        String registeredPassword = passwordsConfig.getString(playerUUID.toString());
        Location joinLocation = authHash.get(playerUUID);

        if (player.hasPermission("rod.admin") && joinLocation != null && authHash.containsKey(player.getUniqueId()) && registeredPassword != null) {

            player.teleport(joinLocation);
            player.showTitle(title);
            player.sendMessage(CC.translate("&cYou need to login with &c/auth <password>&c before doing that"));
            e.setCancelled(true);

        } else if (player.hasPermission("rod.admin") && joinLocation != null && authHash.containsKey(player.getUniqueId()) && registeredPassword == null) {

            player.teleport(joinLocation);
            player.showTitle(noRegister);
            player.sendMessage(CC.translate("&cYou need to register with &a/authregister <password>&c before doing that"));
            e.setCancelled(true);

        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        initPasswordsFile();
        final Player player = e.getPlayer();
        UUID playerUUID = player.getUniqueId();
        String registeredPassword = passwordsConfig.getString(playerUUID.toString());


        if (player.hasPermission("rod.admin") && authHash.containsKey(player.getUniqueId()) && registeredPassword == null) {

            player.showTitle(noRegister);
            player.sendMessage(CC.translate("&cYou need to register with &a/authregister <password>&c before doing that"));
            e.setCancelled(true);
        } else if (player.hasPermission("rod.admin") && authHash.containsKey(playerUUID) && registeredPassword != null) {
            player.sendMessage(CC.translate("&cYou need to login with &c/auth <password>&c before doing that"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onThrow(PlayerDropItemEvent e) {

        final Player player = e.getPlayer();
        initPasswordsFile();
        UUID playerUUID = player.getUniqueId();
        String registeredPassword = passwordsConfig.getString(playerUUID.toString());


        if (player.hasPermission("rod.admin") && authHash.containsKey(player.getUniqueId()) && registeredPassword == null) {
            e.setCancelled(true);
        } else if (player.hasPermission("rod.admin") && authHash.containsKey(playerUUID) && registeredPassword != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent e) {

        final Player player = e.getPlayer();
        initPasswordsFile();
        UUID playerUUID = player.getUniqueId();
        String registeredPassword = passwordsConfig.getString(playerUUID.toString());

        if (player.hasPermission("rod.admin") && authHash.containsKey(player.getUniqueId()) && registeredPassword == null) {
            e.setCancelled(true);
        } else if (player.hasPermission("rod.admin") && authHash.containsKey(playerUUID) && registeredPassword != null) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            initPasswordsFile();
            Player player = (Player) e.getEntity();
            UUID playerUUID = player.getUniqueId();
            String registeredPassword = passwordsConfig.getString(playerUUID.toString());

            // If the player has admin permission and is not authenticated, cancel the entity damage
            if (player.hasPermission("rod.admin") && authHash.containsKey(player.getUniqueId()) && registeredPassword == null) {
                e.setCancelled(true);
                player.sendMessage(CC.translate("&cYou need to register with &a/authregister <password>&c before doing that"));
            }
         else if (player.hasPermission("rod.admin") && authHash.containsKey(playerUUID) && registeredPassword != null) {
               e.setCancelled(true);
            player.sendMessage(CC.translate("&cYou need to login with &c/auth <password>&c before doing that"));
          }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            initPasswordsFile();
            Player player = (Player) e.getDamager();
            UUID playerUUID = player.getUniqueId();
            String registeredPassword = passwordsConfig.getString(playerUUID.toString());

            if (player.hasPermission("rod.admin") && authHash.containsKey(player.getUniqueId()) && registeredPassword == null) {
                e.setCancelled(true);
                player.sendMessage(CC.translate("&cYou need to register with &a/authregister <password>&c before doing that"));

            } else if (player.hasPermission("rod.admin") && authHash.containsKey(playerUUID) && registeredPassword != null) {
                player.sendMessage(CC.translate("&cYou need to login with &c/auth <password>&c before doing that"));
                e.setCancelled(true);
            }
        }
    }





    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players are allowed to run this command"));
            return true;
        }

        Player player = (Player) sender;


        initPasswordsFile();
        UUID playerUUID = player.getUniqueId();
        String registeredPassword = passwordsConfig.getString(playerUUID.toString());


        // add this: Allow only /authregister and /auth commands. not done yet



        switch (label.toLowerCase()) {
            case "auth":


                if (player.hasPermission("rod.admin") && args.length == 0 && registeredPassword != null) {
                    player.sendMessage(CC.translate("&cPlease provide a password using &a/auth <password>"));
                } else if (player.hasPermission("rod.admin") && args.length == 0 && registeredPassword == null) {
                    player.sendMessage(CC.translate("&cPlease register using &a/authregister <password>"));
                    return true;
                }

                 if (player.hasPermission("rod.admin") && authHash.containsKey(player.getUniqueId())) {
                    if (registeredPassword == null) {
                        player.sendMessage(CC.translate("&cYou need to register first with &a/authregister <password>"));
                    }
                    else if (player.hasPermission("rod.admin") && authHash.containsKey(player.getUniqueId())) {
                        if (args.length != 1 && registeredPassword != null) {
                            player.sendMessage(CC.translate("&cPlease provide a password using &a/auth <password>"));
                            return true;
                        }
                    }

                    initPasswordsFile();
                    String password = args[0];
                    String storedPassword = passwordsConfig.getString(playerUUID.toString());

                     if (password.equals(storedPassword)) {
                         player.setWalkSpeed(0.2F);
                         player.sendMessage(CC.translate("&aYou have successfully logged in!"));
                         player.showTitle(loggedInTitle);
                         authHash.remove(playerUUID);
                         LoggedInHash.add(playerUUID);

                     } else {
                         player.sendMessage(CC.translate("&cIncorrect password. Please try again."));
                     }
                } else if (player.hasPermission("rod.admin") && LoggedInHash.contains(player.getUniqueId())) {
                    player.sendMessage(CC.translate("You're already logged in!"));
                } else if (!player.hasPermission("rod.admin")) {
                    player.sendMessage(CC.translate(NO_PERMISSION_MESSAGE));
                }
                break;
        }
        return false;
    }
}
