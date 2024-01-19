package net.hectus.invade;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class BlockRandomizer {
    public static final double CONSTANT_TERM = 1.0 / (0.4 * Math.sqrt(2 * Math.PI));
    public static final Random RANDOM = new Random();

    public static long patch(@NotNull Block origin, int radius, @NotNull BlockPalette palette) {
        Location originLoc = origin.getLocation(); // No need to get this for every single block
        double radiusInverse = 1.0 / radius; // Multiplication faster than division

        long start = System.currentTimeMillis();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = origin.getRelative(x, y, z);

                    if (block.isEmpty()) continue;

                    if (RANDOM.nextDouble() < probability(originLoc.distance(block.getLocation()) * radiusInverse)) {
                        block.setType(palette.random(), false);
                    }
                }
            }
        }

        return System.currentTimeMillis() - start;
    }

    private static double probability(double x) {
        return Math.pow(Math.E, -0.5 * Math.pow(x * 2.5, 2)) * CONSTANT_TERM;
    }

    public record BlockPalette(String name, Map<Material, Integer> materials) {
        public static final BlockPalette SCULK = new BlockPalette("sculk", Map.of(
                Material.SCULK, 65,
                Material.BLACK_CONCRETE, 5,
                Material.CRACKED_DEEPSLATE_BRICKS, 15,
                Material.CRACKED_DEEPSLATE_TILES, 15
        ));

        public static final BlockPalette SLIME = new BlockPalette("slime", Map.of(
                Material.SLIME_BLOCK, 90,
                Material.LIME_CONCRETE, 10
        ));

        public static final BlockPalette NETHER = new BlockPalette("nether", Map.of(
                Material.NETHERRACK, 65,
                Material.NETHER_WART_BLOCK, 5,
                Material.NETHER_BRICKS, 15,
                Material.MAGMA_BLOCK, 15
        ));

        public static final BlockPalette OVERWORLD = new BlockPalette("overworld", Map.of(
                Material.GRASS_BLOCK, 50,
                Material.MOSS_BLOCK, 25,
                Material.AZALEA_LEAVES, 10,
                Material.DIRT, 10,
                Material.STONE, 5
        ));

        public static final BlockPalette END = new BlockPalette("end", Map.of(
                Material.END_STONE, 65,
                Material.END_STONE_BRICKS, 15,
                Material.PURPUR_BLOCK, 15,
                Material.OBSIDIAN, 5
        ));

        public static BlockPalette valueOf(@NotNull String name) {
            return switch (name.toLowerCase()) {
                case "sculk" -> SCULK;
                case "slime" -> SLIME;
                case "nether" -> NETHER;
                case "overworld" -> OVERWORLD;
                case "end" -> END;
                default -> throw new IllegalStateException("Unexpected value: " + name.toLowerCase());
            };
        }

        public Material random() {
            AtomicInteger percentage = new AtomicInteger(RANDOM.nextInt(101));
            return materials.entrySet().stream()
                    .filter(entry -> (percentage.addAndGet(-entry.getValue())) <= 0)
                    .findFirst()
                    .map(Map.Entry::getKey)
                    .orElse(Material.AIR);
        }
    }
}
