package net.hectus.invade.matches;

import net.hectus.invade.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MatchManager {
    public static final List<Match> MATCHES = new ArrayList<>();

    public static @Nullable Match getMatchByPlayer(Player player) {
        return MATCHES.stream()
                .filter(match -> match.players.containsKey(player))
                .findFirst()
                .orElse(null);
    }

    public static @Nullable PlayerData getPlayerData(Player player) {
        Match match = getMatchByPlayer(player);
        return match != null ? match.players.get(player) : null;
    }
}
