package net.hectus.invade.tasks.item;

import net.hectus.Translation;
import net.hectus.invade.Building.Cord;
import net.hectus.invade.matches.Match;
import net.hectus.invade.tasks.Task;
import org.bukkit.entity.Player;

import java.util.Locale;

public class ArtifactTask extends Task { // TODO: Add artifacts with cords and stuff
    public enum Artifact {
        MIDDLE(new Cord(933, 0, 592));

        public final Cord cord;

        Artifact(Cord cord) {
            this.cord = cord;
        }
    }

    public final Artifact artifact;

    public ArtifactTask(Match match, Player player, Artifact artifact) {
        super(match, player);
        this.artifact = artifact;
    }

    @Override
    public int points() {
        return 0;
    }

    @Override
    public String getTranslated(Locale locale) {
        return Translation.string(locale, "task.artifact.info", Arti);
    }
}
