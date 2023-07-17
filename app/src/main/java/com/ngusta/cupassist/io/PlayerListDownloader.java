package com.ngusta.cupassist.io;

import com.google.common.collect.HashMultimap;

import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.CompetitionPeriod;
import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.domain.PlayerList;
import com.ngusta.cupassist.domain.PlayerResults;
import com.ngusta.cupassist.parser.PlayerDetailsParser;
import com.ngusta.cupassist.parser.PlayerDetailsParser.ParseMode;
import com.ngusta.cupassist.parser.PlayerListParser;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class PlayerListDownloader {

    private static final String CUP_ASSIST_PLAYERS_RANKING_BASE_URL = "https://www.profixio.com/fx/ranking_beach/index.php";

    private static final String CUP_ASSIST_PLAYER_DETAILS_URL = "https://www.profixio.com/fx/ranking_beach/visrank_detaljer.php";

    private static final String BEACHLIVE_PLAYERS_RANKING_MEN_URL = "https://beachlive.se/api/rank/players?classname=H&noOfPlayers=3000";

    private static final String BEACHLIVE_PLAYERS_RANKING_WOMEN_URL = "https://beachlive.se/api/rank/players?classname=D&noOfPlayers=3000";

    private static final String BEACHLIVE_PLAYERS_RANKING_MIXED_URL = "https://beachlive.se/api/rank/players?classname=M&noOfPlayers=3000";

    private final PlayerListParser playerListParser;

    private final SourceCodeRequester sourceCodeRequester;

    private PlayerList playerList;

    public PlayerListDownloader() {
        playerListParser = new PlayerListParser();
        sourceCodeRequester = new SourceCodeRequester();
    }

    public PlayerListDownloader(Context context) {
        this();
    }

    public HashMultimap<String, Player> getPlayers() {
        if (playerList != null) {
            return playerList.getPlayers();
        }
        try {
            sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_BASE_URL);
            playerList = new PlayerList(CompetitionPeriod.findPeriodByDate(new Date()));

            Set<Player> men = playerListParser.parsePlayerList(requestPlayersFromBeachLive(BEACHLIVE_PLAYERS_RANKING_MEN_URL));
            Set<Player> women = playerListParser.parsePlayerList(requestPlayersFromBeachLive(BEACHLIVE_PLAYERS_RANKING_WOMEN_URL));
            Set<Player> mix = playerListParser.parsePlayerList(requestPlayersFromBeachLive(BEACHLIVE_PLAYERS_RANKING_MIXED_URL));

            HashMultimap<String, Player> players = HashMultimap.create();
            for (Player man : men) {
                players.put(man.getNameAndClub(), man);
            }
            for (Player woman : women) {
                players.put(woman.getNameAndClub(), woman);
            }

            for (Player mixPlayer : mix) {
                Player foundPlayer = null;
                for (Player p : players.get(mixPlayer.getNameAndClub())) {
                    if (p.getPlayerId().equals(mixPlayer.getPlayerId())) {
                        foundPlayer = p;
                        break;
                    }
                }
                if (foundPlayer != null) {
                    foundPlayer.setMixedEntryPoints(mixPlayer.getEntryPoints());
                    foundPlayer.setMixedEntryRank(mixPlayer.getEntryRank());
                } else {
                    mixPlayer.setMixedEntryPoints(mixPlayer.getEntryPoints());
                    mixPlayer.setMixedEntryRank(mixPlayer.getEntryRank());
                    mixPlayer.setEntryPoints(0);
                    mixPlayer.setEntryRank(0);
                    players.put(mixPlayer.getNameAndClub(), mixPlayer);
                }
            }
            playerList.setPlayers(players);
            return playerList.getPlayers();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return HashMultimap.create();
    }

    private InputStreamReader requestPlayersFromBeachLive(String url) {
        try {
            URL beachliveEndpoint = new URL(url);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) beachliveEndpoint.openConnection();
            if (httpsURLConnection.getResponseCode() == 200) {
                InputStream responseBody = httpsURLConnection.getInputStream();
                return new InputStreamReader(responseBody);
            } else {
                throw new IOException("BeachLive seems down");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePlayerWithDetailsAndResults(Player player) {
        try {
            if (player.getClazz() == null) {
                return false;
            }
            Map<String, String> data = new HashMap<>();
            data.put("spid", player.getPlayerId());
            data.put("klasse", player.getClazz().toString().substring(0, 1));
            data.put("rand", "0.5");

            PlayerDetailsParser playerDetailsParser = new PlayerDetailsParser();

            PlayerResults playerResults = new PlayerResults();
            if (!player.isOnlyMixedPlayer()) {
                String source = sourceCodeRequester.getSourceCodePost(CUP_ASSIST_PLAYER_DETAILS_URL, data);
                player.setAge(playerDetailsParser.parseAge(source));
                playerResults = playerDetailsParser.parseResults(source, ParseMode.ALL_RESULTS);
            }
            player.setResults(playerResults);

            data.put("klasse", "M");
            String source = sourceCodeRequester.getSourceCodePost(CUP_ASSIST_PLAYER_DETAILS_URL, data);
            player.setMixedResults(playerDetailsParser.parseResults(source, ParseMode.ONLY_MIXED));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void clearPlayers() {
        playerList = null;
    }
}
