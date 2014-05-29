package com.ngusta.cupassist.io;

import com.ngusta.cupassist.activity.MyApplication;
import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.domain.Tournament;
import com.ngusta.cupassist.parser.TournamentListParser;
import com.ngusta.cupassist.parser.TournamentParser;

import android.content.Context;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TournamentListCache extends Cache<Tournament> {

    private static final String CUP_ASSIST_TOURNAMENT_LIST_URL
            = "http://www.cupassist.com/pa/terminliste.php?org=SVBF.SE.SVB";

    private static final String CUP_ASSIST_TOURNAMENT_LIST_2013_URL
            = "http://www.cupassist.com/pa/terminliste.php?org=SVBF.SE.SVB&p=25";

    private static final String CUP_ASSIST_BASE_URL = "http://www.cupassist.com/";

    public static final String CUP_ASSIST_TOURNAMENT_URL
            = "http://www.cupassist.com/pamelding/redirect.php?tknavn=";

    private static final String CUP_ASSIST_TOURNAMENT_PLAYERS_URL
            = "http://www.cupassist.com/pamelding/vis_paamelding.php?order=rp";

    public List<Tournament> tournaments;

    private static final String FILE_NAME = "tournaments";

    private Context context;

    private TournamentListParser tournamentListParser;

    private TournamentParser tournamentParser;

    private SourceCodeRequester sourceCodeRequester;

    public TournamentListCache(Context context) {
        this();
        this.context = context;
    }

    public TournamentListCache() {
        tournamentListParser = new TournamentListParser();
        tournamentParser = new TournamentParser();
        sourceCodeRequester = new SourceCodeRequester();
    }

    public List<Tournament> getTournaments() {
        if (tournaments != null) {
            return tournaments;
        }
        tournaments = MyApplication.CACHE_TOURNAMENTS ? (List<Tournament>) load(FILE_NAME, context)
                : null;
        if (tournaments == null) {
            try {
                tournaments = tournamentListParser.parseTournamentList(
                        sourceCodeRequester.getSourceCode(CUP_ASSIST_TOURNAMENT_LIST_URL));
                save(tournaments, FILE_NAME, context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tournaments;
    }

    public void getTournamentDetails(Tournament tournament, Map<Clazz, Set<Player>> allPlayers) {
        if (tournament.getTeams() != null && MyApplication.CACHE_TOURNAMENTS) {
            return;
        }
        try {
            String source = sourceCodeRequester
                    .getSourceCode(CUP_ASSIST_BASE_URL + "pa/" + tournament.getUrl());
            tournament.setRegistrationUrl(tournamentParser.parseRegistrationUrl(source));
            tournament.setMaxNumberOfTeams(tournamentParser.parseMaxNumberOfTeams(source));

            if (tournament.getRegistrationUrl() == null) {
                throw new IllegalArgumentException(
                        "Missing registration URL for tournament: " + tournament);
            }
            sourceCodeRequester
                    .getSourceCode(CUP_ASSIST_TOURNAMENT_URL + tournament.getRegistrationUrl());
            source = sourceCodeRequester.getSourceCode(CUP_ASSIST_TOURNAMENT_PLAYERS_URL);
            tournament.setTeams(tournamentParser.parseTeams(source, allPlayers));
            save(tournaments, FILE_NAME, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
