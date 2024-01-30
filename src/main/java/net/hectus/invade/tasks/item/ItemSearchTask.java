package net.hectus.invade.tasks.item;

import com.marcpg.text.Formatter;
import net.hectus.invade.structures.Cord;
import net.hectus.invade.PlayerData;
import net.hectus.invade.match.Match;
import net.hectus.invade.tasks.Task;
import net.hectus.lang.Translation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.Locale;

public class ItemSearchTask extends Task {
    public final Material item;
    private int ticks;

    public ItemSearchTask(Match match, Player player, PlayerData playerData, Material item) {
        super(match, player, playerData);
        this.item = item;
    }

    @Override
    public void tick() {
        ticks++;
        if (ticks++ > 60) { // 60 ticks = 30 seconds
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
    }

    @Override
    public int points() {
        return 3;
    }

    @Override
    public String getTranslated(Locale locale) {
        return Translation.string(locale, "task.item_search.info", Formatter.toPascalCase(item.name()));
    }
}
