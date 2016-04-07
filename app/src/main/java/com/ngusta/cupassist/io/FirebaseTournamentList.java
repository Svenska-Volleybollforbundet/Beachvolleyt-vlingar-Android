package com.ngusta.cupassist.io;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.ngusta.beachvolley.domain.NewTeam;
import com.ngusta.beachvolley.domain.Player;
import com.ngusta.beachvolley.domain.Team;
import com.ngusta.beachvolley.domain.Tournament;
import com.ngusta.cupassist.activity.MyApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseTournamentList extends TournamentListCache {

    private final List<Tournament> tournaments;

    private final Map<String, List<NewTeam>> registeredTeams;

    public FirebaseTournamentList() {
        tournaments = new ArrayList<>();
        MyApplication.getFirebase().child("tournaments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    tournaments.add(postSnapshot.getValue(Tournament.class));
                }
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        registeredTeams = new HashMap<>();
        MyApplication.getFirebase().child("registeredTeams").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot teamListSnapshot : snapshot.getChildren()) {
                    List<NewTeam> teams = new ArrayList<>();
                    for (DataSnapshot teamSnapshot : teamListSnapshot.getChildren()) {
                        teams.add(teamSnapshot.getValue(NewTeam.class));
                    }
                    registeredTeams.put(teamListSnapshot.getKey(), teams);
                }
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    @Override
    public List<Tournament> getTournaments() {
        while (tournaments.isEmpty()) {
            try {
                Thread.sleep(100);
                System.out.println("Waiting for tournaments");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(tournaments);
        return tournaments;
    }

    @Override
    public void getTournamentDetails(final Tournament tournament, final Map<String, Player> allPlayers) throws IOException {
        while (registeredTeams.isEmpty()) {
            try {
                Thread.sleep(100);
                System.out.println("Waiting for registered teams");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        List<Team> teams = new ArrayList<>();
        List<NewTeam> registeredTeamsForTournament = registeredTeams.get(tournament.uniqueIdentifier());
        if (registeredTeamsForTournament != null) {
            for (NewTeam team : registeredTeamsForTournament) {
                Player playerA = allPlayers.get(team.getPlayerA());
                if (playerA == null) {
                    String[] split = team.getPlayerA().split(" ");
                    playerA = new Player(split[0], split[1], split.length == 3 ? split[2] : "");
                }
                Player playerB = allPlayers.get(team.getPlayerB());
                if (playerB == null) {
                    String[] split = team.getPlayerB().split(" ");
                    playerB = new Player(split[0], split[1], split.length == 3 ? split[2] : "");
                }
                teams.add(new Team(playerA, playerB, team.getRegistrationTime(),
                        team.getClazz(), team.isPaid()));
            }
        }
        tournament.setTeams(teams);
    }
}
