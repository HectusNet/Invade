package net.hectus.invade;

import com.marcpg.util.Randomizer;
import net.hectus.Translation;
import net.hectus.invade.matches.Match;
import net.hectus.invade.tasks.*;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
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
                player.getInventory().remove(Material.valueOf(ArmorLevel.values()[ordinal() - 1].name()));

            player.getInventory().addItem(new ItemStack(Material.valueOf(name())));
        }
    }

    private static final Random RANDOM = new Random();

    public final Player player;
    public final Match match;
    public ArmorLevel armor;
    public WeaponLevel weapon;
    private Task currentTask;
    private int completedTasks = 0;
    private int points = 0;
    private int kills = 0;
    private boolean dead;

    public PlayerData(Player player, Match match) {
        this.player = player;
        this.match = match;
        nextTask(false);
    }

    public void nextTask(boolean completed) {
        if (currentTask != null) {
            currentTask.done();

            if (completed) {
                completeTask();
                addPoints(currentTask.points());

                player.sendMessage(Translation.component(player.locale(), "task.done.complete").color(NamedTextColor.GREEN));

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
            } else {
                player.sendMessage(Translation.component(player.locale(), "task.done.invalid").color(NamedTextColor.RED));
            }
        }

        currentTask = switch (RANDOM.nextInt(5)) {
            case 0 -> new BountyTask(match, player, Randomizer.fromCollection(match.players.entrySet().stream()
                    .filter(targetEntry -> targetEntry.getKey() != player && !targetEntry.getValue().isDead())
                    .map(Map.Entry::getKey)
                    .toList()));
            case 1 -> new CheckPointTask(match, player, Randomizer.fromArray(Building.values()));
            case 2 -> new CleaningTask(match, player, match.palette, RANDOM.nextInt(25, 80));
            case 3 -> new TransportTask(match, player, Randomizer.fromCollection(match.VALID_ITEMS), Randomizer.fromCollection(Building.destinations()));
            default -> new ItemSearchTask(match, player, Randomizer.fromCollection(match.VALID_ITEMS));
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

    public boolean isDead() {
        return dead;
    }

    public void nowDead() {
        dead = true;
    }
}
