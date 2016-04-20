package com.ngusta.beachvolley.backend;

import com.firebase.client.AuthData;
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
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FirebaseUpdateServlet extends HttpServlet {

    private static final String ALL = "all";

    private static final String ACTIVE_TOURNAMENTS = "active";

    private static final int ONE_DAY_IN_MS = 24 * 3600 * 1000;

    private static final int TWO_WEEKS_IN_MS = 14 * 24 * 3600 * 1000;

    private static final String FIREBASE_SECRET = "nzgY8tDeQQKWgGvBCt98EGoQz1zNVp7HA0s2Fkvg";

    private Firebase firebase;
    private Map<String, Player> players;
    private List<Tournament> tournaments;

    private Logger log = Logger.getLogger("FirebaseUpdateServlet");

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        getFirebase();
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
        log.info("Run time for " + path + ": " + duration + " ms");
        firebase.child("Run time for " + path).setValue(duration + " ms");
        Firebase.goOffline();
    }

    private void getFirebase() {
        firebase = new Firebase("https://beachvolleydb.firebaseio.com/v1");
        firebase.authWithCustomToken(FIREBASE_SECRET, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                log.info("Firebase authenticated.");
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                log.info("Authentication error when connection to Firebase: " + firebaseError.getMessage());
            }
        });
    }

    private void updateAll() throws IOException {
        TournamentListCache tournamentListCache = new TournamentListCache();
        Map<String, Tournament> tournaments = tournamentListCache.getTournaments();

        PlayerListCache playerListCache = new PlayerListCache();
        Map<String, Player> players = playerListCache.getPlayers();
        firebase.child("players").setValue(players);

        log.info("Updated all " + players.size() + " players.");
        for (Tournament tournament : tournaments.values()) {
            if (tournament.getUrl() != null && !tournament.getUrl().isEmpty()) {
                List<Team> teams = tournamentListCache.getTournamentDetails(tournament, players);
                firebase.child("registeredTeams/" + tournament.uniqueIdentifier()).setValue(teams);
            }
        }
        firebase.child("tournaments").setValue(tournaments);
        log.info("Updated all " + tournaments.size() + " tournaments.");
    }

    private void updateActive() throws IOException {
        requestPlayers();
        requestActiveTournaments();
        waitForTournamentsAndPlayers();
        log.info("Players and tournaments read");

        TournamentListCache tournamentListCache = new TournamentListCache();
        setCookie();
        for (Tournament tournament : tournaments) {
            try {
                if (tournament.getUrl() != null && !tournament.getUrl().isEmpty()) {
                    List<Team> teams = tournamentListCache.getTeams(tournament, players);
                    firebase.child("registeredTeams/" + tournament.uniqueIdentifier()).setValue(teams);
                    log.info("Updated " + teams.size() + " teams for " + tournament.getName());
                } else {
                    log.info("Tried to update the tournament " + tournament.getName() + ", but it wasn't open.");
                }
            } catch (IOException exception) {
                log.info("Could not parse teams for " + tournament.toString());
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

    private void requestActiveTournaments() {
        long now = System.currentTimeMillis() - ONE_DAY_IN_MS;
        long twoWeeksInTheFuture = System.currentTimeMillis() + TWO_WEEKS_IN_MS;
        Query qref = firebase.child("tournaments").orderByChild("startDate").startAt(now).endAt(twoWeeksInTheFuture);
        final List<Tournament> activeTournaments = new ArrayList<>();
        qref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    activeTournaments.add(snapshot.getValue(Tournament.class));
                }
                tournaments = activeTournaments;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void waitForTournamentsAndPlayers() {
        try {
            while (tournaments == null || players == null) {
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
