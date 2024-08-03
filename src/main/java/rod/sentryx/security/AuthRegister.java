package rod.sentryx.security;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import rod.sentryx.util.CC;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class AuthRegister implements CommandExecutor {

    private static final String NO_PERMISSION_MESSAGE = CC.translate("&cYou do not have permission to run this command!");
    private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#%$&!])[a-zA-Z\\d@#%$&!]{8,}$";
    private static final String PASSWORD_TOO_SHORT_MESSAGE = CC.translate("&cYour password must be at least 8 characters long, " +
            "contain at least one letter, one number, and one special character (@, #, %, $, &, !).");

    private File passwordFile;
    private FileConfiguration passwordsConfig;
    private final HashMap<UUID, Location> authHash;

    public AuthRegister(HashMap<UUID, Location> authHash) {
        this.authHash = authHash;
        initPasswordsFile();
    }

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

    private void savePasswordsConfig() {
        try {
            passwordsConfig.save(passwordFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players are allowed to run this command"));
            return true;
        }

        Player player = (Player) sender;
        String commandLabel = label.toLowerCase();



        if ("authregister".equals(commandLabel)) {
            return handleAuthRegister(player, args);
        }

        return true;
    }



    private boolean handleAuthRegister(Player player, String[] args) {
        if (player.hasPermission("rod.admin") && args.length != 1) {
            player.sendMessage(CC.translate("&cPlease provide a password using &a/authregister <password>"));
            return true;
        }

        String password = args[0];

        if (!password.matches(PASSWORD_REGEX)) {
            player.sendMessage(PASSWORD_TOO_SHORT_MESSAGE);
            return true;
        }

        UUID playerUUID = player.getUniqueId();
        String registeredPassword = passwordsConfig.getString(playerUUID.toString());

        if (player.hasPermission("rod.admin")) {
            if (authHash.containsKey(playerUUID)) {
                player.sendMessage(CC.translate("&aYou have successfully registered please login with &c/auth <password>"));
                savePassword(playerUUID, password);
            } else if (registeredPassword != null) {
                player.sendMessage(CC.translate("&aYou're already registered please use &c/auth <password> &ato login"));
                savePasswordsConfig();
            }
        } else {
            player.sendMessage(NO_PERMISSION_MESSAGE);
        }
        return true;
    }




    private void savePassword(UUID playerUUID, String password) {
        passwordsConfig.set(playerUUID.toString(), password);
        savePasswordsConfig();
    }
}
