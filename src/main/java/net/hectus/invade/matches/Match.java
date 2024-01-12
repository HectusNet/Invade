package net.hectus.invade.matches;

import com.marcpg.data.time.Time;
import net.hectus.Translation;
import net.hectus.invade.BlockRandomizer;
import net.hectus.invade.Invade;
import net.hectus.invade.InvadeTicks;
import net.hectus.invade.PlayerData;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.postgresql.util.PGInterval;

import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static net.hectus.invade.Invade.DATABASE;

public class Match {
    public enum State { PRE, IN, END }

    public static final Random RANDOM = new Random();

    public final Set<Material> VALID_ITEMS;
    public final HashMap<Player, PlayerData> players = new HashMap<>();
    public final InvadeTicks invadeTicks = new InvadeTicks(this, new Time(15, Time.Unit.MINUTES));
    public final World world;
    public final BlockRandomizer.BlockPalette palette;
    public State state = State.PRE;
    public Instant startingTime;

    public Match(@NotNull World world, BlockRandomizer.BlockPalette palette, Player @NotNull ... players) {
        this.world = world;
        this.palette = palette;

        // Not the place to use a stream in, but I love streams, so I still did lol
        VALID_ITEMS = world.getEntities().parallelStream()
                .filter(ItemFrame.class::isInstance)
                .map(ItemFrame.class::cast)
                .map(ItemFrame::getItem)
                .map(ItemStack::getType)
                .collect(Collectors.toSet());

        for (Player player : players) {
            this.players.put(player, new PlayerData(player, this));
        }

        List<Integer> corner1 = Invade.CONFIG.getIntegerList("corner1");
        List<Integer> corner2 = Invade.CONFIG.getIntegerList("corner2");
        generateFeatures(palette, new Location(world, corner1.get(0), corner1.get(1), corner1.get(2)), new Location(world, corner2.get(0), corner2.get(1), corner2.get(2)));
        start();
    }

    public void start() {
        startingTime = Instant.now();
        new BukkitRunnable() {
            int timer = 5;
            @Override
            public void run() {
                if (timer == 0) {
                    for (Player player : players.keySet()) {
                        player.showTitle(Title.title(Translation.component(player.locale(), "match.start.title.start", timer), Translation.component(player.locale(), "match.start.title.start.subtitle")));
                    }
                    state = State.IN;
                    invadeTicks.start();
                    cancel();
                }
                for (Player player : players.keySet()) {
                    player.showTitle(Title.title(Translation.component(player.locale(), "match.start.title.timer", timer), Translation.component(player.locale(), "match.start.title.timer.subtitle")));
                }
                timer--;
            }
        }.runTaskTimer(Invade.getPlugin(Invade.class), 0, 20);
    }

    public void stop(Player... winners) throws SQLException {
        state = State.END;
        for (Player player : players.keySet()) {
            player.showTitle(Title.title(Translation.component(player.locale(), "match.end.title"), Translation.component(player.locale(), "match.end.time.subtitle")));

            UUID uuid = player.getUniqueId();
            if (!DATABASE.contains(uuid)) {
                DATABASE.add(uuid, player.getName());
            }
            DATABASE.set(uuid, "matches", (int) DATABASE.get(uuid, "matches") + 1);
            if (List.of(winners).contains(player)) {
                DATABASE.set(uuid, "wins", (int) DATABASE.get(uuid, "wins") + 1);
            } else if (winners.length == 0) {
                DATABASE.set(uuid, "ties", (int) DATABASE.get(uuid, "ties") + 1);
            } else {
                DATABASE.set(uuid, "loses", (int) DATABASE.get(uuid, "loses") + 1);
            }
            DATABASE.set(uuid, "playtime", new PGInterval(0, 0, 0, 0, 0, ((PGInterval) DATABASE.get(uuid, "playtime")).getWholeSeconds() + (Instant.now().getEpochSecond() - startingTime.getEpochSecond())));
        }
    }

    public void generateFeatures(@NotNull BlockRandomizer.BlockPalette blockPalette, @NotNull Location c1, @NotNull Location c2) {
        c1.getWorld().getPlayers().forEach(player -> player.showTitle(Title.title(Translation.component(player.locale(), "match.start.generation"), Translation.component(player.locale(), "match.start.generation.subtitle"))));
        for (int i = 0; i < RANDOM.nextInt(15, 30); i++) {
            Block targetBlock;
            do {
                targetBlock = new Location(
                        c1.getWorld(),
                        RANDOM.nextDouble(Math.min(c1.x(), c2.x()), Math.max(c1.x(), c2.x())),
                        RANDOM.nextDouble(Math.min(c1.y(), c2.y()), Math.max(c1.y(), c2.y())),
                        RANDOM.nextDouble(Math.min(c1.z(), c2.z()), Math.max(c1.z(), c2.z()))
                ).getBlock();
            } while (targetBlock.isEmpty());
            BlockRandomizer.patch(targetBlock, RANDOM.nextInt(8, 26), blockPalette);
        }
    }
}
