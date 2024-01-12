package net.hectus.invade.tasks;

import net.hectus.invade.matches.Match;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;

public abstract class Task {
    public final Match match;
    public final Player player;

    public Task(Match match, Player player) {
        this.match = match;
        this.player = player;
    }

    @ApiStatus.OverrideOnly
    public boolean isInvalid() {
        return false;
    }

    @ApiStatus.OverrideOnly
    public void done() {}

    public int points() {
        return 1;
    }

    public String getTranslated(Locale locale) {
        return "";
    }
}
