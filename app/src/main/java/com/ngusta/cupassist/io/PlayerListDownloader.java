package com.ngusta.cupassist.io;

import com.google.common.collect.HashMultimap;

import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.CompetitionPeriod;
import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.domain.PlayerList;
import com.ngusta.cupassist.domain.PlayerResults;
import com.ngusta.cupassist.parser.PlayerDetailsParser;
import com.ngusta.cupassist.parser.PlayerListParser;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlayerListDownloader {

    private static final String CUP_ASSIST_PLAYERS_RANKING_BASE_URL = "https://www.profixio.com/fx/ranking_beach/index.php";

    private static final String CUP_ASSIST_PLAYERS_RANKING_MEN_URL = "https://www.profixio.com/fx/ranking_beach/visrank.php?k=H";

    private static final String CUP_ASSIST_PLAYERS_RANKING_WOMEN_URL = "https://www.profixio.com/fx/ranking_beach/visrank.php?k=D";

    private static final String CUP_ASSIST_PLAYERS_RANKING_MIXED_URL = "https://www.profixio.com/fx/ranking_beach/visrank.php?k=M";

    private static final String CUP_ASSIST_PLAYER_DETAILS_URL = "https://www.profixio.com/fx/ranking_beach/visrank_detaljer.php";

    private PlayerListParser playerListParser;

    private SourceCodeRequester sourceCodeRequester;

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

            Set<Player> men = playerListParser.parsePlayerList(sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_MEN_URL));
            Set<Player> women = playerListParser.parsePlayerList(sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_WOMEN_URL));
            Set<Player> mix = playerListParser.parsePlayerList(sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_MIXED_URL));

            HashMultimap<String, Player> players = HashMultimap.create();
            for (Player man : men) {
                boolean found = false;
                for (Player p : players.get(man.getNameAndClub())) {
                    if (p.getPlayerId().equals(man.getPlayerId())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    man.setClazz(Clazz.MEN);
                    players.put(man.getNameAndClub(), man);
                }
            }
            for (Player woman : women) {
                boolean found = false;
                for (Player p : players.get(woman.getNameAndClub())) {
                    if (p.getPlayerId().equals(woman.getPlayerId())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    woman.setClazz(Clazz.WOMEN);
                    players.put(woman.getNameAndClub(), woman);
                }
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
                    foundPlayer.setMixRankingPoints(mixPlayer.getRankingPoints());
                } else {
                    mixPlayer.setClazz(Clazz.MIXED);
                    mixPlayer.setMixedEntryPoints(mixPlayer.getEntryPoints());
                    mixPlayer.setMixRankingPoints(mixPlayer.getRankingPoints());
                    mixPlayer.setEntryPoints(0);
                    mixPlayer.setRankingPoints(0);
                    players.put(mixPlayer.getNameAndClub(), mixPlayer);
                }
            }
            ArrayList<Player> sortedPlayers2 = new ArrayList<>(players.values());
            calculateEntryRankInClazz(sortedPlayers2, Clazz.MEN);
            calculateEntryRankInClazz(sortedPlayers2, Clazz.WOMEN);
            calculateEntryRankInMixed(sortedPlayers2);

            playerList.setPlayers(players);
            return playerList.getPlayers();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return HashMultimap.create();
    }

    private void calculateEntryRankInClazz(ArrayList<Player> sortedPlayers, Clazz clazz) {
        Collections.sort(sortedPlayers, new Player.CurrentEntryPlayerComparator(clazz));
        int entryRanking = 1;
        for (Player player : sortedPlayers) {
            if (player.getClazz() == clazz) {
                player.setEntryRank(entryRanking++);
            }
        }
    }

    private void calculateEntryRankInMixed(ArrayList<Player> sortedPlayers) {
        Collections.sort(sortedPlayers, new Player.CurrentEntryPlayerComparator(Clazz.MIXED));
        int entryRanking = 1;
        for (Player player : sortedPlayers) {
            if (player.getMixRankingPoints() > 0) {
                player.setMixedEntryRank(entryRanking++);
            }
        }
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
                playerResults = playerDetailsParser.parseResults(source);
            }
            player.setResults(playerResults);

            data.put("klasse", "M");
            String source = sourceCodeRequester.getSourceCodePost(CUP_ASSIST_PLAYER_DETAILS_URL, data);
            player.setMixedResults(playerDetailsParser.parseResults(source));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void clearPayers() {
        playerList = null;
    }
}
