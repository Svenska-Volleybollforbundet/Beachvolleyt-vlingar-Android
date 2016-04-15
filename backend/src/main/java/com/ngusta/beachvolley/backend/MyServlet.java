package com.ngusta.beachvolley.backend;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.ngusta.beachvolley.backend.io.PlayerListCache;
import com.ngusta.beachvolley.backend.io.SourceCodeRequester;
import com.ngusta.beachvolley.backend.io.TournamentListCache;
import com.ngusta.beachvolley.domain.Player;
import com.ngusta.beachvolley.domain.Team;
import com.ngusta.beachvolley.domain.Tournament;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyServlet extends HttpServlet {

    public static final String ALL = "all";

    public static final String ACTIVE_TOURNAMENTS = "active";

    public static final int TWO_WEEKS_IN_MS = 14 * 24 * 3600 * 1000;

    private Firebase firebase;

    private Map<String, Player> players;

    private List<Tournament> tournaments;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        firebase = new Firebase("https://beachvolleydb.firebaseio.com/v1");
        long start = System.currentTimeMillis();
        String path = req.getPathInfo().substring(1);
        firebase.child("Latest update " + path + " run").setValue(new Date().toString());
        switch (path) {
            case ALL:
                updateAll();
                break;
            case ACTIVE_TOURNAMENTS:
                updateActive();
                break;
        }
        long duration = System.currentTimeMillis() - start;
        System.out.println("Run time for " + path + ": " + duration + " ms");
        firebase.child("Run time for " + path).setValue(duration + " ms");
    }

    private void updateAll() throws IOException {
        TournamentListCache tournamentListCache = new TournamentListCache();
        Map<String, Tournament> tournaments = tournamentListCache.getTournaments();

        PlayerListCache playerListCache = new PlayerListCache();
        Map<String, Player> players = playerListCache.getPlayers();
        firebase.child("players").setValue(players);

        System.out.println("Updated all " + players.size() + " players.");
        for (Tournament tournament : tournaments.values()) {
            if (tournament.getUrl() != null && !tournament.getUrl().isEmpty()) {
                List<Team> teams = tournamentListCache.getTournamentDetails(tournament, players);
                firebase.child("registeredTeams/" + tournament.uniqueIdentifier()).setValue(teams);
            }
        }
        firebase.child("tournaments").setValue(tournaments);
        System.out.println("Updated all " + tournaments.size() + " tournaments.");
    }

    private void updateActive() throws IOException {
        requestPlayers();
        waitForPlayers();
        System.out.println("Players read");
        TournamentListCache tournamentListCache = new TournamentListCache();
        long now = System.currentTimeMillis();
        long twoWeeksInTheFuture = System.currentTimeMillis() + TWO_WEEKS_IN_MS;
        Query qref = firebase.child("tournaments").orderByChild("startDate").startAt(now).endAt(twoWeeksInTheFuture);
        requestActiveTournaments(qref);
        waitForTournaments();

        setCookie();
        for (Tournament tournament : tournaments) {
            try {
                if (tournament.getUrl() != null && !tournament.getUrl().isEmpty()) {
                    List<Team> teams = tournamentListCache.getTeams(tournament, players);
                    firebase.child("registeredTeams/" + tournament.uniqueIdentifier()).setValue(teams);
                    System.out.println("Updated " + teams.size() + " teams for " + tournament.getName());
                } else {
                    System.out.println("Tried to update the tournament " + tournament.getName() + ", but it wasn't open.");
                }
            } catch (IOException exception) {
                System.out.println("Could not parse teams for " + tournament.toString());
            }
        }
    }

    private void requestPlayers() {
        firebase.child("players").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Player> newPlayers = new HashMap<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Player player = postSnapshot.getValue(Player.class);
                    newPlayers.put(player.uniqueIdentifier(), player);
                }
                players = newPlayers;
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void waitForPlayers() {
        try {
            while (players == null || players.isEmpty()) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void requestActiveTournaments(Query qref) {
        final List<Tournament> downloadedTournaments = new ArrayList<>();
        qref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    downloadedTournaments.add(snapshot.getValue(Tournament.class));
                }
                tournaments = downloadedTournaments;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void waitForTournaments() {
        try {
            while (tournaments == null || tournaments.isEmpty()) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String setCookie() throws IOException {
        return new SourceCodeRequester().getSourceCode("http://www.cupassist.com/pa/ranking_beach/index.php");
    }
}
