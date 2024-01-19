package net.hectus.invade.tasks.movement;

import net.hectus.Translation;
import net.hectus.invade.Building;
import net.hectus.invade.matches.Match;
import net.hectus.invade.tasks.Task;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.Locale;

public class EscortTask extends Task { // TODO: Make the villager follow the player
    public final Building destination;
    public final Villager villager;

    public EscortTask(Match match, Player player, Building destination, Location villagerLocation) {
        super(match, player);
        this.destination = destination;

        villager = match.world.spawn(villagerLocation, Villager.class);
        villager.setProfession(Villager.Profession.NONE);
        villager.setVillagerType(Villager.Type.PLAINS);
    }

    @Override
    public int points() {
        return 12;
    }

    @Override
    public String getTranslated(Locale locale) {
        return Translation.string(locale, "task.escort.info", destination.translate(locale));
    }
}
