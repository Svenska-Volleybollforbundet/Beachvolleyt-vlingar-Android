package com.ngusta.cupassist.io;

import com.google.common.collect.HashMultimap;

import com.ngusta.cupassist.activity.MyApplication;
import com.ngusta.cupassist.domain.Match;
import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.domain.Team;
import com.ngusta.cupassist.domain.Tournament;
import com.ngusta.cupassist.domain.TournamentList;
import com.ngusta.cupassist.parser.TournamentListParser;
import com.ngusta.cupassist.parser.TournamentParser;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class TournamentListCache extends Cache<Tournament> {

    private static final String CUP_ASSIST_TOURNAMENT_LIST_URL
            = "https://profixio.com/fx/terminliste.php?org=SVBF.SE.SVB&p=64&vis_gamle_arr=true";

    public static final String CUP_ASSIST_TOURNAMENT_LIST_URL_WITHOUT_OLD_TOURNAMENTS
            = "https://profixio.com/fx/terminliste.php?org=SVBF.SE.SVB&p=64&vis_gamle_arr=false";

    public static final String CUP_ASSIST_BASE_URL = "https://www.profixio.com/";

    public static final String CUP_ASSIST_TOURNAMENT_URL
            = "https://www.profixio.com/app/matches/";

    private static final String CUP_ASSIST_TOURNAMENT_PLAYERS_URL
            = "https://www.profixio.com/pamelding/vis_paamelding.php?order=ep";

    private static final String CUP_ASSIST_TOURNAMENT_INFO_URL = "https://www.profixio.com/fx/vis_innbydelse.php?ib_id=";

    private static final String STRING_ONLY_IN_REAL_TOURNAMENT_PAGE = "evenemang";

    public static final String PROFIXIO_BASE_RESULT_REPORTING_URL = "https://www.profixio.com/app/matches/summer_slam_2021_ch_03/results-login";

    public static final String PROFIXIO_BASE_RESULT_URL = "https://www.profixio.com/app/matches/";

    public static final String PROFIXIO_SIGN_UP_URL = "https://www.profixio.com/pamelding/index.php";

    private static final String PROFIXIO_BASE_REGISTRATION_URL = "https://www.profixio.com/app/matches/";

    private static final String PROFIXIO_ENDING_REGISTRATION_URL = "/teams";

    public static final String COMPETITION_REGULATIONS_URL
            = "https://www.volleyboll.se/globalassets/svenska-volleybollforbundet-svensk-volleyboll/dokumentbank/beachvolley/tb-bv-2021/tavlingsbestammelser-beachvolley-2021_uppdaterat-20210328.pdf";

    public static final String RULES_URL
            = "http://www.fivb.org/EN/BeachVolleyball/Document_Refereeing/FIVB-BeachVolleyball_Rules_2017-2020-EN-v05.pdf";

    private TournamentList tournamentList;

    private static final String FILE_NAME = "tournamentList";

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
        if (tournamentList != null) {
            return tournamentList.getTournaments();
        }
        try {
            tournamentList = (TournamentList) load(FILE_NAME, context);
        } catch (RuntimeException e) {
            tournamentList = null;
        }

        if (downloadTournaments()) {
            try {
                List<Tournament> tournaments = tournamentListParser.parseTournamentList(
                        sourceCodeRequester.getSourceCode(CUP_ASSIST_TOURNAMENT_LIST_URL));
                Collections.sort(tournaments);
                tournamentList = new TournamentList(tournaments);
                save(tournamentList, FILE_NAME, context);
            } catch (IOException e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        }
        return tournamentList.getTournaments();
    }

    private boolean downloadTournaments() {
        return !MyApplication.CACHE_TOURNAMENTS || tournamentList == null || !tournamentList.isValid();
    }

    public void getTournamentDetails(Tournament tournament, HashMultimap<String, Player> allPlayers) throws IOException {
        String source = sourceCodeRequester.getSourceCode(CUP_ASSIST_BASE_URL + "fx/" + tournament.getUrl());
        if (cookieHasExpired(source)) {
            sourceCodeRequester.getSourceCode(CUP_ASSIST_TOURNAMENT_LIST_URL);
            source = sourceCodeRequester.getSourceCode(CUP_ASSIST_BASE_URL + "fx/" + tournament.getUrl());
        }
        tournament.setUrlName(tournamentParser.parseRegistrationUrl(source));
        tournament.setMaxNumberOfTeams(tournamentParser.parseMaxNumberOfTeams(source));

        if (tournament.isRegistrationOpen()) {
            //sourceCodeRequester.getSourceCode(
            //        CUP_ASSIST_TOURNAMENT_URL + tournament.getUrlName());
            //source = sourceCodeRequester.getSourceCode(CUP_ASSIST_TOURNAMENT_PLAYERS_URL);
            boolean newProfixio = isNewProfixio(source);
            if (newProfixio) {
                List<Team> allTeams = new ArrayList<>();
                int page = 1;
                while (allTeams.size() == 30 * (page - 1)) {
                    source = sourceCodeRequester.getSourceCode(PROFIXIO_BASE_REGISTRATION_URL + tournament.getUrlName() + PROFIXIO_ENDING_REGISTRATION_URL + "?page=" + page);
                    allTeams.addAll(tournamentParser.parseTeams(source, allPlayers, newProfixio));
                    page++;
                }
                tournament.setTeams(allTeams);
            }
        }
    }

    private boolean isNewProfixio(String source) {
        return true; // TODO remove or fix : source.contains("pamelding/velgTurnering");
    }

    private boolean cookieHasExpired(String source) {
        return !source.contains(STRING_ONLY_IN_REAL_TOURNAMENT_PAGE);
    }

    public List<Match> getTournamentResult(Tournament tournament) throws IOException {
        List<Match> allMatches = new ArrayList<>();
        List<Match> matches;
        int page = 1;
        do {
            String source = sourceCodeRequester.getSourceCode(getResultsUrl(tournament.getUrlName(), page++));
            matches = tournamentParser.parseMatches(source);
            allMatches.addAll(matches);
        } while (matches.size() > 0);
        return allMatches;
    }

    public static String getReportingUrl(String tournamentUrlName) {
        return CUP_ASSIST_TOURNAMENT_URL + tournamentUrlName + "/results-login";
    }

    public static String getResultsUrl(String tournamentUrlName, int page) {
        return PROFIXIO_BASE_RESULT_URL + tournamentUrlName + "/latest?page=" + page;
    }
}
