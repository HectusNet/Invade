package net.hectus.invade.tasks.movement;

import net.hectus.invade.Building;
import net.hectus.invade.Invade;
import net.hectus.invade.PlayerData;
import net.hectus.invade.matches.Match;
import net.hectus.invade.tasks.Task;
import net.hectus.lang.Translation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitTask;

import java.util.Locale;

public class EscortTask extends Task {
    public final Building destination;
    public final Villager villager;

    public BukkitTask pathfindingUpdater;

    public EscortTask(Match match, Player player, PlayerData playerData, Building destination, Location villagerLocation) {
        super(match, player, playerData);
        this.destination = destination;

        villager = match.world.spawn(villagerLocation, Villager.class, v -> v.setProfession(Villager.Profession.NONE));
        villager.customName(Component.text("Survivor", NamedTextColor.YELLOW, TextDecoration.BOLD));
        startPathfinderUpdating();
    }

    public void startPathfinderUpdating() {
        pathfindingUpdater = Bukkit.getScheduler().runTaskTimer(Invade.PLUGIN, () -> {
            if (villager.getLocation().distance(player.getLocation()) < 10) {
                villager.getPathfinder().moveTo(player);
            } else {
                villager.getPathfinder().stopPathfinding();
            }
        }, 20, 20);
    }

    @Override
    public int points() {
        return 12;
    }

    @Override
    public String getTranslated(Locale locale) {
        return Translation.string(locale, "task.escort.info", destination.getTranslated(locale));
    }
}
