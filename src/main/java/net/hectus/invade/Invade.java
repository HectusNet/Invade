package net.hectus.invade;

import com.marcpg.libpg.data.database.sql.AutoCatchingSQLConnection;
import com.marcpg.libpg.data.database.sql.SQLConnection;
import com.marcpg.libpg.lang.Translation;
import net.hectus.invade.commands.SlashSkip;
import net.hectus.invade.commands.SlashStart;
import net.hectus.invade.events.BasicPlayerEvents;
import net.hectus.invade.events.TaskEvents;
import net.hectus.invade.match.Match;
import net.hectus.invade.match.MatchManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public final class Invade extends JavaPlugin {
    public static Plugin PLUGIN;
    public static Logger LOG;
    public static AutoCatchingSQLConnection<UUID> DATABASE;

    @Override
    public void onEnable() {
        LOG = getSLF4JLogger();
        PLUGIN = this;

        saveDefaultConfig();
        connectDatabase();

        try {
            File langDirectory = new File(getDataFolder(), "lang");
            if (langDirectory.mkdirs() || new File(langDirectory, "en_US.properties").createNewFile()) {
                LOG.info("Created translation directories (plugins/Invade/lang/), as they didn't exist before!");
                LOG.warn("Please download the latest translations now, as the demo en_US.properties doesn't contain any translations.");
            }
            Translation.loadProperties(langDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Objects.requireNonNull(getCommand("start")).setExecutor(new SlashStart());
        Objects.requireNonNull(getCommand("skip")).setExecutor(new SlashSkip());

        getServer().getPluginManager().registerEvents(new BasicPlayerEvents(), this);
        getServer().getPluginManager().registerEvents(new TaskEvents(), this);

        try {
            translations();
            connectDatabase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        LOG.info("Successfully started up Invade's plugin!");
    }

    @Override
    public void onDisable() {
        for (Match match : MatchManager.MATCHES) match.invadeTicks.stop();
        if (DATABASE != null) DATABASE.closeConnection();
        LOG.info("Successfully shut down all components of Invade's plugin!");
    }

    void translations() throws IOException {
        Files.copy(Objects.requireNonNull(getResource("en_US.yml")), getDataFolder().toPath().resolve("en_US.yml"), StandardCopyOption.REPLACE_EXISTING);
        for (File file : Objects.requireNonNull(new File(getDataFolder(), "lang").listFiles())) {
            String[] lang = file.getName().replace(".yml", "").split("_");
            Translation.loadSingleMap(new Locale(lang[0], lang[1]), YamlConfiguration.loadConfiguration(file).getValues(true)
                    .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, o -> String.valueOf(o.getValue()))));
        }
    }

    void connectDatabase() {
        ConfigurationSection db = Objects.requireNonNull(getConfig().getConfigurationSection("database"));
        if (db.getBoolean("enabled")) {
            try {
                DATABASE = new AutoCatchingSQLConnection<>(
                        SQLConnection.DatabaseType.POSTGRESQL,
                        Objects.requireNonNull(db.getString("address")),
                        db.getString("username"),
                        db.getString("password"),
                        db.getString("table"),
                        "uuid",
                        e -> LOG.error("There was an issue while interacting with the database: {}", e.getMessage()));
            } catch (SQLException | ClassNotFoundException e) {
                LOG.error("Couldn't establish connection to playerdata database!");
            }
        }
    }

    /*
    POSTGRESQL DATABASE CONFIGURATION:
    | Name    | uuid        | name         | matches | wins | loses | playtime |
    | Type    | UUID        | VARCHAR(255) | INT     | INT  | INT   | INTERVAL |
    | Default | PRIMARY KEY | NOT NULL     | 0       | 0    | 0     | 00:00:00 |
     */
}
