package net.hectus.invade.commands;

import net.hectus.invade.Invade;
import net.hectus.invade.matches.Match;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SlashStart implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        World world = Objects.requireNonNull(Bukkit.getWorld("world"));
        Invade.getMatchManager().addMatch(new Match(world.getPlayers()));
        sender.sendMessage(Component.text("Successfully started a match with all players in your current world!", NamedTextColor.GREEN));
        return true;
    }
}
