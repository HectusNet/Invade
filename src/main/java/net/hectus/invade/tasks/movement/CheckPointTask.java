package net.hectus.invade.tasks.movement;

import net.hectus.Translation;
import net.hectus.invade.Building;
import net.hectus.invade.matches.Match;
import net.hectus.invade.tasks.Task;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class CheckPointTask extends Task {
    public final Building destination;

    public CheckPointTask(Match match, Player player, @NotNull Building destination) {
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
