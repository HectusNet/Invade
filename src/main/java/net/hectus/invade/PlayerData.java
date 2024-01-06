package net.hectus.invade;

import com.marcpg.util.Randomizer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlayerData {
    private final Player player;
    private Material currentTask;
    private int points = 0;
    private int kills = 0;

    public PlayerData(Player player) {
        this.player = player;
        nextTask();
    }

    public void nextTask() {
        Material nextTask = currentTask;
        do nextTask = Randomizer.fromArray(Material.values());
        while (nextTask == currentTask);

        currentTask = nextTask;
    }

    public Material currentTask() {
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
}
