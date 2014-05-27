package com.ngusta.cupassist.io;

import com.ngusta.cupassist.activity.MyApplication;
import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.CompetitionPeriod;
import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.domain.PlayerList;
import com.ngusta.cupassist.parser.PlayerListParser;

import android.content.Context;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class PlayerListCache extends Cache<Player> {
    private static final String FILE_NAME = "players";
    private static String CUP_ASSIST_PLAYERS_RANKING_BASE_URL = "http://www.cupassist.com/pa/ranking_beach/index.php";
    private static String CUP_ASSIST_PLAYERS_RANKING_MEN_URL = "http://www.cupassist.com/pa/ranking_beach/visrank.php?k=H";
    private static String CUP_ASSIST_PLAYERS_RANKING_WOMEN_URL = "http://www.cupassist.com/pa/ranking_beach/visrank.php?k=D";
    private static String CUP_ASSIST_PLAYERS_RANKING_MIXED_URL = "http://www.cupassist.com/pa/ranking_beach/visrank.php?k=M";

    private PlayerListParser playerListParser;

    private SourceCodeRequester sourceCodeRequester;

    private PlayerList playerList;

    private Context context;

    public PlayerListCache(Context context) {
        this();
        this.context = context;
    }

    public PlayerListCache() {
        playerListParser = new PlayerListParser();
        sourceCodeRequester = new SourceCodeRequester();
    }

    public Map<Clazz, Set<Player>> getPlayers() {
        if (playerList != null) {
            return playerList.getPlayers();
        }
        try {
            playerList = (PlayerList) load(FILE_NAME, context);
        } catch (RuntimeException e) {
            playerList = null;
        }

        if (downloadPlayers()) {
            try {
                sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_BASE_URL);
                playerList = new PlayerList(CompetitionPeriod.findPeriodByDate(new Date()));
                playerList.getPlayers().put(Clazz.MEN, playerListParser.parsePlayerList(
                        sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_MEN_URL)));
                playerList.getPlayers().put(Clazz.WOMEN, playerListParser.parsePlayerList(
                        sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_WOMEN_URL)));
                playerList.getPlayers().put(Clazz.MIXED, playerListParser.parsePlayerList(
                        sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_MIXED_URL)));
                save(playerList, FILE_NAME, context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return playerList.getPlayers();
    }

    private boolean downloadPlayers() {
        return !MyApplication.CACHE_PLAYERS || playerList == null || !playerList
                .isFromCurrentCompetitionPeriod();
    }
}
