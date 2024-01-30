package net.hectus.invade.game_events;

import net.hectus.invade.match.Match;

import java.util.Locale;

public class ApocalypseEvent extends Event {
    public ApocalypseEvent(Match match) {
        super(match);
    }

    @Override
    public void run() {

    }

    @Override
    public String getTranslated(Locale locale) {
        return null;
    }
}
