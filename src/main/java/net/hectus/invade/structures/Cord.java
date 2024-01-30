package net.hectus.invade.structures;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public record Cord(@Range(from = 816, to = 1049) int x, @Range(from = 528, to = 655) int z) {
    @Contract(value = "_ -> new", pure = true)
    public @NotNull Location toLocation(@NotNull World world) {
        return new Location(world, x, 24, z);
    }

    @Contract("_ -> new")
    public static @NotNull Cord fromLocation(@NotNull Location location) {
        return new Cord(location.getBlockX(), location.getBlockZ());
    }
}
