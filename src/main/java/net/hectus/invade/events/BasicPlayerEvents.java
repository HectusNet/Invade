package net.hectus.invade.events;

import com.marcpg.data.time.Time;
import com.marcpg.util.Randomizer;
import net.hectus.invade.*;
import net.hectus.invade.match.InvadeTicks;
import net.hectus.invade.match.Match;
import net.hectus.invade.match.MatchManager;
import net.hectus.invade.structures.Building;
import net.hectus.invade.tasks.hostile.BountyTask;
import net.hectus.invade.tasks.hostile.HuntingTask;
import net.hectus.lang.Translation;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BasicPlayerEvents implements Listener {
    @EventHandler
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        PlayerData playerData = MatchManager.getPlayerData(event.getPlayer());
        if (playerData != null) {
            Player killer = event.getPlayer().getKiller();
            if (killer != null) {
                PlayerData killerData = MatchManager.getPlayerData(killer);
                if (killerData != null) {
                    if (killerData.currentTask() instanceof BountyTask task) {
                        if (task.target == event.getPlayer()) {
                            killerData.nextTask(true);
                        }
                    } else if (killerData.currentTask() instanceof HuntingTask task) {
                        if (task.addKill()) {
                            killerData.nextTask(true);
                        }
                    }
                    killerData.addKill();
                }
            }

            Bukkit.getScheduler().runTaskLater(Invade.PLUGIN, () -> {
                event.getPlayer().teleport(Randomizer.fromArray(Building.values()).middle().toLocation(event.getPlayer().getWorld()));
                event.getPlayer().setGameMode(GameMode.SPECTATOR);
            }, 10);

            playerData.match.players.remove(event.getPlayer());
            Bukkit.getScheduler().runTaskLater(Invade.PLUGIN, () -> {
                if (playerData.match.players.size() <= 1) {
                    playerData.match.invadeTicks.stop();
                }
            }, 40);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof Player) {
            Match match = MatchManager.getMatchByPlayer(player);
            if (match != null && match.graceTime) {
                event.setCancelled(true);
                player.sendMessage(Translation.component(player.locale(), "match.grace.info", new Time(match.invadeTicks.time.get() - 780).getPreciselyFormatted()));
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (!event.hasItem() || (event.getAction() != Action.RIGHT_CLICK_AIR || event.getAction() != Action.RIGHT_CLICK_AIR)) return;

        if (event.getMaterial() == Material.TOTEM_OF_UNDYING) {
            PlayerData playerData = MatchManager.getPlayerData(event.getPlayer());
            if (playerData != null) {
                Objects.requireNonNull(event.getItem()).setAmount(event.getItem().getAmount() - 1);
                playerData.removePoints(playerData.currentTask().points() / 2);
                playerData.nextTask(false);
                event.getPlayer().sendMessage(Translation.component(event.getPlayer().locale(), "task.skip.success").color(NamedTextColor.YELLOW));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        if (!event.hasChangedOrientation()) return;

        PlayerData playerData = MatchManager.getPlayerData(event.getPlayer());
        if (playerData != null) {
            InvadeTicks.updateCompass(playerData);
        }
    }
}
