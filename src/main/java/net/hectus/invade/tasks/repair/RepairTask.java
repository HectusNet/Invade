package net.hectus.invade.tasks.repair;

import net.hectus.invade.Building;
import net.hectus.invade.Cord;
import net.hectus.invade.PlayerData;
import net.hectus.invade.matches.Match;
import net.hectus.invade.tasks.Task;
import net.hectus.lang.Translation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Random;

public class RepairTask extends Task { // TODO: Code this with map marker, etc.
    public enum Repairable {
        VENDING_MACHINE, SECURITY_SYSTEM, LIGHT, TOILET, DUCT_CLEANING, DUCT_VENTILATOR;

        public String getTranslated(Locale locale) {
            return Translation.string(locale, "task.repair.repairable." + name().toLowerCase());
        }
    }

    public final Cord location;
    public final Repairable broken;

    public RepairTask(Match match, Player player, PlayerData playerData, @NotNull Repairable broken) {
        super(match, player, playerData);
        this.broken = broken;

        location = switch (broken) {
            case VENDING_MACHINE, SECURITY_SYSTEM, LIGHT, DUCT_CLEANING, DUCT_VENTILATOR -> null;
            case TOILET -> new Random().nextBoolean() ? Building.WEST_SIDE_TOILETS.middle() : Building.EAST_SIDE_TOILETS.middle();
        };
        playerData.mapMarker = location;
    }

    @Override
    public int points() {
        return 12;
    }

    @Override
    public String getTranslated(Locale locale) {
        return Translation.string(locale, "task.repair.info");
    }
}
