package com.ngusta.beachvolley.backend.io;

import com.ngusta.beachvolley.backend.parser.PlayerListParser;
import com.ngusta.beachvolley.domain.Clazz;
import com.ngusta.beachvolley.domain.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlayerListCache {

    private static final String CUP_ASSIST_PLAYERS_RANKING_BASE_URL = "http://www.cupassist.com/pa/ranking_beach/index.php";

    private static final String CUP_ASSIST_PLAYERS_RANKING_MEN_URL = "http://www.cupassist.com/pa/ranking_beach/visrank.php?k=H";

    private static final String CUP_ASSIST_PLAYERS_RANKING_WOMEN_URL = "http://www.cupassist.com/pa/ranking_beach/visrank.php?k=D";

    private static final String CUP_ASSIST_PLAYERS_RANKING_MIXED_URL = "http://www.cupassist.com/pa/ranking_beach/visrank.php?k=M";

    private PlayerListParser playerListParser;

    private SourceCodeRequester sourceCodeRequester;

    public PlayerListCache() {
        playerListParser = new PlayerListParser();
        sourceCodeRequester = new SourceCodeRequester();
    }

    public Map<String, Player> getPlayers() {
        Map<String, Player> players = new HashMap<>();
        try {
            sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_BASE_URL);
            Set<Player> men = playerListParser.parsePlayerList(sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_MEN_URL));
            Set<Player> women = playerListParser.parsePlayerList(sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_WOMEN_URL));
            Set<Player> mix = playerListParser.parsePlayerList(sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_MIXED_URL));

            for (Player man : men) {
                if (!players.containsKey(man.uniqueIdentifier())) {
                    man.setClazz(Clazz.MEN);
                    players.put(man.uniqueIdentifier(), man);
                }
            }
            for (Player woman : women) {
                if (!players.containsKey(woman.uniqueIdentifier())) {
                    woman.setClazz(Clazz.WOMEN);
                    players.put(woman.uniqueIdentifier(), woman);
                }
            }

            for (Player mixPlayer : mix) {
                Player player = players.get(mixPlayer.uniqueIdentifier());
                if (player != null) {
                    player.setMixEntryPoints(mixPlayer.getEntryPoints());
                    player.setMixRankingPoints(mixPlayer.getRankingPoints());
                } else {
                    mixPlayer.setClazz(Clazz.MIXED);
                    mixPlayer.setMixEntryPoints(mixPlayer.getEntryPoints());
                    mixPlayer.setMixRankingPoints(mixPlayer.getRankingPoints());
                    mixPlayer.setEntryPoints(0);
                    mixPlayer.setRankingPoints(0);
                    players.put(mixPlayer.uniqueIdentifier(), mixPlayer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return players;
    }
}
