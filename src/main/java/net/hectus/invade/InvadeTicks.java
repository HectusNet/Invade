package net.hectus.invade;

import com.marcpg.data.time.Time;
import net.hectus.Translation;
import net.hectus.invade.events.TaskEvents;
import net.hectus.invade.matches.Match;
import net.hectus.invade.tasks.CleaningTask;
import net.hectus.invade.tasks.TransportTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;

import static com.marcpg.color.McFormat.*;

public class InvadeTicks {
    public final Time time;
    private static final ScoreboardManager manager = Bukkit.getScoreboardManager();
    private final Match match;
    private BukkitTask task;

    public InvadeTicks(Match match, Time time) {
        this.match = match;
        this.time = time;
    }

    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(Invade.getPlugin(Invade.class), () -> {
            time.decrement();
            if (time.get() <= 0) stop();

            for (HashMap.Entry<Player, PlayerData> entry : match.players.entrySet()) {
                checkValid(entry.getValue());
                updateScoreboard(this, entry.getValue());
                updateActionBar(entry.getValue());
            }
        }, 0, 20);
    }

    public void stop() {
        task.cancel();
    }

    public static void checkValid(@NotNull PlayerData playerData) {
        if (playerData.currentTask().isInvalid()) {
            playerData.removePoints(1);
            playerData.nextTask(false);
        }
    }

    public static void updateScoreboard(@NotNull InvadeTicks ticker, @NotNull PlayerData playerData) {
        Locale l = playerData.player.locale();

        Scoreboard scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("invade", Criteria.DUMMY, MiniMessage.miniMessage().deserialize("<gradient #8F00FF #61C3CB>Invade</gradient>"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        objective.getScore(Translation.string(l, "scoreboard.time") + " " + PURPLE + ticker.time.getOneUnitFormatted()).setScore(6);
        objective.getScore(" ").setScore(5);
        objective.getScore(Translation.string(l, "scoreboard.task") + " " + BLUE + playerData.currentTask().getTranslated(l)).setScore(4);
        objective.getScore(Translation.string(l, "scoreboard.completed_tasks") + " " + BLUE + playerData.completedTasks()).setScore(3);
        objective.getScore(Translation.string(l, "scoreboard.points") + " " + BLUE + playerData.points()).setScore(2);
        objective.getScore("  ").setScore(1);
        objective.getScore(Translation.string(l, "scoreboard.kills") + " " + RED + playerData.kills()).setScore(0);

        playerData.player.setScoreboard(scoreboard);
    }

    public static void updateActionBar(PlayerData playerData) {
        Locale l = playerData.player.locale();
        if (playerData.currentTask() instanceof CleaningTask cleaningTask) {
            playerData.player.sendActionBar(Translation.component(l, "task.cleaning.actionbar.left", cleaningTask.blocksLeft).color(NamedTextColor.GRAY));
        } else if (playerData.currentTask() instanceof TransportTask transportTask && transportTask.foundItem) {
            if (TaskEvents.isInField(playerData.player.getLocation(), transportTask.destination.corner1, transportTask.destination.corner2)) {
                playerData.player.sendActionBar(Translation.component(l, "task.transport.actionbar.sneak").color(NamedTextColor.GREEN).decorate(TextDecoration.UNDERLINED));
            } else {
                playerData.player.sendActionBar(Translation.component(l, "task.transport.actionbar.found").color(NamedTextColor.GRAY));
            }
        }
    }
}
