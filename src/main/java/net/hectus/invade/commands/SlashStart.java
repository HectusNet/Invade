package net.hectus.invade.commands;

import net.hectus.invade.match.Match;
import net.hectus.invade.match.MatchManager;
import net.hectus.invade.structures.BlockRandomizer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SlashStart implements CommandExecutor, TabExecutor {
    public static final List<String> VALID_THEMES = List.of("sculk", "slime", "nether", "overworld", "end");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length != 1) {
            sender.sendMessage(Component.text("You need to provide a block palette / theme to use!", NamedTextColor.RED));
            return true;
        }

        World world = Objects.requireNonNull(Bukkit.getWorld("world"));
        MatchManager.MATCHES.add(new Match(world, BlockRandomizer.BlockPalette.valueOf(args[0]), world.getPlayers().toArray(Player[]::new)));

        sender.sendMessage(Component.text("Successfully started a match with all players in your current world!", NamedTextColor.GREEN));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return args.length == 1 ? VALID_THEMES : List.of();
    }
}
