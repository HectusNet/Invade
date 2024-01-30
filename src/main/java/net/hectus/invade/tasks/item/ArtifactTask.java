package net.hectus.invade.tasks.item;

import net.hectus.invade.structures.Cord;
import net.hectus.invade.PlayerData;
import net.hectus.invade.match.Match;
import net.hectus.invade.tasks.Task;
import net.hectus.lang.Translation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class ArtifactTask extends Task { // TODO: Add artifacts with cords and stuff
    public enum Artifact {
        MIDDLE(new Cord(933, 592));

        public final Cord cord;

        Artifact(Cord cord) {
            this.cord = cord;
        }

        public String getTranslated(Locale locale) {
            return Translation.string(locale, "task.artifact.artifact." + name().toLowerCase());
        }
    }

    public final Artifact artifact;

    public ArtifactTask(Match match, Player player, PlayerData playerData, @NotNull Artifact artifact) {
        super(match, player, playerData);
        this.artifact = artifact;
        this.playerData.mapMarker = artifact.cord;
    }

    @Override
    public int points() {
        return 0;
    }

    @Override
    public String getTranslated(Locale locale) {
        return Translation.string(locale, "task.artifact.info", artifact.getTranslated(locale));
    }
}
