package net.hectus.invade.match;

import com.marcpg.libpg.data.time.Time;
import com.marcpg.libpg.lang.Translation;
import net.hectus.invade.Invade;
import net.hectus.invade.PlayerData;
import net.hectus.invade.game_events.ChaosEvent;
import net.hectus.invade.game_events.Event;
import net.hectus.invade.structures.BlockRandomizer;
import net.hectus.invade.structures.Building;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.postgresql.util.PGInterval;

import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static net.hectus.invade.Invade.DATABASE;

public class Match {
    public static final Random RANDOM = new Random();

    public final Set<Material> VALID_ITEMS;
    public final HashMap<Player, PlayerData> players = new HashMap<>();
    public final InvadeTicks invadeTicks = new InvadeTicks(this, new Time(10, Time.Unit.MINUTES));
    public final World world;
    public final BlockRandomizer.BlockPalette palette;
    public Instant startingTime;
    public boolean graceTime = true;
    public Event currentEvent;

    public Match(@NotNull World world, BlockRandomizer.BlockPalette palette, Player @NotNull ... players) {
        this.world = world;
        this.palette = palette;

        // Not the place to use a stream in, but I love streams, so I still did lol
        VALID_ITEMS = world.getEntities().parallelStream()
                .filter(e -> e instanceof ItemFrame)
                .map(e -> ((ItemFrame) e).getItem().getType())
                .collect(Collectors.toSet());

        for (Player player : players) {
            this.players.put(player, new PlayerData(player, this));
        }

        generateFeatures(palette, new Location(world, 816, 22, 528), new Location(world, 1049, 42, 655));
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
                        player.showTitle(Title.title(Translation.component(player.locale(), "match.starting.start", timer), Translation.component(player.locale(), "match.starting.start-sub")));
                    }

                    List<Building> buildings = new ArrayList<>(List.of(Building.values()));
                    Collections.shuffle(buildings);
                    buildings.subList(0, 12).forEach(building -> spawnMobs(building.middle().toLocation(world)));

                    invadeTicks.start();
                    cancel();
                }
                for (Player player : players.keySet()) {
                    player.showTitle(Title.title(Translation.component(player.locale(), "match.starting.timer", timer), Translation.component(player.locale(), "match.starting.timer-sub")));
                }
                timer--;
            }
        }.runTaskTimer(Invade.PLUGIN, 0, 20);
    }

    public void stop(Player... winners) throws SQLException {
        for (Player player : players.keySet()) {
            player.showTitle(Title.title(Translation.component(player.locale(), "match.ending.title"), Translation.component(player.locale(), "match.ending.time")));
            player.activeBossBars().forEach(player::hideBossBar);

            UUID uuid = player.getUniqueId();
            if (!DATABASE.contains(uuid)) {
                DATABASE.add(Map.of("uuid", uuid, "name", player.getName()));
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

        MatchManager.MATCHES.remove(this);
    }

    public void event() {
        currentEvent = switch (RANDOM.nextInt(graceTime ? 1 : 0, 2)) {
            default -> new ChaosEvent(this, RANDOM.nextInt(50, 71));
        };
        currentEvent.run();
        currentEvent.startTitle(players.keySet());

        Time duration = currentEvent.getDuration();
        if (duration != null)
            Bukkit.getScheduler().runTaskLater(Invade.PLUGIN, () -> currentEvent.done(), duration.get() * 20);
    }

    public void generateFeatures(@NotNull BlockRandomizer.BlockPalette blockPalette, @NotNull Location c1, @NotNull Location c2) {
        c1.getWorld().getPlayers().forEach(player -> player.showTitle(Title.title(Translation.component(player.locale(), "match.starting.gen"), Translation.component(player.locale(), "match.starting.gen-sub"))));
        for (int i = 0; i < RANDOM.nextInt(22, 36); i++) {
            Block targetBlock;
            do {
                targetBlock = new Location(
                        c1.getWorld(),
                        RANDOM.nextDouble(c1.x(), c2.x()),
                        RANDOM.nextDouble(c1.y(), c2.y()),
                        RANDOM.nextDouble(c1.z(), c2.z())
                ).getBlock();
            } while (targetBlock.isEmpty());
            BlockRandomizer.patch(targetBlock, RANDOM.nextInt(14, 29), blockPalette);
        }
    }

    public void spawnMobs(Location location) {
        switch (palette.name()) {
            case "sculk" -> {
                world.spawn(location, Warden.class);
                world.spawn(location, Bat.class);
            }
            case "slime" -> {
                world.spawn(location, Slime.class);
                world.spawn(location, Slime.class);
                world.spawn(location, Slime.class);
                world.spawn(location, Slime.class);
            }
            case "nether" -> {
                world.spawn(location, PigZombie.class);
                world.spawn(location, WitherSkeleton.class);
                world.spawn(location, MagmaCube.class);
                world.spawn(location, Blaze.class);
            }
            case "overworld" -> {
                world.spawn(location, Skeleton.class);
                world.spawn(location, Zombie.class);
                world.spawn(location, Spider.class);
                world.spawn(location, Vindicator.class);
            }
            case "end" -> {
                world.spawn(location, Enderman.class);
                world.spawn(location, Enderman.class);
                world.spawn(location, Enderman.class);
                world.spawn(location, Silverfish.class);
            }
        }
    }
}
