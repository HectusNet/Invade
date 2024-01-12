package net.hectus.invade.tasks;

import net.hectus.Translation;
import net.hectus.invade.BlockRandomizer;
import net.hectus.invade.matches.Match;
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

    public boolean addCleanedBlock() {
        return blocksLeft-- <= 0;
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
    public String getTranslated(Locale locale) {
        return Translation.string(locale, "task.cleaning.info", blocksLeft);
    }
}
