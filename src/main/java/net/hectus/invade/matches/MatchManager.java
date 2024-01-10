package net.hectus.invade.matches;

import org.bukkit.entity.Player;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.List;

public class MatchManager {

    List<Match> matches = new ArrayList<>();

    public void addPlayerToMatch(Player player){
        if(matches.isEmpty()){
            matches.add(new Match());
        }
        boolean done = false;
        for(Match match : matches){
            if(match.state.equals(Match.State.PRE)){
                match.addPlayer(player);
                done = true;
            }
        }
        if(!done){
            matches.add(new Match(player));
        }
    }

    public Match getMatchByPlayer(Player player){
        for(Match match : matches){
            if(match.players.containsKey(player)){
                return match;
            }
        }
        return null;
    }

    public boolean isInMatch(Player player){
        for(Match match : matches){
            if(match.players.containsKey(player)){
                return true;
            }
        }
        return false;
    }

}
