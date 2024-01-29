package net.hectus.invade;

import com.marcpg.data.time.Time;
import com.marcpg.util.Randomizer;
import net.hectus.invade.events.TaskEvents;
import net.hectus.invade.matches.Match;
import net.hectus.invade.tasks.hostile.TokenCollectTask;
import net.hectus.invade.tasks.item.TransportTask;
import net.hectus.invade.tasks.repair.CleaningTask;
import net.hectus.lang.Translation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;

import static com.marcpg.color.McFormat.*;

public class InvadeTicks {
    private static final ScoreboardManager manager = Bukkit.getScoreboardManager();
    private final Match match;
    public final Time time;
    private BukkitTask task;
    private boolean second;
    private static final Component SCOREBOARD_TITLE = Component.text().append(Component.text("I", TextColor.color(143, 0, 255))).append(Component.text("n", TextColor.color(134, 39, 245))).append(Component.text("v", TextColor.color(125, 78, 234))).append(Component.text("a", TextColor.color(115, 117, 224))).append(Component.text("d", TextColor.color(106, 156, 213))).append(Component.text("e", TextColor.color(97, 195, 203))).append(Component.text("-", TextColor.color(176, 140, 144))).append(Component.text("-", TextColor.color(255, 85, 85))).build();

    public InvadeTicks(Match match, Time time) {
        this.match = match;
        this.time = time;
    }

    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(Invade.PLUGIN, () -> {
            second = !second;
            if (second) {
                time.decrement();
                if (time.get() <= 0) stop();
                if (time.get() == 780 && time.get() != 900) match.graceTime = false;
                if (time.get() % 30 == 0)
                    match.spawnMobs(Randomizer.fromArray(Building.values()).middle().toLocation(match.world));
            }

            for (HashMap.Entry<Player, PlayerData> entry : match.players.entrySet()) {
                updateTasks(entry.getValue());
                updateScoreboard(this, entry.getValue());
                updateActionBar(entry.getValue());
            }
        }, 0, 10);
    }

    public void stop() {
        task.cancel();
        try {
            match.stop(match.players.keySet().toArray(new Player[0]));
        } catch (SQLException e) {
            Invade.LOG.severe("Couldn't save game results into invade playerdata!");
        }
    }

    public static void updateTasks(@NotNull PlayerData playerData) {
        playerData.currentTask().tick();
        if (playerData.currentTask().isInvalid()) {
            playerData.removePoints(1);
            playerData.nextTask(false);
        }
    }

    public static void updateScoreboard(@NotNull InvadeTicks ticker, @NotNull PlayerData playerData) {
        Locale l = playerData.player.locale();

        Scoreboard scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("invade", Criteria.DUMMY, SCOREBOARD_TITLE);
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

    public static void updateActionBar(@NotNull PlayerData playerData) {
        Locale l = playerData.player.locale();
        if (playerData.currentTask() instanceof CleaningTask cleaningTask) {
            playerData.player.sendActionBar(Translation.component(l, "task.cleaning.actionbar.left", cleaningTask.blocksLeft).color(NamedTextColor.GRAY));
        } else if (playerData.currentTask() instanceof TransportTask transportTask && transportTask.foundItem) {
            if (TaskEvents.isInField(playerData.player.getLocation(), transportTask.destination.corner1, transportTask.destination.corner2)) {
                playerData.player.sendActionBar(Translation.component(l, "task.transport.actionbar.sneak").color(NamedTextColor.GREEN).decorate(TextDecoration.UNDERLINED));
            } else {
                playerData.player.sendActionBar(Translation.component(l, "task.transport.actionbar.found").color(NamedTextColor.GRAY));
            }
        } else if (playerData.currentTask() instanceof TokenCollectTask tokenCollectTask) {
            playerData.player.sendActionBar(Translation.component(l, "task.token_collect.actionbar", tokenCollectTask.tokens, tokenCollectTask.tokenRequirement).color(NamedTextColor.GREEN));
        }
    }

    public static final String COMPASS_TEMPLATE = "S  ·  -  ·  -  ·  -  ·  SW  ·  -  ·  -  ·  -  ·  W  ·  -  ·  -  ·  -  ·  NW  ·  -  ·  -  ·  -  ·  N  ·  -  ·  -  ·  -  ·  NE  ·  -  ·  -  ·  -  ·  E  ·  -  ·  -  ·  -  ·  SE  ·  -  ·  -  ·  -  ·  S  ·  -  ·  -  ·  -  ·  SW  ·  -  ·  -  ·  -  ·  W  ·  -  ·  -  ·  -  ·  NW  ·  -  ·  -  ·  -  ·  N  ·  -  ·  -  ·  -  ·  NE  ·  -  ·  -  ·  -  ·  E  ·  -  ·  -  ·  -  ·  SE  ·  -  ·  -  ·  -  ·  "; // ◈

    public static void updateCompass(@NotNull PlayerData playerData) {
        String compass = COMPASS_TEMPLATE;
        if (playerData.mapMarker != null) {
            Vector playerToMarker = playerData.mapMarker.toLocation(playerData.match.world).toVector().subtract(playerData.player.getLocation().toVector()).normalize();
            int markerIndex = index((float) Math.toDegrees(Math.atan2(-playerToMarker.getX(), playerToMarker.getZ())), COMPASS_TEMPLATE.length());
            compass = compass.substring(0, markerIndex - 4) + GREEN + "->░█░<-" + GREEN + compass.substring(markerIndex + 5);
        }

        int index = index(playerData.player.getYaw(), compass.length());
        playerData.compass.name(Component.text(compass.substring(index - 25, index + 25)));
    }

    public static int index(float yaw, int chars) {
        return (int) (normalize(yaw) * chars / 720.0f + chars * 0.5f);
    }

    private static float normalize(float yaw) {
        while (yaw < -180.0f) yaw += 360.0f;
        while (yaw > 180f) yaw -= 360f;
        return yaw;
    }
}
