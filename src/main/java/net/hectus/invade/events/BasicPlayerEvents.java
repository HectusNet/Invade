package net.hectus.invade.events;

import net.hectus.invade.PlayerData;
import net.hectus.invade.matches.MatchManager;
import net.hectus.invade.tasks.BountyTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

public class BasicPlayerEvents implements Listener {
    @EventHandler
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        PlayerData playerData = MatchManager.getPlayerData(event.getPlayer());
        if (playerData != null) {
            playerData.nowDead();

            Player killer = event.getPlayer().getKiller();
            if (killer != null) {
                PlayerData killerData = MatchManager.getPlayerData(killer);
                if (killerData != null) {
                    if (killerData.currentTask() instanceof BountyTask task) {
                        if (task.target == event.getPlayer()) {
                            killerData.nextTask(true);
                        }
                    }
                    killerData.addKill();
                }
            }
        }
    }
}
