package net.hectus.invade;

import com.marcpg.data.time.Time;
import com.marcpg.util.Randomizer;
import net.hectus.Translation;
import net.hectus.invade.events.TaskEvents;
import net.hectus.invade.matches.Match;
import net.hectus.invade.tasks.repair.CleaningTask;
import net.hectus.invade.tasks.repair.TokenCollectTask;
import net.hectus.invade.tasks.item.TransportTask;
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
    public static final String COMPASS = "S  ·  ◈  ·  ◈  ·  ◈  ·  SW  ·  ◈  ·  ◈  ·  ◈  ·  W  ·  ◈  ·  ◈  ·  ◈  ·  NW  ·  ◈  ·  ◈  ·  ◈  ·  N  ·  ◈  ·  ◈  ·  ◈  ·  NE  ·  ◈  ·  ◈  ·  ◈  ·  E  ·  ◈  ·  ◈  ·  ◈  ·  SE  ·  ◈  ·  ◈  ·  ◈  ·  S  ·  ◈  ·  ◈  ·  ◈  ·  SW  ·  ◈  ·  ◈  ·  ◈  ·  W  ·  ◈  ·  ◈  ·  ◈  ·  NW  ·  ◈  ·  ◈  ·  ◈  ·  N  ·  ◈  ·  ◈  ·  ◈  ·  NE  ·  ◈  ·  ◈  ·  ◈  ·  E  ·  ◈  ·  ◈  ·  ◈  ·  SE  ·  ◈  ·  ◈  ·  ◈  ·  ";

    public final Time time;
    private static final ScoreboardManager manager = Bukkit.getScoreboardManager();
    private final Match match;
    private BukkitTask task;
    private boolean second;

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
                if (time.get() % 60 == 0) match.spawnMobs(Randomizer.fromArray(Building.values()).middle().toLocation(match.world));
            }

            for (HashMap.Entry<Player, PlayerData> entry : match.players.entrySet()) {
                checkValid(entry.getValue());
                updateScoreboard(this, entry.getValue());
                updateActionBar(entry.getValue());
            }
        }, 0, 10);
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
        Objective objective = scoreboard.registerNewObjective("invade", Criteria.DUMMY, Component.text("§x§8§F§0§0§F§F&lI§x§8§6§2§7§F§5&ln§x§7§D§4§E§E§A&lv§x§7§3§7§5§E§0&la§x§6§A§9§C§D§5&ld§x§6§1§C§3§C§B&le" + RED + " Beta"));
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

    public static void updateCompass(@NotNull PlayerData playerData) {
        int chars = COMPASS.length();
        int index = (int) (normalize(playerData.player.getYaw()) * chars / 720.0f + chars * 0.5f);
        playerData.compass.name(Component.text(COMPASS.substring(index - 25, index + 25)));
    }

    private static float normalize(float yaw) {
        while (yaw < -180.0f) yaw += 360.0f;
        while (yaw > 180f) yaw -= 360f;
        return yaw;
    }
}
