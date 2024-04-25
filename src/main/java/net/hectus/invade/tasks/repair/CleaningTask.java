package net.hectus.invade.tasks.repair;

import com.marcpg.libpg.lang.Translation;
import net.hectus.invade.PlayerData;
import net.hectus.invade.match.Match;
import net.hectus.invade.tasks.Task;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class CleaningTask extends Task {
    public int blocksLeft;

    public CleaningTask(Match match, Player player, PlayerData playerData, int blocksLeft) {
        super(match, player, playerData);
        this.blocksLeft = blocksLeft;

        player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
        player.getInventory().addItem(new ItemStack(Material.IRON_SHOVEL));
        player.getInventory().addItem(new ItemStack(Material.IRON_HOE));
    }

    public boolean addCleanedBlock() {
        return blocksLeft-- <= 1; // 1 because otherwise it will be one block late, as it detects the block break before adding the points
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
        return Translation.string(locale, "task.cleaning", blocksLeft);
    }
}
