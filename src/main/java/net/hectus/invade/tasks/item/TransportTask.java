package net.hectus.invade.tasks.item;

import com.marcpg.text.Formatter;
import net.hectus.invade.structures.Cord;
import net.hectus.invade.structures.Building;
import net.hectus.invade.PlayerData;
import net.hectus.invade.match.Match;
import net.hectus.lang.Translation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.Locale;

public class TransportTask extends ItemSearchTask {
    public final Building destination;
    public boolean foundItem = false;
    private int itemSearchTicks;
    private boolean setBuildingMarker;

    public TransportTask(Match match, Player player, PlayerData playerData, Material item, Building destination) {
        super(match, player, playerData, item);
        this.destination = destination;
    }

    @Override
    public void tick() {
        if (!foundItem) {
            itemSearchTicks++;
            if (itemSearchTicks >= 60) { // 60 ticks = 30 seconds
                Location playerLocation = player.getLocation();
                match.world.getEntitiesByClass(ItemFrame.class).stream()
                        .filter(itemFrame -> itemFrame.getItem().getType() == item)
                        .map(ItemFrame::getLocation)
                        .min(Comparator.comparingDouble(location -> location.distanceSquared(playerLocation)))
                        .ifPresentOrElse(
                                location -> playerData.mapMarker = Cord.fromLocation(location),
                                () -> {
                                    playerData.removePoints(1);
                                    playerData.nextTask(false);
                                }
                        );
            }
        } else {
            if (!setBuildingMarker) {
                setBuildingMarker = true;
                playerData.mapMarker = destination.middle();
            }
        }
    }

    @Override
    public int points() {
        return 8;
    }

    @Override
    public String getTranslated(Locale locale) {
        return Translation.string(locale, "task.transport.info", Formatter.toPascalCase(item.name()), destination.getTranslated(locale));
    }
}
