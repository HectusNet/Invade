package net.hectus.invade.tasks;

import com.marcpg.text.Formatter;
import net.hectus.Translation;
import net.hectus.invade.Building;
import net.hectus.invade.matches.Match;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Locale;

public class TransportTask extends ItemSearchTask {
    public final Building destination;

    public TransportTask(Match match, Player player, Material item, Building destination) {
        super(match, player, item);
        this.destination = destination;
    }

    @Override
    public int points() {
        return 6;
    }

    @Override
    public Component getTranslated(Locale locale) {
        return Translation.component(locale, "task.transport.info", Formatter.toPascalCase(item.name()), destination.translate(locale));
    }
}
