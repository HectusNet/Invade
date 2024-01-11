package net.hectus.invade.tasks;

import net.hectus.Translation;
import net.hectus.invade.BlockRandomizer;
import net.hectus.invade.matches.Match;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class CleaningTask extends Task {
    public final BlockRandomizer.BlockPalette palette;
    public int blocksLeft;

    public CleaningTask(Match match, Player player, BlockRandomizer.BlockPalette palette, int blocksLeft) {
        super(match, player);
        this.palette = palette;
        this.blocksLeft = blocksLeft;

        player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
        player.getInventory().addItem(new ItemStack(Material.IRON_SHOVEL));
        player.getInventory().addItem(new ItemStack(Material.IRON_HOE));
    }

    public boolean addCleanedItems(int amount) {
        return (blocksLeft -= amount) <= 0;
    }

    public boolean addCleanedItem() {
        return blocksLeft-- <= 0;
    }

    @Override
    public boolean isInvalid() {
        return super.isInvalid();
    }

    @Override
    public void done() {
        player.getInventory().remove(Material.IRON_PICKAXE);
        player.getInventory().remove(Material.IRON_SHOVEL);
        player.getInventory().remove(Material.IRON_HOE);
    }

    @Override
    public int points() {
        return 8;
    }

    @Override
    public Component getTranslated(Locale locale) {
        return Translation.component(locale, "task.cleaning.info", blocksLeft);
    }
}
