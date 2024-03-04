package net.hectus.invade.tasks.hostile;

import com.marcpg.lang.Translation;
import net.hectus.invade.PlayerData;
import net.hectus.invade.match.Match;
import net.hectus.invade.tasks.Task;
import org.bukkit.entity.Player;

import java.util.Locale;

public class TokenCollectTask extends Task {
    public final int tokenRequirement;
    public int tokens;

    public TokenCollectTask(Match match, Player player, PlayerData playerData, int tokenRequirement) {
        super(match, player, playerData);
        this.tokenRequirement = tokenRequirement;
    }

    public boolean addTokens(int tokens) {
        return (this.tokens += tokens) >= tokenRequirement;
    }

    @Override
    public int points() {
        return 10;
    }

    @Override
    public String getTranslated(Locale locale) {
        return Translation.string(locale, "task.token_collect.info", tokenRequirement);
    }
}
