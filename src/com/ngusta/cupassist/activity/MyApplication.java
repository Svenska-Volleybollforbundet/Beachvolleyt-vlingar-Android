package com.ngusta.cupassist.activity;

import android.app.Application;
import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.io.PlayerListCache;
import com.ngusta.cupassist.io.TournamentListCache;

import java.util.ArrayList;

public class MyApplication extends Application {

    public static boolean RUN_AS_ANDROID_APP = true;
    public static final boolean USE_CACHE_DATA = false;

    private TournamentListCache tournamentListCache;

    @Override
    public void onCreate() {
        super.onCreate();
        tournamentListCache = new TournamentListCache(this);
    }

    private static void desktopRun() {
//        TournamentListCache tournamentListCache = new TournamentListCache();
//        ArrayList<Tournament> tournaments = tournamentListCache.getTournaments();
//        for (Tournament tournament : tournaments) {
//            System.out.println("\n" + tournament);
//            tournamentListCache.loadTeams(tournament);
//            for (Team team : tournament.getTeams()) {
//                System.out.println(team.getPlayerA().getName() + " och " + team.getPlayerB().getName());
//            }
//        }
        PlayerListCache playerListCache = new PlayerListCache();
        ArrayList<Player> players = playerListCache.getPlayers();
        for (Player player : players) {
            System.out.println(player);
        }

    }

    public TournamentListCache getTournamentListCache() {
        return tournamentListCache;
    }

    public void setTournamentListCache(TournamentListCache tournamentListCache) {
        this.tournamentListCache = tournamentListCache;
    }

    public static void main(String[] args) {
        RUN_AS_ANDROID_APP = false;
        desktopRun();
    }
}
