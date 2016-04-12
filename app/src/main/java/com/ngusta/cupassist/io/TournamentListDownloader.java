package com.ngusta.cupassist.io;

import com.ngusta.cupassist.activity.MyApplication;
import com.ngusta.beachvolley.domain.Player;
import com.ngusta.beachvolley.domain.Tournament;
import com.ngusta.beachvolley.domain.TournamentList;
import com.ngusta.cupassist.parser.TournamentListParser;
import com.ngusta.cupassist.parser.TournamentParser;

import android.content.Context;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TournamentListDownloader {

    private static final String CUP_ASSIST_TOURNAMENT_LIST_URL
            = "http://www.cupassist.com/pa/terminliste.php?org=SVBF.SE.SVB&p=40";

    private static final String CUP_ASSIST_BASE_URL = "http://www.cupassist.com/";

    public static final String CUP_ASSIST_TOURNAMENT_URL
            = "http://www.cupassist.com/pamelding/redirect.php?tknavn=";

    private static final String CUP_ASSIST_TOURNAMENT_PLAYERS_URL
            = "http://www.cupassist.com/pamelding/vis_paamelding.php?order=rp";

    private static final String STRING_ONLY_IN_REAL_TOURNAMENT_PAGE = "evenemang";

    public TournamentList tournamentList;

    private TournamentListParser tournamentListParser;

    private TournamentParser tournamentParser;

    private SourceCodeRequester sourceCodeRequester;

    public TournamentListDownloader() {
        tournamentListParser = new TournamentListParser();
        tournamentParser = new TournamentParser();
        sourceCodeRequester = new SourceCodeRequester();
    }

    public List<Tournament> getTournaments() {
        if (tournamentList != null) {
            return tournamentList.getTournaments();
        }
        try {
            List<Tournament> tournaments = tournamentListParser.parseTournamentList(
                    sourceCodeRequester.getSourceCode(CUP_ASSIST_TOURNAMENT_LIST_URL));
            Collections.sort(tournaments);
            tournamentList = new TournamentList(tournaments);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tournamentList.getTournaments();
    }

    public void getTournamentDetails(Tournament tournament, Map<String, Player> allPlayers) throws IOException {
        String source = sourceCodeRequester.getSourceCode(CUP_ASSIST_BASE_URL + "pa/" + tournament.getUrl());
        if (cookieHasExpired(source)) {
            sourceCodeRequester.getSourceCode(CUP_ASSIST_TOURNAMENT_LIST_URL);
            source = sourceCodeRequester.getSourceCode(CUP_ASSIST_BASE_URL + "pa/" + tournament.getUrl());
        }
        tournament.setRegistrationUrl(tournamentParser.parseRegistrationUrl(source));
        tournament.setMaxNumberOfTeams(tournamentParser.parseMaxNumberOfTeams(source));

        if (tournament.isRegistrationOpen()) {
            sourceCodeRequester.getSourceCode(
                    CUP_ASSIST_TOURNAMENT_URL + tournament.getRegistrationUrl());
            source = sourceCodeRequester.getSourceCode(CUP_ASSIST_TOURNAMENT_PLAYERS_URL);
            tournament.setTeams(tournamentParser.parseTeams(source, allPlayers));
        }
    }

    private boolean cookieHasExpired(String source) {
        return !source.contains(STRING_ONLY_IN_REAL_TOURNAMENT_PAGE);
    }
}
