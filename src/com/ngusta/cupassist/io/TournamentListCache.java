package com.ngusta.cupassist.io;

import android.content.Context;
import com.ngusta.cupassist.activity.MyApplication;
import com.ngusta.cupassist.domain.Tournament;
import com.ngusta.cupassist.net.AndroidSourceCodeRequester;
import com.ngusta.cupassist.net.DesktopSourceCodeRequester;
import com.ngusta.cupassist.net.SourceCodeRequester;
import com.ngusta.cupassist.parser.TournamentListParser;
import com.ngusta.cupassist.parser.TournamentParser;

import java.io.*;
import java.util.ArrayList;

public class TournamentListCache {
    private static final String CUP_ASSIST_TOURNAMENT_LIST_URL = "http://www.cupassist.com/pa/terminliste.php?org=SVBF.SE.SVB";
    private static final String CUP_ASSIST_TOURNAMENT_LIST_2013_URL = "http://www.cupassist.com/pa/terminliste.php?org=SVBF.SE.SVB&p=25";
    private static final String CUP_ASSIST_BASE_URL = "http://www.cupassist.com/";
    private static final String CUP_ASSIST_TOURNAMENT_URL = "http://www.cupassist.com/pamelding/redirect.php?tknavn=";
    private static final String CUP_ASSIST_TOURNAMENT_PLAYERS_URL = "http://www.cupassist.com/pamelding/vis_paamelding.php?order=rp";

    public ArrayList<Tournament> tournaments;

    private static final String FILE_NAME = "cupassist_tournaments";
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

    public ArrayList<Tournament> getTournaments() {
        if (tournaments != null) {
            return tournaments;
        }
        tournaments = MyApplication.USE_CACHE_DATA ? loadTournaments() : null;
        if (tournaments == null) {
            try {
                tournaments = tournamentListParser.parseTournamentList(sourceCodeRequester.getSourceCode(CUP_ASSIST_TOURNAMENT_LIST_URL));
                for (Tournament tournament : tournaments) {
                    String source = sourceCodeRequester.getSourceCode(CUP_ASSIST_BASE_URL + "pa/" + tournament.getUrl());
                    tournament.setRedirectUrl(tournamentListParser.parseRedirectUrl(source));
                }
                saveTournaments();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tournaments;
    }

    private ArrayList<Tournament> loadTournaments() {
        try {
            FileInputStream fileInputStream = null;
            if (MyApplication.RUN_AS_ANDROID_APP) {
                File file = new File(context.getFilesDir(), FILE_NAME + ".data");
                fileInputStream = new FileInputStream(file);
            } else {
                fileInputStream = new FileInputStream(FILE_NAME + ".data");
            }
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            return (ArrayList<Tournament>) objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveTournaments() throws IOException {
        FileOutputStream fileOutputStream;
        if (MyApplication.RUN_AS_ANDROID_APP) {
            File file = new File(context.getFilesDir(), FILE_NAME + ".data");
            fileOutputStream = new FileOutputStream(file);
        } else {
            fileOutputStream = new FileOutputStream(FILE_NAME + ".data");
        }
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(tournaments);
    }

    public void loadTeams(Tournament tournament) {
        if (tournament.getRedirectUrl() == null) {
            return;
        }
        try {
            new DesktopSourceCodeRequester().getSourceCode(CUP_ASSIST_TOURNAMENT_URL + tournament.getRedirectUrl());
            String source = new DesktopSourceCodeRequester().getSourceCode(CUP_ASSIST_TOURNAMENT_PLAYERS_URL)
                    .replace("style='cursor:pointer'>", "style='cursor:pointer' />")
                    .replace("</b></a></td>", "</a></b></td>")
                    .replace("&nbsp;", "")
                    .replace("onMouseOut='resetItalic(this.id)'>", "onMouseOut='resetItalic(this.id)' />")
                    .replace("<b>Klubb", "<b>Klubb</b>")
                    .replace("<b>Totalt", "<b>Totalt</b>")
                    .replace("<b>Klass", "<b>Klass</b>")
                    .replace("KFUM Gymnastik & IA Karskrona", "KFUM Gymnastik &amp; IA Karskrona");
            tournament.setTeams(tournamentParser.parseTeams(source));
            saveTournaments();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SourceCodeRequester getSourceCodeRequester() {
        return sourceCodeRequester;
    }
}
