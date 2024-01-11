package net.hectus.invade;

import com.marcpg.data.time.Time;
import net.hectus.Translation;
import net.hectus.invade.matches.Match;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Locale;

import static com.marcpg.color.McFormat.*;

public class ScoreboardTimer {
    static final ScoreboardManager manager = Bukkit.getScoreboardManager();
    final Match match;
    final Time time;
    BukkitTask task;

    public ScoreboardTimer(Match match, Time time) {
        this.match = match;
        this.time = time;
    }

    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(Invade.getPlugin(Invade.class), () -> {
            time.decrement();
            if (time.get() <= 0) stop();

            for (HashMap.Entry<Player, PlayerData> entry : match.players.entrySet()) {
                Locale l = entry.getKey().locale();

                Scoreboard scoreboard = manager.getNewScoreboard();
                Objective objective = scoreboard.registerNewObjective("invade", Criteria.DUMMY, MiniMessage.miniMessage().deserialize("<gradient #8F00FF #61C3CB>Invade</gradient>"));
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                objective.getScore(Translation.string(l, "scoreboard.time") + " " + PURPLE + time.getOneUnitFormatted()).setScore(5);
                objective.getScore(" ").setScore(4);
                objective.getScore(Translation.string(l, "scoreboard.task") + " " + BLUE + entry.getValue().currentTask()).setScore(3);
                objective.getScore(Translation.string(l, "scoreboard.points") + " " + BLUE + entry.getValue().points()).setScore(2);
                objective.getScore("  ").setScore(1);
                objective.getScore(Translation.string(l, "scoreboard.kills") + " " + RED + entry.getValue().kills()).setScore(0);

                entry.getKey().setScoreboard(scoreboard);

                if (entry.getValue().currentTask().isInvalid()) {
                    entry.getValue().removePoints(1);
                    entry.getValue().nextTask();
                }
            }
        }, 0, 20);
    }

    public void stop() {
        task.cancel();
    }
}
