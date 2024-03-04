package net.hectus.invade;

import com.marcpg.lang.Translation;
import com.marcpg.util.Randomizer;
import net.hectus.invade.match.Match;
import net.hectus.invade.structures.Building;
import net.hectus.invade.structures.Cord;
import net.hectus.invade.tasks.Task;
import net.hectus.invade.tasks.hostile.BountyTask;
import net.hectus.invade.tasks.hostile.HuntingTask;
import net.hectus.invade.tasks.hostile.StealTask;
import net.hectus.invade.tasks.hostile.TokenCollectTask;
import net.hectus.invade.tasks.item.ArtifactTask;
import net.hectus.invade.tasks.item.ItemSearchTask;
import net.hectus.invade.tasks.item.TransportTask;
import net.hectus.invade.tasks.movement.CheckPointTask;
import net.hectus.invade.tasks.movement.EscortTask;
import net.hectus.invade.tasks.repair.CleaningTask;
import net.hectus.invade.tasks.repair.RepairTask;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class PlayerData {
    public enum ArmorLevel {
        LEATHER, CHAINMAIL, GOLDEN, IRON, DIAMOND, NETHERITE;

        public void apply(@NotNull Player player) {
            player.getInventory().setHelmet(new ItemStack(Material.valueOf(name() + "_HELMET")));
            player.getInventory().setChestplate(new ItemStack(Material.valueOf(name() + "_CHESTPLATE")));
            player.getInventory().setLeggings(new ItemStack(Material.valueOf(name() + "_LEGGINGS")));
            player.getInventory().setBoots(new ItemStack(Material.valueOf(name() + "_BOOTS")));
        }
    }

    public enum WeaponLevel {
        WOODEN_AXE, WOODEN_SWORD, STONE_AXE, STONE_SWORD, GOLDEN_AXE, GOLDEN_SWORD, IRON_AXE, IRON_SWORD, DIAMOND_AXE, DIAMOND_SWORD, NETHERITE_AXE, NETHERITE_SWORD;

        public void apply(@NotNull Player player) {
            if (this != WOODEN_AXE)
                player.getInventory().remove(Material.valueOf(WeaponLevel.values()[ordinal() - 1].name()));

            player.getInventory().addItem(new ItemStack(Material.valueOf(name())));
        }
    }

    private static final Random RANDOM = new Random();

    public final BossBar compass;
    public final Player player;
    public final Match match;

    public Cord mapMarker;

    private ArmorLevel armor;
    private WeaponLevel weapon;
    private Task currentTask;
    private int completedTasks = 0;
    private int points = 0;
    private int kills = 0;

    public PlayerData(Player player, Match match) {
        this.player = player;
        this.match = match;

        armor = ArmorLevel.LEATHER;
        armor.apply(player);
        weapon = WeaponLevel.WOODEN_AXE;
        weapon.apply(player);

        ItemStack skipItem = new ItemStack(Material.TOTEM_OF_UNDYING, 3);
        skipItem.editMeta(meta -> meta.displayName(Component.text("Skip Task", NamedTextColor.RED)));
        player.getInventory().setItem(8, skipItem);

        compass = BossBar.bossBar(Component.text(""), 1.0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
        compass.addViewer(player);

        nextTask(false);
    }

    public void nextTask(boolean completed) {
        if (currentTask != null) {
            currentTask.done();

            if (completed) {
                addPoints(currentTask.points());
                completeTask();

                player.getInventory().addItem(new ItemStack(Material.BREAD, 3));

                if (Randomizer.boolByChance(66.66)) {
                    if (weapon.ordinal() != 11) {
                        weapon = WeaponLevel.values()[weapon.ordinal() + 1];
                        weapon.apply(player);
                        player.sendMessage(Translation.component(player.locale(), "equipment.weapon.upgrade").color(NamedTextColor.DARK_GREEN));
                    }
                } else {
                    if (armor.ordinal() != 5) {
                        armor = ArmorLevel.values()[armor.ordinal() + 1];
                        armor.apply(player);
                        player.sendMessage(Translation.component(player.locale(), "equipment.armor.upgrade").color(NamedTextColor.DARK_GREEN));
                    }
                }
                player.sendMessage(Translation.component(player.locale(), "task.done.complete").color(NamedTextColor.GREEN));
            } else {
                player.sendMessage(Translation.component(player.locale(), "task.done.invalid").color(NamedTextColor.RED));
            }
        }

        currentTask = switch (RANDOM.nextInt(match.graceTime ? 3 : 0, 22)) {
            case 0, 1 -> new BountyTask(match, player, this, Randomizer.fromCollection(match.players.keySet().stream()
                    .filter(playerData -> playerData != player)
                    .toList()));
            case 2 -> new HuntingTask(match, player, this, RANDOM.nextInt(2, 7));
            case 3, 4, 5 -> new StealTask(match, player, this, Randomizer.fromCollection(match.world.getPlayers().stream().filter(target -> target.getGameMode() == GameMode.SURVIVAL && target != player).toList()));
            case 6, 7, 8 -> new TransportTask(match, player, this, Randomizer.fromCollection(match.VALID_ITEMS), Randomizer.fromCollection(Building.destinations()));
            case 9, 10 -> new CheckPointTask(match, player, this, Randomizer.fromArray(Building.values()));
            case 11 -> new EscortTask(match, player, this, Randomizer.fromArray(Building.values()), Randomizer.fromArray(Building.values()).middle().toLocation(match.world));
            case 12, 13 -> new CleaningTask(match, player, this, RANDOM.nextInt(25, 80));
            case 14 -> new TokenCollectTask(match, player, this, RANDOM.nextInt(10, 21));
            // EXCLUDED
            case 15 -> new ArtifactTask(match, player, this, Randomizer.fromArray(ArtifactTask.Artifact.values()));
            case 16, 17 -> new RepairTask(match, player, this, Randomizer.fromArray(RepairTask.Repairable.values()));
            // EXCLUDED
            default -> new ItemSearchTask(match, player, this, Randomizer.fromCollection(match.VALID_ITEMS));
        };
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    public Task currentTask() {
        return currentTask;
    }

    public void completeTask() {
        completedTasks++;
    }

    public int completedTasks() {
        return completedTasks;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void removePoints(int points) {
        this.points -= points;
    }

    public int points() {
        return points;
    }

    public void addKill() {
        kills++;
    }

    public int kills() {
        return kills;
    }
}
