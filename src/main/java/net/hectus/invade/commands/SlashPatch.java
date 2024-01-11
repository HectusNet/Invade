package net.hectus.invade.commands;

import net.hectus.invade.BlockRandomizer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SlashPatch implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length != 2) return false;

        if (sender instanceof Player player) {
            Block targetBlock = player.getTargetBlockExact(128);
            if (targetBlock != null) {
                long time = BlockRandomizer.patch(targetBlock, Integer.parseInt(args[0]), BlockRandomizer.BlockPalette.valueOf(args[1]));
                player.sendMessage(Component.text("Done! Took a total of: " + time + "ms", NamedTextColor.GREEN));
            } else {
                player.sendMessage(Component.text("You aren't looking at any block!", NamedTextColor.RED));
            }
        } else {
            sender.sendMessage(Component.text("Only players can execute this command!", NamedTextColor.RED));
        }
        return true;
    }
}
