package net.hectus.invade.game_events;

import com.marcpg.libpg.data.time.Time;
import com.marcpg.libpg.lang.Translatable;
import com.marcpg.libpg.lang.Translation;
import net.hectus.invade.match.Match;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class Event implements Translatable {
    public final Match match;

    public Event(Match match) {
        this.match = match;
    }

    public void startTitle(@NotNull Set<Player> players) {
        for (Player player : players) {
            player.showTitle(Title.title(Component.text(getTranslated(player.locale()) + "!", NamedTextColor.GREEN), Component.text(getSecondaryTranslated(player.locale()), NamedTextColor.GRAY)));
        }
    }

    public void endTitle(@NotNull Set<Player> players) {
        for (Player player : players) {
            player.showTitle(Title.title(Component.text(getTranslated(player.locale()), NamedTextColor.GOLD), Translation.component(player.locale(), "event.ending").color(NamedTextColor.RED)));
        }
    }

    public void done() {
        match.currentEvent.endTitle(match.players.keySet());
        match.currentEvent = null;
    }

    public abstract void run();

    public abstract @Nullable Time getDuration();
}
