package com.ngusta.beachvolley.backend;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.ngusta.beachvolley.backend.io.PlayerListCache;
import com.ngusta.beachvolley.backend.io.TournamentListCache;
import com.ngusta.beachvolley.domain.NewTeam;
import com.ngusta.beachvolley.domain.Player;
import com.ngusta.beachvolley.domain.Team;
import com.ngusta.beachvolley.domain.Tournament;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyServlet extends HttpServlet {

    static Logger Log = Logger.getLogger("com.ngusta.beachvolley.backend.MyServlet");

    private Firebase firebase;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        firebase = new Firebase("https://incandescent-heat-8146.firebaseio.com/");
        long start = System.currentTimeMillis();
        firebase.child("Latest ran").setValue(new Date().toString());

        //updateAll();

        firebase.child("tournaments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("There are " + snapshot.getChildrenCount() + " tournaments");
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Tournament tournament = postSnapshot.getValue(Tournament.class);
                    System.out.println(tournament);
                }
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        firebase.child("players").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("There are " + snapshot.getChildrenCount() + " players");
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Player player = postSnapshot.getValue(Player.class);
                    System.out.println(player);
                }
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        firebase.child("registeredTeams").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("There are " + snapshot.getChildrenCount() + " tournaments with registered teams");
                for (DataSnapshot teamListSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot teamSnapshot : teamListSnapshot.getChildren()) {
                        NewTeam team = teamSnapshot.getValue(NewTeam.class);
                        System.out.println(team);
                    }
                }
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        long duration = System.currentTimeMillis() - start;
        firebase.child("Run time").setValue(duration + " ms");
    }

    private void updateAll() throws IOException {
        TournamentListCache tournamentListCache = new TournamentListCache();
        Map<String, Tournament> tournaments = tournamentListCache.getTournaments();

        PlayerListCache playerListCache = new PlayerListCache();
        Map<String, Player> players = playerListCache.getPlayers();
        firebase.child("players").setValue(players);

        for (Tournament tournament : tournaments.values()) {
            List<NewTeam> teams = tournamentListCache.getTournamentDetails(tournament, players);
            firebase.child("registeredTeams/" + tournament.uniqueIdentifier()).setValue(teams);
        }
        firebase.child("tournaments").setValue(tournaments);
    }
}
