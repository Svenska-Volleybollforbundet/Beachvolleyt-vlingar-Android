package com.ngusta.cupassist.activity;

import com.firebase.client.Firebase;
import com.ngusta.beachvolley.domain.Player;
import com.ngusta.beachvolley.domain.Team;
import com.ngusta.beachvolley.domain.Tournament;
import com.ngusta.cupassist.io.PlayerListDownloader;
import com.ngusta.cupassist.io.TournamentListDownloader;

import android.app.Application;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MyApplication extends Application {

    public static final String FIREBASE_TOURNAMENTS_PATH = "tournaments";

    public static final String FIREBASE_REGISTERED_TEAMS_PATH = "registeredTeams";

    public static boolean RUN_AS_ANDROID_APP = true;

    private static Firebase firebase;

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://beachvolleydb.firebaseio.com/v1");
    }

    public static Firebase getTournamentsFirebase() {
        return firebase.child(FIREBASE_TOURNAMENTS_PATH);
    }

    public static Firebase getTeamsFirebase() {
        return firebase.child(FIREBASE_REGISTERED_TEAMS_PATH);
    }

    private static void desktopRun() throws IOException {
        long start = System.currentTimeMillis();
        PlayerListDownloader playerListDownloader = new PlayerListDownloader();
        Map<String, Player> players = playerListDownloader.getPlayers();
        System.out.println("Players loaded, took " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        TournamentListDownloader tournamentListDownloader = new TournamentListDownloader();
        List<Tournament> tournaments = tournamentListDownloader.getTournaments();
        System.out.println(
                "Tournaments loaded, took " + (System.currentTimeMillis() - start) + " ms");
        for (Tournament tournament : tournaments) {
            if (tournament.getName().equals("BBVC Challenger II")) {

                System.out.println("\n" + tournament);
                tournamentListDownloader.getTournamentDetails(tournament, players);

                for (Tournament.TournamentClazz clazz : tournament.getClazzes()) {
                    for (Tournament.TeamGroupPosition groupTeamPair : tournament
                            .getTeamGroupPositionsForClazz(clazz)) {
                        int group = groupTeamPair.group;
                        Team team = groupTeamPair.team;
                        System.out.println(
                                group + " " + team.getPlayerA().getName() + " och " + team
                                        .getPlayerB().getName()
                                        + " entry: " + (team.getPlayerA().getEntryPoints() + team
                                        .getPlayerB().getEntryPoints()) + " Paid: " + team.hasPaid()
                        );
                    }
                    System.out.println();
                }
            }
        }
    }

    public static void main(String[] args) {
        RUN_AS_ANDROID_APP = false;
        try {
            desktopRun();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
