package net.hectus.invade.events;

import net.hectus.invade.Cord;
import net.hectus.invade.Invade;
import net.hectus.invade.InvadeTicks;
import net.hectus.invade.PlayerData;
import net.hectus.invade.matches.MatchManager;
import net.hectus.invade.tasks.hostile.StealTask;
import net.hectus.invade.tasks.item.ItemSearchTask;
import net.hectus.invade.tasks.item.TransportTask;
import net.hectus.invade.tasks.movement.CheckPointTask;
import net.hectus.invade.tasks.movement.EscortTask;
import net.hectus.invade.tasks.repair.CleaningTask;
import net.hectus.invade.tasks.hostile.TokenCollectTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;

public class TaskEvents implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        PlayerData playerData = MatchManager.getPlayerData(event.getPlayer());
        if (playerData == null) return;

        if (playerData.currentTask() instanceof CheckPointTask task && isInField(event.getTo(), task.destination.corner1, task.destination.corner2)) {
            playerData.nextTask(true);
        } else if (playerData.currentTask() instanceof TransportTask task && task.foundItem && isInField(event.getTo(), task.destination.corner1, task.destination.corner2)) {
            InvadeTicks.updateActionBar(playerData);
        } else if (playerData.currentTask() instanceof EscortTask task && isInField(event.getTo(), task.destination.corner1, task.destination.corner2) && task.villager.getLocation().distance(event.getTo()) < 10) {
            playerData.nextTask(true);
        }
    }

    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        event.setDropItems(false);
        PlayerData playerData = MatchManager.getPlayerData(event.getPlayer());
        if (playerData != null) {
            if (playerData.currentTask() instanceof CleaningTask task && playerData.match.palette.materials().containsKey(event.getBlock().getType())) {
                Location loc = event.getBlock().getLocation().clone();
                if (task.addCleanedBlock()) {
                    playerData.nextTask(true);
                } else {
                    InvadeTicks.updateActionBar(playerData);
                }

                Bukkit.getScheduler().runTaskLater(Invade.PLUGIN, () -> loc.getBlock().setType(Material.GRAY_CONCRETE), 2);

                return;
            }
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ItemFrame itemFrame) {
            event.setCancelled(true);
            if (event.getDamager() instanceof Player player) {
                PlayerData playerData = MatchManager.getPlayerData(player);
                if (playerData != null) {
                    if (playerData.currentTask() instanceof ItemSearchTask task && task.item == itemFrame.getItem().getType()) {
                        if (task instanceof TransportTask transportTask) {
                            transportTask.foundItem = true;
                        } else {
                            playerData.nextTask(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(@NotNull PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            PlayerData playerData = MatchManager.getPlayerData(event.getPlayer());
            if (playerData != null) {
                if (playerData.currentTask() instanceof TransportTask task && task.foundItem && isInField(event.getPlayer().getLocation(), task.destination.corner1, task.destination.corner2)) {
                    playerData.nextTask(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerKillEntity(@NotNull EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        event.getDrops().clear();
        if (player != null) {
            PlayerData playerData = MatchManager.getPlayerData(player);
            if (playerData != null && playerData.currentTask() instanceof TokenCollectTask task) {
                if (task.addTokens(switch (event.getEntity().getType()) {
                    case WARDEN -> 20;
                    case VINDICATOR, ENDERMAN -> 10;
                    case WITHER_SKELETON -> 7;
                    case ZOMBIFIED_PIGLIN, BLAZE, SKELETON -> 5;
                    case SPIDER, ZOMBIE -> 3;
                    default -> 1;
                })) {
                    playerData.addPoints(task.points());
                    playerData.nextTask(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(@NotNull PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player target) {
            PlayerData playerData = MatchManager.getPlayerData(event.getPlayer());
            if (playerData != null && playerData.currentTask() instanceof StealTask task) {
                if (task.target == target) {
                    playerData.nextTask(true);
                    target.playSound(target, Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 1.0f);
                    playerData.player.playSound(playerData.player, Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 1.0f);
                } else {
                    playerData.player.sendMessage(Component.text("That's the wrong player! You need to steal from " + task.target.getName() + " and not " + target.getName() + ".", NamedTextColor.RED));
                }
            }
        }
    }

    public static boolean isInField(@NotNull Location loc, @NotNull Cord cord1, @NotNull Cord cord2) {
        int minX = Math.min(cord1.x(), cord2.x());
        int maxX = Math.max(cord1.x(), cord2.x());
        int minZ = Math.min(cord1.z(), cord2.z());
        int maxZ = Math.max(cord1.z(), cord2.z());
        return loc.x() >= minX && loc.x() <= maxX && loc.z() >= minZ && loc.z() <= maxZ;
    }
}
