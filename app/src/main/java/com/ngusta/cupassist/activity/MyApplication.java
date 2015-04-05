package com.ngusta.cupassist.activity;

import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.domain.Team;
import com.ngusta.cupassist.domain.Tournament;
import com.ngusta.cupassist.io.PlayerListCache;
import com.ngusta.cupassist.io.TournamentListCache;

import android.app.Application;

import java.util.List;
import java.util.Map;

public class MyApplication extends Application {

    public static boolean RUN_AS_ANDROID_APP = true;

    public static final boolean CACHE_PLAYERS = true;

    public static final boolean CACHE_TOURNAMENTS = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private static void desktopRun() {
        long start = System.currentTimeMillis();
        PlayerListCache playerListCache = new PlayerListCache();
        Map<String, Player> players = playerListCache.getPlayers();
        System.out.println("Players loaded, took " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        TournamentListCache tournamentListCache = new TournamentListCache();
        List<Tournament> tournaments = tournamentListCache.getTournaments();
        System.out.println(
                "Tournaments loaded, took " + (System.currentTimeMillis() - start) + " ms");
        for (Tournament tournament : tournaments) {
            if (tournament.getName().equals("Beachhallen Challenger II 2014")) {

                System.out.println("\n" + tournament);
                tournamentListCache.getTournamentDetails(tournament, players);

                for (Tournament.TournamentClazz clazz : tournament.getClazzes()) {
                    for (Tournament.TeamGroupPosition groupTeamPair : tournament
                            .getTeamGroupPositionsForClazz(clazz)) {
                        int group = groupTeamPair.group;
                        Team team = groupTeamPair.team;
                        System.out.println(
                                group + " " + team.getPlayerA().getName() + " och " + team
                                        .getPlayerB().getName()
                                        + " entry: " + (team.getPlayerA().getEntryPoints() + team
                                        .getPlayerB().getEntryPoints()));
                    }
                    System.out.println();
                }
            }
        }
    }

    public static void main(String[] args) {
        RUN_AS_ANDROID_APP = false;
        desktopRun();
    }
}
