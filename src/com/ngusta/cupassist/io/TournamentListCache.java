package com.ngusta.cupassist.io;

import android.content.Context;
import com.ngusta.cupassist.activity.MyApplication;
import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.domain.Tournament;
import com.ngusta.cupassist.net.AndroidSourceCodeRequester;
import com.ngusta.cupassist.net.DesktopSourceCodeRequester;
import com.ngusta.cupassist.net.SourceCodeRequester;
import com.ngusta.cupassist.parser.TournamentListParser;
import com.ngusta.cupassist.parser.TournamentParser;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class TournamentListCache extends Cache<Tournament> {
    private static final String CUP_ASSIST_TOURNAMENT_LIST_URL = "http://www.cupassist.com/pa/terminliste.php?org=SVBF.SE.SVB";
    private static final String CUP_ASSIST_TOURNAMENT_LIST_2013_URL = "http://www.cupassist.com/pa/terminliste.php?org=SVBF.SE.SVB&p=25";
    private static final String CUP_ASSIST_BASE_URL = "http://www.cupassist.com/";
    private static final String CUP_ASSIST_TOURNAMENT_URL = "http://www.cupassist.com/pamelding/redirect.php?tknavn=";
    private static final String CUP_ASSIST_TOURNAMENT_PLAYERS_URL = "http://www.cupassist.com/pamelding/vis_paamelding.php?order=rp";

    public List<Tournament> tournaments;

    private static final String FILE_NAME = "tournaments";
    private Context context;
    private TournamentListParser tournamentListParser;
    private TournamentParser tournamentParser;
    private SourceCodeRequester sourceCodeRequester;

    public TournamentListCache() {
        tournamentListParser = new TournamentListParser();
        tournamentParser = new TournamentParser();
        sourceCodeRequester = new DesktopSourceCodeRequester();
    }

    public TournamentListCache(Context context) {
        this.context = context;
        tournamentListParser = new TournamentListParser();
        tournamentParser = new TournamentParser();
        sourceCodeRequester = new AndroidSourceCodeRequester();
    }

    public List<Tournament> getTournaments() {
        if (tournaments != null) {
            return tournaments;
        }
        tournaments = MyApplication.CACHE_TOURNAMENTS ? (List<Tournament>) load(FILE_NAME, context) : null;
        if (tournaments == null) {
            try {
                tournaments = tournamentListParser.parseTournamentList(sourceCodeRequester.getSourceCode(CUP_ASSIST_TOURNAMENT_LIST_URL));
                for (Tournament tournament : tournaments) {
                    String source = sourceCodeRequester.getSourceCode(CUP_ASSIST_BASE_URL + "pa/" + tournament.getUrl());
                    tournament.setRedirectUrl(tournamentListParser.parseRedirectUrl(source));
                }
                save(tournaments, FILE_NAME, context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tournaments;
    }

    public void getTeams(Tournament tournament, Set<Player> allPlayers) {
        if (tournament.getRedirectUrl() == null || MyApplication.CACHE_TOURNAMENTS) {
            return;
        }
        try {
            sourceCodeRequester.getSourceCode(CUP_ASSIST_TOURNAMENT_URL + tournament.getRedirectUrl());
            String source = sourceCodeRequester.getSourceCode(CUP_ASSIST_TOURNAMENT_PLAYERS_URL)
                    .replace("style='cursor:pointer'>", "style='cursor:pointer' />")
                    .replace("</b></a></td>", "</a></b></td>")
                    .replace("&nbsp;", "")
                    .replace("onMouseOut='resetItalic(this.id)'>", "onMouseOut='resetItalic(this.id)' />")
                    .replace("<b>Klubb", "<b>Klubb</b>")
                    .replace("<b>Totalt", "<b>Totalt</b>")
                    .replace("<b>Klass", "<b>Klass</b>")
                    .replace("KFUM Gymnastik & IA Karskrona", "KFUM Gymnastik &amp; IA Karskrona");
            tournament.setTeams(tournamentParser.parseTeams(source, allPlayers));
            save(tournaments, FILE_NAME, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
