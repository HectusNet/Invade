package net.hectus.invade.matches;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MatchManager {

    List<Match> matches = new ArrayList<>(); // Set instead of List ? - chimkenu

    public void addMatch(Match match) {
        matches.add(match);
    }

    public List<Match> getMatches() {
        matches.removeIf(match -> match.state.equals(Match.State.END));
        return matches;
    }

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
