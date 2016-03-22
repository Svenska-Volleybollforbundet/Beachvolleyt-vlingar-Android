package com.ngusta.cupassist.activity;

import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.domain.Team;
import com.ngusta.cupassist.domain.Tournament;
import com.ngusta.cupassist.io.PlayerListCache;
import com.ngusta.cupassist.io.TournamentListCache;
import com.ngusta.cupassist.service.PlayerService;
import com.ngusta.cupassist.service.TournamentService;

import android.app.Application;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MyApplication extends Application {

    public static boolean RUN_AS_ANDROID_APP = true;

    public static final boolean CACHE_PLAYERS = true;

    public static final boolean CACHE_TOURNAMENTS = false;

    private PlayerService playerService;

    private TournamentService tournamentService;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public PlayerService getPlayerService() {
        if (playerService == null) {
            playerService = new PlayerService(getApplicationContext());
        }
        return playerService;
    }

    public TournamentService getTournamentService() {
        if (tournamentService == null) {
            tournamentService = new TournamentService(getApplicationContext(), getPlayerService());
        }
        return tournamentService;
    }

    private static void desktopRun() throws IOException {
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
            if (tournament.getName().equals("BBVC Challenger II")) {

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
