package net.hectus.invade.matches;

import com.marcpg.data.time.Time;
import net.hectus.invade.Invade;
import net.hectus.invade.PlayerData;
import net.hectus.invade.tasks.ScoreboardTimer;
import net.hectus.Translation;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.postgresql.util.PGInterval;

import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static net.hectus.invade.Invade.database;

public class Match {
    public enum State { PRE, IN, END }

    public final HashMap<Player, PlayerData> players = new HashMap<>();
    public final ScoreboardTimer scoreboardTimer = new ScoreboardTimer(this, new Time(15, Time.Unit.MINUTES));
    public State state = State.PRE;
    public Instant startingTime;

    public Match(Player @NotNull ... players) {
        for (Player player : players) {
            this.players.put(player, new PlayerData(player));
        }
    }

    public Match(List<Player> players) {
        for (Player player : players) {
            this.players.put(player, new PlayerData(player));
        }
    }

    public void addPlayer(Player player) {
        players.put(player, new PlayerData(player));
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
}
