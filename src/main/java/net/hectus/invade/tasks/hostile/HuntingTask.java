package net.hectus.invade.tasks.hostile;

import net.hectus.invade.PlayerData;
import net.hectus.invade.match.Match;
import net.hectus.invade.tasks.Task;
import net.hectus.lang.Translation;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Locale;

public class HuntingTask extends Task {
    public final int required;
    public int killed = 0;

    public HuntingTask(Match match, Player player, PlayerData playerData, int required) {
        super(match, player, playerData);
        this.required = required;
    }

    public boolean addKill() {
        return killed++ >= required;
    }

    @Override
    public boolean isInvalid() {
        return match.world.getPlayers().stream().filter(target -> target.getGameMode() == GameMode.SURVIVAL).toList().size() <= required;
    }

    @Override
    public int points() {
        return 22;
    }

    @Override
    public String getTranslated(Locale locale) {
        return Translation.string(locale, "task.hunting.info", required - killed);
    }
}
