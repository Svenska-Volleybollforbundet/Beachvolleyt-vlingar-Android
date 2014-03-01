package com.ngusta.cupassist.io;

import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.net.DesktopSourceCodeRequester;
import com.ngusta.cupassist.net.SourceCodeRequester;
import com.ngusta.cupassist.parser.PlayerListParser;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerListCache {
    private static String CUP_ASSIST_PLAYERS_RANKING_BASE_URL = "http://www.cupassist.com/pa/ranking_beach/index.php";
    private static String CUP_ASSIST_PLAYERS_RANKING_MEN_URL = "http://www.cupassist.com/pa/ranking_beach/visrank.php?k=H";

    private PlayerListParser playerListParser;
    private SourceCodeRequester sourceCodeRequester;
    private ArrayList<Player> players;

    public PlayerListCache() {
        playerListParser = new PlayerListParser();
        sourceCodeRequester = new DesktopSourceCodeRequester();
    }

    public ArrayList<Player> getPlayers() {
        if (players != null) {
            return players;
        }
        players = loadPlayers();
        if (players == null) {
            try {
                sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_BASE_URL);
                players = playerListParser.parsePlayerList(sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_MEN_URL));
                savePlayers();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return players;
    }

    private void savePlayers() {
        //TODO Implement saving
    }

    private ArrayList<Player> loadPlayers() {
        //TODO Implement loading
        return null;
    }
}
