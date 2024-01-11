package net.hectus.invade.events;

import net.hectus.invade.Building;
import net.hectus.invade.PlayerData;
import net.hectus.invade.matches.MatchManager;
import net.hectus.invade.tasks.CheckPointTask;
import net.hectus.invade.tasks.CleaningTask;
import net.hectus.invade.tasks.ItemSearchTask;
import net.hectus.invade.tasks.TransportTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;

public class TaskEvents implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        PlayerData playerData = MatchManager.getPlayerData(event.getPlayer());
        if (playerData == null) return;

        if (playerData.currentTask() instanceof CheckPointTask task) {
            if (isInField(event.getTo(), task.destination.corner1, task.destination.corner2)) {
                playerData.addPoints(task.points());
                playerData.nextTask();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        PlayerData playerData = MatchManager.getPlayerData(event.getPlayer());
        if (playerData != null) {
            if (playerData.currentTask() instanceof CleaningTask task && playerData.match.palette.materials().containsKey(event.getBlock().getType())) {
                if (task.addCleanedItem()) {
                    playerData.addPoints(task.points());
                    playerData.nextTask();
                }
                event.getBlock().setType(Material.GRAY_CONCRETE);
                return;
            }
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ItemFrame itemFrame) {
            if (event.getDamager() instanceof Player player) {
                PlayerData playerData = MatchManager.getPlayerData(player);
                if (playerData != null) {
                    if (playerData.currentTask() instanceof ItemSearchTask task && !task.foundItem && task.item == itemFrame.getItem().getType()) {
                        if (task instanceof TransportTask transportTask) {
                            transportTask.foundItem = true;
                        } else {
                            playerData.addPoints(task.points());
                            playerData.nextTask();
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            PlayerData playerData = MatchManager.getPlayerData(event.getPlayer());
            if (playerData != null) {
                if (playerData.currentTask() instanceof TransportTask task && task.foundItem && isInField(event.getPlayer().getLocation(), task.destination.corner1, task.destination.corner2)) {
                    playerData.addPoints(task.points());
                    playerData.nextTask();
                }
            }
        }
    }

    public static boolean isInField(@NotNull Location loc, Building.@NotNull Cord cord1, Building.@NotNull Cord cord2) {
        int minX = Math.min(cord1.x(), cord2.x());
        int maxX = Math.max(cord1.x(), cord2.x());
        int minZ = Math.min(cord1.z(), cord2.z());
        int maxZ = Math.max(cord1.z(), cord2.z());
        return loc.x() > minX && loc.x() > maxX && loc.z() > minZ && loc.z() > maxZ;
    }
}
