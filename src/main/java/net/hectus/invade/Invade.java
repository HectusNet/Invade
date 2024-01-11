package net.hectus.invade;

import net.hectus.PostgreConnection;
import net.hectus.Translation;
import net.hectus.invade.commands.SlashPatch;
import net.hectus.invade.commands.SlashStart;
import net.hectus.invade.matches.Match;
import net.hectus.invade.matches.MatchManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Logger;

public final class Invade extends JavaPlugin {
    public static Logger LOG;
    public static PostgreConnection database;

    @Override
    public void onEnable() {
        LOG = getLogger();

        try {
            File langDirectory = new File(getDataFolder(), "lang");
            if (langDirectory.mkdirs()) LOG.info("Created translation directories (plugins/Invade/lang/), as they didn't exist before!");
            Translation.load(langDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            saveDefaultConfig();
            ConfigurationSection pgConf = Objects.requireNonNull(getConfig().getConfigurationSection("postgresql"));
            if (pgConf.getBoolean("enabled")) database = new PostgreConnection(Objects.requireNonNull(pgConf.getString("url")), pgConf.getString("user"), pgConf.getString("passwd"), pgConf.getString("table"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Objects.requireNonNull(getCommand("start")).setExecutor(new SlashStart());
        Objects.requireNonNull(getCommand("patch")).setExecutor(new SlashPatch());

        LOG.info("Successfully started up Invade's plugin!");
    }

    @Override
    public void onDisable() {
        try {
            for (Match match : MatchManager.MATCHES) match.stop();
            if (getConfig().getBoolean("postgresql.enabled")) database.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        LOG.info("Successfully shut down all components of Invade's plugin!");
    }

    /*
    POSTGRESQL DATABASE CONFIGURATION:
    | Name    | player_uuid | player_name  | matches | wins | loses | playtime |
    | Type    | UUID        | VARCHAR(255) | INT     | INT  | INT   | INTERVAL |
    | Default | PRIMARY KEY | NOT NULL     | 0       | 0    | 0     | 00:00:00 |
     */
}
