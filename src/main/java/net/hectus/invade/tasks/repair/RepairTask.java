package net.hectus.invade.tasks.repair;

import com.marcpg.libpg.lang.Translation;
import net.hectus.invade.PlayerData;
import net.hectus.invade.match.Match;
import net.hectus.invade.structures.Cord;
import net.hectus.invade.tasks.Task;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class RepairTask extends Task {
    public enum Repairable {
        VENDING_MACHINE, SECURITY_SYSTEM, LIGHT, TOILET, DUCT_CLEANING, DUCT_VENTILATOR;

        public String getTranslated(Locale locale) {
            return Translation.string(locale, "task.repairable." + name().toLowerCase());
        }
    }

    public final Cord location;
    public final Repairable broken;

    public RepairTask(Match match, Player player, PlayerData playerData, @NotNull Repairable broken) {
        super(match, player, playerData);
        this.broken = broken;

        // TODO: Set `location` to the location of the Repairable
        location = switch (broken) {
            // case SECURITY_SYSTEM -> (new Random().nextBoolean() ? Building.EAST_SIDE_SECURITY : Building.WEST_SIDE_SECURITY).middle();
            // case TOILET -> (new Random().nextBoolean() ? Building.EAST_SIDE_TOILETS : Building.WEST_SIDE_TOILETS).middle();
            default -> null;
        };

        playerData.mapMarker = location;
    }

    @Override
    public int points() {
        return 12;
    }

    @Override
    public String getTranslated(Locale locale) {
        return Translation.string(locale, "task.repair", broken.getTranslated(locale));
    }
}
