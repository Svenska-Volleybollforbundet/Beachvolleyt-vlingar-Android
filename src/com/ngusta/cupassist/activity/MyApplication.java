package com.ngusta.cupassist.activity;

import android.app.Application;
import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.domain.Team;
import com.ngusta.cupassist.domain.Tournament;
import com.ngusta.cupassist.io.PlayerListCache;
import com.ngusta.cupassist.io.TournamentListCache;

import java.util.List;
import java.util.Set;

public class MyApplication extends Application {

    public static boolean RUN_AS_ANDROID_APP = true;
    public static final boolean CACHE_PLAYERS = true;
    public static final boolean CACHE_TOURNAMENTS = true;

    private TournamentListCache tournamentListCache;
    private static PlayerListCache playerListCache;

    @Override
    public void onCreate() {
        super.onCreate();
        tournamentListCache = new TournamentListCache(this);
        playerListCache = new PlayerListCache(this);
    }

    private static void desktopRun() {
        long start = System.currentTimeMillis();
        playerListCache = new PlayerListCache();
        Set<Player> players = playerListCache.getPlayers();
        System.out.println("Players loaded, took " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        TournamentListCache tournamentListCache = new TournamentListCache();
        List<Tournament> tournaments = tournamentListCache.getTournaments();
        System.out.println("Tournaments loaded, took " + (System.currentTimeMillis() - start) + " ms");
        for (Tournament tournament : tournaments) {
            if (!tournament.getName().equals("Beachhallen Open I 2014")) {
                continue;
            }
            System.out.println("\n" + tournament);
            tournamentListCache.getTeams(tournament, players);
            for (Team team : tournament.getSeededTeams()) {
                System.out.println(team.getPlayerA().getName() + " och " + team.getPlayerB().getName()
                        + " entry: " + (team.getPlayerA().getEntryPoints() + team.getPlayerB().getEntryPoints()));
            }
        }
    }

    public TournamentListCache getTournamentListCache() {
        return tournamentListCache;
    }

    public PlayerListCache getPlayerListCache() {
        return playerListCache;
    }

    public static void main(String[] args) {
        RUN_AS_ANDROID_APP = false;
        desktopRun();
    }
}
