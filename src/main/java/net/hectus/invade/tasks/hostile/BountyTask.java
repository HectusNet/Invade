package net.hectus.invade.tasks.hostile;

import net.hectus.Translation;
import net.hectus.invade.matches.Match;
import net.hectus.invade.tasks.Task;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class BountyTask extends Task {
    public final Player target;

    public BountyTask(Match match, Player player, @NotNull Player target) {
        super(match, player);
        this.target = target;
    }

    @Override
    public boolean isInvalid() {
        return match.players.get(target).isDead();
    }

    @Override
    public int points() {
        return 12;
    }

    @Override
    public String getTranslated(Locale locale) {
        return Translation.string(locale, "task.bounty.info", target.getName());
    }
}
