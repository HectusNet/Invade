package net.hectus.invade.game_events;

import net.hectus.invade.matches.Match;

import java.util.Locale;

public abstract class Event {
    public final Match match;

    protected Event(Match match) {
        this.match = match;
    }

    public void done() {}

    public abstract void run();

    public abstract String getTitleTranslated(Locale locale);

    public abstract String getDescriptionTranslated(Locale locale);
}
