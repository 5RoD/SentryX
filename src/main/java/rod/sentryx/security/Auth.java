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
    private HashMap<UUID, Location> authHash = new HashMap<>();

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

    // Initialize the password file for the player
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
        UUID playerUUID = player.getUniqueId();

        initPasswordsFile();  // Initialize the passwords file on player join

        String registeredPassword = passwordsConfig.getString(playerUUID.toString());

        // Check if the player is an admin and has a registered password
        if (player.hasPermission("rod.admin") && registeredPassword != null) {
            Location playerJoinLocation = player.getLocation();
            authHash.put(player.getUniqueId(), playerJoinLocation);
            player.setWalkSpeed(0.0F); // Set player walk speed to 0 until authenticated
            player.sendMessage(CC.translate("&cPlease login with &a/auth <password>&c before doing anything!"));
        } else if (player.hasPermission("rod.admin") && registeredPassword == null) {
            Location playerJoinLocation = player.getLocation();
            authHash.put(player.getUniqueId(), playerJoinLocation);
            player.setWalkSpeed(0.0F); // Set player walk speed to 0 until registered
            player.sendMessage(CC.translate("&cPlease register with &a/authregister <password>&c before doing anything!"));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        final Player player = e.getPlayer();

        // Remove the player from AuthHash and LoggedInHash upon quitting
        if (authHash.remove(player.getUniqueId()) != null) {
            player.setWalkSpeed(0.2F); // Reset player walk speed on quit
        }
        LoggedInHash.remove(player.getUniqueId()); // Remove player from logged in hash
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        final Player player = e.getPlayer();

        // If the player does not have admin permission, return early
        if (!player.hasPermission("rod.admin")) {
            return;
        }

        initPasswordsFile();  // Ensure the passwords file is initialized
        UUID playerUUID = player.getUniqueId();
        String registeredPassword = passwordsConfig.getString(playerUUID.toString());
        Location joinLocation = authHash.get(playerUUID);

        // Check if the player is an admin and has not registered
        if (player.hasPermission("rod.admin") && joinLocation != null && authHash.containsKey(player.getUniqueId()) && registeredPassword == null) {
            player.teleport(joinLocation);  // Teleport player back to the join location
            player.showTitle(noRegister);  // Show register title to the player
            player.sendMessage(CC.translate("&cYou need to register with &a/authregister <password>&c before doing that"));
            e.setCancelled(true); // Cancel the move event

        } else if (player.hasPermission("rod.admin") && joinLocation != null && authHash.containsKey(player.getUniqueId()) && registeredPassword != null) {
            player.teleport(joinLocation);  // Teleport player back to the join location
            player.showTitle(title);  // Show login title to the player
            player.sendMessage(CC.translate("&cYou need to login with &a/auth <password>&c before doing that"));
            e.setCancelled(true); // Cancel the move event
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        initPasswordsFile();  // Ensure the passwords file is initialized
        final Player player = e.getPlayer();
        UUID playerUUID = player.getUniqueId();
        String registeredPassword = passwordsConfig.getString(playerUUID.toString());

        // Check if the player is an admin and has not registered
        if (player.hasPermission("rod.admin") && authHash.containsKey(player.getUniqueId()) && registeredPassword == null) {
            player.showTitle(noRegister);  // Show register title to the player
            player.sendMessage(CC.translate("&cYou need to register with &a/authregister <password>&c before doing that"));
            e.setCancelled(true);  // Cancel the interact event
        } else if (player.hasPermission("rod.admin") && authHash.containsKey(playerUUID) && registeredPassword != null) {
            player.sendMessage(CC.translate("&cYou need to login with &a/auth <password>&c before doing that"));
            e.setCancelled(true);  // Cancel the interact event
        }
    }

    @EventHandler
    public void onThrow(PlayerDropItemEvent e) {
        final Player player = e.getPlayer();
        initPasswordsFile();  // Ensure the passwords file is initialized
        UUID playerUUID = player.getUniqueId();
        String registeredPassword = passwordsConfig.getString(playerUUID.toString());

        // Check if the player is an admin and has not registered
        if (player.hasPermission("rod.admin") && authHash.containsKey(player.getUniqueId()) && registeredPassword == null) {
            e.setCancelled(true);  // Cancel the item drop event
        } else if (player.hasPermission("rod.admin") && authHash.containsKey(playerUUID) && registeredPassword != null) {
            e.setCancelled(true);  // Cancel the item drop event
        }
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent e) {
        final Player player = e.getPlayer();
        initPasswordsFile();  // Ensure the passwords file is initialized
        UUID playerUUID = player.getUniqueId();
        String registeredPassword = passwordsConfig.getString(playerUUID.toString());

        // Check if the player is an admin and has not registered
        if (player.hasPermission("rod.admin") && authHash.containsKey(player.getUniqueId()) && registeredPassword == null) {
            e.setCancelled(true);  // Cancel the item damage event
        } else if (player.hasPermission("rod.admin") && authHash.containsKey(playerUUID) && registeredPassword != null) {
            e.setCancelled(true);  // Cancel the item damage event
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            initPasswordsFile();  // Ensure the passwords file is initialized
            Player player = (Player) e.getEntity();
            UUID playerUUID = player.getUniqueId();
            String registeredPassword = passwordsConfig.getString(playerUUID.toString());

            // Check if the player is an admin and has not registered
            if (player.hasPermission("rod.admin") && authHash.containsKey(player.getUniqueId()) && registeredPassword == null) {
                e.setCancelled(true);  // Cancel the entity damage event
                player.sendMessage(CC.translate("&cYou need to register with &a/authregister <password>&c before doing that"));
            } else if (player.hasPermission("rod.admin") && authHash.containsKey(playerUUID) && registeredPassword != null) {
                e.setCancelled(true);  // Cancel the entity damage event
                player.sendMessage(CC.translate("&cYou need to login with &a/auth <password>&c before doing that"));
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            initPasswordsFile();  // Ensure the passwords file is initialized
            Player player = (Player) e.getDamager();
            UUID playerUUID = player.getUniqueId();
            String registeredPassword = passwordsConfig.getString(playerUUID.toString());

            // Check if the player is an admin and has not registered
            if (player.hasPermission("rod.admin") && authHash.containsKey(player.getUniqueId()) && registeredPassword == null) {
                e.setCancelled(true);  // Cancel the entity damage by entity event
                player.sendMessage(CC.translate("&cYou need to register with &a/authregister <password>&c before doing that"));

            } else if (player.hasPermission("rod.admin") && authHash.containsKey(playerUUID) && registeredPassword != null) {
                player.sendMessage(CC.translate("&cYou need to login with &a/auth <password>&c before doing that"));
                e.setCancelled(true);  // Cancel the entity damage by entity event
            }
        }
    }

    // Checks all player commands and only allows /auth and /authregister if the player is not authenticated
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String command = e.getMessage().toLowerCase();

        // Check if the player is an admin and is not logged in
        if (player.hasPermission("rod.admin") && !LoggedInHash.contains(player.getUniqueId())) {
            // Only allow the /auth and /authregister commands
            if (!command.startsWith("/auth") && !command.startsWith("/authregister")) {
                player.sendMessage(CC.translate("&cYou must authenticate first with &a/auth &cor register with &a/authregister."));
                e.setCancelled(true);  // Cancel the command
            } else if (command.startsWith("/authreset")) {
                e.setCancelled(true);
                player.sendMessage(CC.translate("&cYou must authenticate first with &a/auth &cor register with &a/authregister."));
            } else {
                player.sendMessage(CC.translate("&cAn error occurred contact the admins"));
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players are allowed to run this command"));
            return true;
        }

        Player player = (Player) sender;

        initPasswordsFile();  // Ensure the passwords file is initialized
        UUID playerUUID = player.getUniqueId();

        String registeredPassword = passwordsConfig.getString(playerUUID.toString());

        switch (label.toLowerCase()) {
            case "authreset":
                // Check if the player has the required permissions
                if (!player.hasPermission("rod.admin")) {
                    player.sendMessage(CC.translate(NO_PERMISSION_MESSAGE));
                    return true;
                }
                // Ensure the player provided a target player name
                if (args.length == 0) {
                    player.sendMessage(CC.translate("&cPlease reset a player using &a/authreset <playername>"));
                    return true;
                }

                String playerName = args[0];
                Player targetPlayer = player.getServer().getPlayerExact(playerName);

                // Check if the target player is online
                if (targetPlayer != null) {
                    UUID targetUUID = targetPlayer.getUniqueId();

                    // Check if the target player has a registered password
                    if (passwordsConfig.contains(targetUUID.toString())) {
                        passwordsConfig.set(targetUUID.toString(), null);
                        try {
                            passwordsConfig.save(passwordFile);  // Save changes to the file
                            player.sendMessage(CC.translate("&aSuccessfully reset authentication for player: &e" + targetPlayer.getName()));
                        } catch (IOException e) {
                            player.sendMessage(CC.translate("&cAn error occurred while saving changes."));
                            e.printStackTrace();
                        }
                    } else {
                        player.sendMessage(CC.translate("&cPlayer &e" + targetPlayer.getName() + "&c does not have a registered password."));
                    }
                } else {
                    player.sendMessage(CC.translate("&cPlayer &e" + playerName + "&c is not online."));
                }
                break;

            case "auth":
                // Check if the player has the required permissions
                if (!player.hasPermission("rod.admin")) {
                    player.sendMessage(CC.translate(NO_PERMISSION_MESSAGE));
                    return true;
                }

                // Check if the player is already logged in
                if (LoggedInHash.contains(player.getUniqueId())) {
                    player.sendMessage(CC.translate("&aYou're already logged in!"));
                    return true;
                }

                // Ensure the player provided a password
                if (args.length == 0) {
                    if (registeredPassword == null) {
                        player.sendMessage(CC.translate("&cPlease register using &a/authregister <password>"));
                    } else {
                        player.sendMessage(CC.translate("&cPlease provide a password using &a/auth <password>"));
                    }
                    return true;
                }

                // Verify that the player is in the authentication process
                if (!authHash.containsKey(player.getUniqueId())) {
                    player.sendMessage(CC.translate("&cYou need to register first with &a/authregister <password>"));
                    return true;
                }

                // Check if the provided password matches the stored password
                String password = args[0];

                if (password.equals(registeredPassword)) {
                    player.setWalkSpeed(0.2F);  // Reset player walk speed upon successful login
                    player.sendMessage(CC.translate("&aYou have successfully logged in!"));
                    player.showTitle(loggedInTitle);  // Show successful login title to the player
                    authHash.remove(playerUUID);  // Remove the player from the authentication hash
                    LoggedInHash.add(playerUUID);  // Add the player to the logged-in hash
                } else {
                    player.sendMessage(CC.translate("&cIncorrect password. Please try again."));
                }
                break;

            default:
                // Handle unknown command labels
                player.sendMessage(CC.translate("&cUnknown command: " + label));
                return true;
        }

        return true;  // Return true to indicate that the command was handled
    }
}