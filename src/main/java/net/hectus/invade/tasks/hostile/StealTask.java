package net.hectus.invade.tasks.hostile;

import net.hectus.invade.Cord;
import net.hectus.invade.PlayerData;
import net.hectus.invade.matches.Match;
import net.hectus.invade.tasks.Task;
import net.hectus.lang.Translation;
import org.bukkit.entity.Player;

import java.util.Locale;

public class StealTask extends Task {
    public final Player target;

    public StealTask(Match match, Player player, PlayerData playerData, Player target) {
        super(match, player, playerData);
        this.target = target;
    }

    @Override
    public boolean isInvalid() {
        return match.players.get(target).isDead();
    }

    @Override
    public void tick() {
        playerData.mapMarker = Cord.fromLocation(target.getLocation());
    }

    @Override
    public int points() {
        return 8;
    }

    @Override
    public String getTranslated(Locale locale) {
        return Translation.string(locale, "task.steal.info", target.getName());
    }
}
