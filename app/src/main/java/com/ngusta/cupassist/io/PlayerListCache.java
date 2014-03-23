package com.ngusta.cupassist.io;

import android.content.Context;
import com.ngusta.cupassist.activity.MyApplication;
import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.net.AndroidSourceCodeRequester;
import com.ngusta.cupassist.net.DesktopSourceCodeRequester;
import com.ngusta.cupassist.net.SourceCodeRequester;
import com.ngusta.cupassist.parser.PlayerListParser;

import java.io.IOException;
import java.util.Set;

public class PlayerListCache extends Cache<Player> {
    private static final String FILE_NAME = "players";
    private static String CUP_ASSIST_PLAYERS_RANKING_BASE_URL = "http://www.cupassist.com/pa/ranking_beach/index.php";
    private static String CUP_ASSIST_PLAYERS_RANKING_MEN_URL = "http://www.cupassist.com/pa/ranking_beach/visrank.php?k=H";
    private static String CUP_ASSIST_PLAYERS_RANKING_WOMEN_URL = "http://www.cupassist.com/pa/ranking_beach/visrank.php?k=D";
    private static String CUP_ASSIST_PLAYERS_RANKING_MIXED_URL = "http://www.cupassist.com/pa/ranking_beach/visrank.php?k=M";

    private PlayerListParser playerListParser;
    private SourceCodeRequester sourceCodeRequester;
    private Set<Player> players;
    private Context context;

    public PlayerListCache(Context context) {
        this.context = context;
        playerListParser = new PlayerListParser();
        sourceCodeRequester = new AndroidSourceCodeRequester();
    }

    public PlayerListCache() {
        playerListParser = new PlayerListParser();
        sourceCodeRequester = new DesktopSourceCodeRequester();
    }

    public Set<Player> getPlayers() {
        if (players != null) {
            return players;
        }
        players = MyApplication.CACHE_PLAYERS ? (Set<Player>) load(FILE_NAME, context) : null;
        if (players == null) {
            try {
                sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_BASE_URL);
                players = playerListParser.parsePlayerList(sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_MEN_URL));
                players.addAll(playerListParser.parsePlayerList(sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_WOMEN_URL)));
                save(players, FILE_NAME, context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return players;
    }
}
