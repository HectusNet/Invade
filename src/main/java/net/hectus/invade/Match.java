package net.hectus.invade;

import com.marcpg.data.time.Time;
import net.hectus.Translation;
import net.hectus.invade.tasks.ScoreboardTimer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.postgresql.util.PGInterval;

import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static net.hectus.invade.Invade.database;

public class Match {
    public enum State { PRE, IN, END }

    public static final Random RANDOM = new Random();

    public final HashMap<Player, PlayerData> players = new HashMap<>();
    public final ScoreboardTimer scoreboardTimer = new ScoreboardTimer(this, new Time(15, Time.Unit.MINUTES));
    public final World world;
    public State state = State.PRE;
    public Instant startingTime;

    public Match(World world, Player @NotNull ... players) {
        this.world = world;
        for (Player player : players) {
            this.players.put(player, new PlayerData(player));
        }
        generateFeatures(BlockRandomizer.BlockPalette.SCULK, new Location(Bukkit.getWorld("world"), 0, 0, 0), new Location(Bukkit.getWorld("world"), 0, 0, 0));
        start();
    }

    public void start() {
        startingTime = Instant.now();
        new BukkitRunnable() {
            int timer = 5;
            @Override
            public void run() {
                if (timer == 0) {
                    for (Player player : players.keySet()) {
                        player.showTitle(Title.title(Translation.component(player.locale(), "match.start.title.start", timer), Translation.component(player.locale(), "match.start.title.start.subtitle")));
                    }
                    state = State.IN;
                    scoreboardTimer.start();
                    cancel();
                }
                for (Player player : players.keySet()) {
                    player.showTitle(Title.title(Translation.component(player.locale(), "match.start.title.timer", timer), Translation.component(player.locale(), "match.start.title.timer.subtitle")));
                }
                timer--;
            }
        }.runTaskTimer(Invade.getPlugin(Invade.class), 0, 20);
    }

    public void stop(Player... winners) throws SQLException {
        for (Player player : players.keySet()) {
            player.showTitle(Title.title(Translation.component(player.locale(), "match.end.title"), Translation.component(player.locale(), "match.end.time.subtitle")));

            UUID uuid = player.getUniqueId();
            if (!database.contains(uuid)) {
                database.add(uuid, player.getName());
            }
            database.set(uuid, "matches", (int) database.get(uuid, "matches") + 1);
            if (List.of(winners).contains(player)) {
                database.set(uuid, "wins", (int) database.get(uuid, "wins") + 1);
            } else if (winners.length == 0) {
                database.set(uuid, "ties", (int) database.get(uuid, "ties") + 1);
            } else {
                database.set(uuid, "loses", (int) database.get(uuid, "loses") + 1);
            }
            database.set(uuid, "playtime", new PGInterval(0, 0, 0, 0, 0, ((PGInterval) database.get(uuid, "playtime")).getWholeSeconds() + (Instant.now().getEpochSecond() - startingTime.getEpochSecond())));
        }
    }

    public void generateFeatures(@NotNull BlockRandomizer.BlockPalette blockPalette, @NotNull Location c1, @NotNull Location c2) {
        c1.getWorld().getPlayers().forEach(player -> player.showTitle(Title.title(Translation.component(player.locale(), "match.start.generation"), Translation.component(player.locale(), "match.start.generation.subtitle"))));
        for (int i = 0; i < RANDOM.nextInt(10, 24); i++) {
            Block targetBlock;
            do {
                targetBlock = new Location(
                        c1.getWorld(),
                        RANDOM.nextDouble(Math.min(c1.x(), c2.x()), Math.max(c1.x(), c2.x())),
                        RANDOM.nextDouble(Math.min(c1.y(), c2.y()), Math.max(c1.y(), c2.y())),
                        RANDOM.nextDouble(Math.min(c1.z(), c2.z()), Math.max(c1.z(), c2.z()))
                ).getBlock();
            } while (targetBlock.isEmpty());
            BlockRandomizer.patch(targetBlock, RANDOM.nextInt(4, 20), blockPalette);
        }
    }
}
