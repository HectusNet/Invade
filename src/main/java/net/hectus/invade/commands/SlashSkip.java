package net.hectus.invade.commands;

import net.hectus.invade.PlayerData;
import net.hectus.invade.match.MatchManager;
import net.hectus.lang.Translation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlashSkip implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            PlayerData playerData = MatchManager.getPlayerData(player);
            if (playerData != null) {
                ItemStack skipItems = player.getInventory().getItem(0);
                if (skipItems == null || skipItems.isEmpty() || skipItems.getAmount() <= 0) {
                    player.sendMessage(Translation.component(player.locale(), "task.skip.no_left").color(NamedTextColor.RED));
                    return true;
                }

                playerData.removePoints(playerData.currentTask().points() / 2);
                playerData.nextTask(false);
                player.sendMessage(Translation.component(player.locale(), "task.skip.success").color(NamedTextColor.YELLOW));
            } else {
                player.sendMessage(Component.text("You can't skip tasks before the match started!", NamedTextColor.RED));
            }
        }
        return true;
    }
}
