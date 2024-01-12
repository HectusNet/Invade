package net.hectus.invade.tasks;

import net.hectus.Translation;
import net.hectus.invade.Building;
import net.hectus.invade.matches.Match;
import org.bukkit.entity.Player;

import java.util.Locale;

public class CheckPointTask extends Task {
    public final Building destination;

    public CheckPointTask(Match match, Player player, Building destination) {
        super(match, player);
        this.destination = destination;
    }

    @Override
    public int points() {
        return 4;
    }

    @Override
    public String getTranslated(Locale locale) {
        return Translation.string(locale, "task.checkpoint.info", destination.translate(locale));
    }
}
