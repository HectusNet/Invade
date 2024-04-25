package net.hectus.invade.tasks.hostile;

import com.marcpg.libpg.lang.Translation;
import net.hectus.invade.PlayerData;
import net.hectus.invade.match.Match;
import net.hectus.invade.structures.Cord;
import net.hectus.invade.tasks.Task;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class BountyTask extends Task {
    public final Player target;

    public BountyTask(Match match, Player player, PlayerData playerData, @NotNull Player target) {
        super(match, player, playerData);
        this.target = target;
    }

    @Override
    public boolean isInvalid() {
        return target.getGameMode() != GameMode.SURVIVAL;
    }

    @Override
    public void tick() {
        playerData.mapMarker = Cord.fromLocation(target.getLocation());
    }

    @Override
    public int points() {
        return 12;
    }

    @Override
    public String getTranslated(Locale locale) {
        return Translation.string(locale, "task.bounty", target.getName());
    }
}
