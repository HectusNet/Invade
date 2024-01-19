package net.hectus.invade.tasks.repair;

import net.hectus.Translation;
import net.hectus.invade.Building;
import net.hectus.invade.PlayerData;
import net.hectus.invade.matches.Match;
import net.hectus.invade.tasks.Task;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Random;

public class RepairTask extends Task { // TODO: Code this with map marker, etc.
    public enum Repairable {
        VENDING_MACHINE, SECURITY_SYSTEM, LIGHT, TOILET;

        public String getTranslated(Locale locale) {
            return Translation.string(locale, "task.repair.repairable." + name().toLowerCase());
        }

    }

    public final Building.Cord location;
    public final Repairable broken;

    public RepairTask(Match match, Player player, @NotNull Repairable broken, @NotNull PlayerData playerData) {
        super(match, player);
        this.broken = broken;

        location = switch (broken) {
            case VENDING_MACHINE, SECURITY_SYSTEM, LIGHT -> null;
            case TOILET -> new Random().nextBoolean() ? Building.WC1.middle() : Building.WC2.middle();
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
