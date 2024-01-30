package net.hectus.invade.game_events;

import net.hectus.invade.match.Match;
import net.hectus.lang.Translatable;

public abstract class Event extends Translatable {
    public final Match match;

    public Event(Match match) {
        this.match = match;
    }

    public void done() {}

    public abstract void run();
}
