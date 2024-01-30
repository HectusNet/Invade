package net.hectus.invade.tasks;

import net.hectus.invade.PlayerData;
import net.hectus.invade.match.Match;
import net.hectus.lang.Translatable;
import org.bukkit.entity.Player;

public abstract class Task extends Translatable {
    public final Match match;
    public final Player player;
    public final PlayerData playerData;

    public Task(Match match, Player player, PlayerData playerData) {
        this.match = match;
        this.player = player;
        this.playerData = playerData;
    }

    public boolean isInvalid() {
        return false;
    }

    public void tick() {}

    public void done() {}

    public abstract int points();
}
