package net.hectus.invade.tasks.item;

import com.marcpg.text.Formatter;
import net.hectus.Translation;
import net.hectus.invade.matches.Match;
import net.hectus.invade.tasks.Task;
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
    public int points() {
        return 3;
    }

    @Override
    public String getTranslated(Locale locale) {
        return Translation.string(locale, "task.item_search.info", Formatter.toPascalCase(item.name()));
    }
}
