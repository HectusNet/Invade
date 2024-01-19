package net.hectus.invade.tasks;

import net.hectus.Translatable;
import net.hectus.invade.matches.Match;
import org.bukkit.entity.Player;

public abstract class Task extends Translatable {
    public final Match match;
    public final Player player;

    public Task(Match match, Player player) {
        this.match = match;
        this.player = player;
    }

    public boolean isInvalid() {
        return false;
    }

    public void done() {}

    public abstract int points();
}
