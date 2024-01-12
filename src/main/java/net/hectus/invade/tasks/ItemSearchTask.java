package net.hectus.invade.tasks;

import com.marcpg.text.Formatter;
import net.hectus.Translation;
import net.hectus.invade.matches.Match;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Locale;

public class ItemSearchTask extends Task {
    public final Material item;
    public boolean foundItem = false;

    public ItemSearchTask(Match match, Player player, Material item) {
        super(match, player);
        this.item = item;
    }

    @Override
    public String getTranslated(Locale locale) {
        return Translation.string(locale, "task.item_search.info", Formatter.toPascalCase(item.name()));
    }
}
