package net.hectus.invade;

import com.marcpg.data.database.sql.SQLConnection;
import com.marcpg.lang.Translation;
import net.hectus.invade.commands.SlashSkip;
import net.hectus.invade.commands.SlashStart;
import net.hectus.invade.events.BasicPlayerEvents;
import net.hectus.invade.events.TaskEvents;
import net.hectus.invade.match.Match;
import net.hectus.invade.match.MatchManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public final class Invade extends JavaPlugin {
    public static Logger LOG;
    public static FileConfiguration CONFIG;
    public static Plugin PLUGIN;
    public static SQLConnection<UUID> DATABASE;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        LOG = getLogger();
        CONFIG = getConfig();
        PLUGIN = this;

        try {
            File langDirectory = new File(getDataFolder(), "lang");
            if (langDirectory.mkdirs() || new File(langDirectory, "en_US.properties").createNewFile()) {
                LOG.info("Created translation directories (plugins/Invade/lang/), as they didn't exist before!");
                LOG.warning("Please download the latest translations now, as the demo en_US.properties doesn't contain any translations.");
            }
            Translation.loadProperties(langDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            ConfigurationSection pgConf = Objects.requireNonNull(CONFIG.getConfigurationSection("postgresql"));
            DATABASE = new SQLConnection<>(SQLConnection.DatabaseType.POSTGRESQL, Objects.requireNonNull(pgConf.getString("url")), pgConf.getString("user"), pgConf.getString("passwd"), "invade_playerdata", "uuid");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Objects.requireNonNull(getCommand("start")).setExecutor(new SlashStart());
        Objects.requireNonNull(getCommand("skip")).setExecutor(new SlashSkip());

        getServer().getPluginManager().registerEvents(new BasicPlayerEvents(), this);
        getServer().getPluginManager().registerEvents(new TaskEvents(), this);

        LOG.info("Successfully started up Invade's plugin!");
    }

    @Override
    public void onDisable() {
        try {
            for (Match match : MatchManager.MATCHES) match.invadeTicks.stop();
            DATABASE.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        LOG.info("Successfully shut down all components of Invade's plugin!");
    }

    /*
    POSTGRESQL DATABASE CONFIGURATION:
    | Name    | uuid        | name         | matches | wins | loses | playtime |
    | Type    | UUID        | VARCHAR(255) | INT     | INT  | INT   | INTERVAL |
    | Default | PRIMARY KEY | NOT NULL     | 0       | 0    | 0     | 00:00:00 |
     */
}
