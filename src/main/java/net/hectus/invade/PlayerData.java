package net.hectus.invade;

import com.destroystokyo.paper.ParticleBuilder;
import com.marcpg.util.Randomizer;
import net.hectus.invade.matches.Match;
import net.hectus.invade.tasks.*;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public class PlayerData {
    private static final Random RANDOM = new Random();

    public final Player player;
    public final Match match;
    private Task currentTask;
    private int points = 0;
    private int kills = 0;
    private boolean dead;

    public PlayerData(Player player, Match match) {
        this.player = player;
        this.match = match;
        nextTask();
    }

    public void nextTask() {
        if (currentTask != null) currentTask.done();

        currentTask = switch (RANDOM.nextInt(5)) {
            case 0 -> new BountyTask(match, player, Randomizer.fromCollection(match.players.entrySet().stream()
                    .filter(targetEntry -> targetEntry.getKey() != player && !targetEntry.getValue().isDead())
                    .map(Map.Entry::getKey)
                    .toList()));
            case 1 -> new CheckPointTask(match, player, Randomizer.fromArray(Building.values()));
            case 2 -> new CleaningTask(match, player, match.palette, RANDOM.nextInt(25, 80));
            case 3 -> new TransportTask(match, player, Randomizer.fromCollection(match.VALID_ITEMS), Randomizer.fromCollection(Building.destinations()));
            default -> new ItemSearchTask(match, player, Randomizer.fromCollection(match.VALID_ITEMS));
        };
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        new ParticleBuilder(Particle.REDSTONE).location(player.getLocation()).color(0, 255, 0).count(15).receivers(player).spawn();
    }

    public Task currentTask() {
        return currentTask;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void removePoints(int points) {
        this.points -= points;
    }

    public int points() {
        return points;
    }

    public void addKill() {
        kills++;
    }

    public int kills() {
        return kills;
    }

    public boolean isDead() {
        return dead;
    }

    public void nowDead() {
        dead = true;
    }
}
