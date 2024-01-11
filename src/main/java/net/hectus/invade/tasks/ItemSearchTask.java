package net.hectus.invade.tasks;

import net.hectus.Translation;
import net.hectus.invade.matches.Match;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class ItemSearchTask extends Task {
    public final Material item;
    public boolean foundItem = false;

    public ItemSearchTask(Match match, Player player, Material item) {
        super(match, player);
        this.item = item;
    }

    @Override
    public boolean isInvalid() {
        return !match.world.getEntities().stream()
                .filter(entity -> entity.getType() == EntityType.ITEM_FRAME)
                .map(entity -> (ItemFrame) entity)
                .filter(itemFrame -> itemFrame.getItem().getType() == item)
                .toList()
                .isEmpty();
    }

    @Override
    public Component getTranslated(Locale locale) {
        return Translation.component(locale, "task.item_search.info", "").append(Component.translatable(new ItemStack(item)));
    }
}
