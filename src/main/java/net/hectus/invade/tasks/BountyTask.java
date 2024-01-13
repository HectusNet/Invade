package net.hectus.invade.tasks;

import net.hectus.Translation;
import net.hectus.invade.matches.Match;
import org.bukkit.entity.Player;

import java.util.Locale;

public class BountyTask extends Task {
    public final Player target;

    public BountyTask(Match match, Player player, Player target) {
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
