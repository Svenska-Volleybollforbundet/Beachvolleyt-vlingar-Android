package com.ngusta.beachvolley.backend.io;

import com.ngusta.beachvolley.domain.NewTeam;
import com.ngusta.beachvolley.domain.Player;
import com.ngusta.beachvolley.domain.Team;
import com.ngusta.beachvolley.domain.Tournament;
import com.ngusta.beachvolley.domain.TournamentList;
import com.ngusta.beachvolley.backend.parser.TournamentListParser;
import com.ngusta.beachvolley.backend.parser.TournamentParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TournamentListCache {

    private static final String CUP_ASSIST_TOURNAMENT_LIST_URL
            = "http://www.cupassist.com/pa/terminliste.php?org=SVBF.SE.SVB&p=40";

    private static final String CUP_ASSIST_BASE_URL = "http://www.cupassist.com/";

    public static final String CUP_ASSIST_TOURNAMENT_URL
            = "http://www.cupassist.com/pamelding/redirect.php?tknavn=";

    private static final String CUP_ASSIST_TOURNAMENT_PLAYERS_URL
            = "http://www.cupassist.com/pamelding/vis_paamelding.php?order=rp";

    private static final String STRING_ONLY_IN_REAL_TOURNAMENT_PAGE = "evenemang";

    public TournamentList tournamentList;

    private static final String FILE_NAME = "tournamentList";

    private TournamentListParser tournamentListParser;

    private TournamentParser tournamentParser;

    private SourceCodeRequester sourceCodeRequester;

    public TournamentListCache() {
        tournamentListParser = new TournamentListParser();
        tournamentParser = new TournamentParser();
        sourceCodeRequester = new SourceCodeRequester();
    }

    public Map<String, Tournament> getTournaments() {
        //TODO add cache?
        Map<String, Tournament> tournamentMap = null;
        try {
            tournamentMap = tournamentListParser.parseTournamentList(
                    sourceCodeRequester.getSourceCode(CUP_ASSIST_TOURNAMENT_LIST_URL));
            List<Tournament> tournaments = new ArrayList(tournamentMap.values());
            Collections.sort(tournaments);
            tournamentList = new TournamentList(tournaments);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tournamentMap;
    }

    public List<Team> getTournamentDetails(Tournament tournament, Map<String, Player> allPlayers) throws IOException {
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
            return tournamentParser.parseTeams(source, allPlayers);
        }
        return null;
    }

    private boolean cookieHasExpired(String source) {
        return !source.contains(STRING_ONLY_IN_REAL_TOURNAMENT_PAGE);
    }
}
