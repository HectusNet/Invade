package net.hectus.invade.tasks;

import net.hectus.Translation;
import net.hectus.invade.Building;
import net.hectus.invade.matches.Match;
import net.kyori.adventure.text.Component;
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
    public Component getTranslated(Locale locale) {
        return Translation.component(locale, "task.checkpoint.info", destination.translate(locale));
    }
}
