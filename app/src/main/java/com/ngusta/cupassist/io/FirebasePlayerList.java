package com.ngusta.cupassist.io;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.ngusta.beachvolley.domain.Player;
import com.ngusta.cupassist.activity.MyApplication;

import java.util.HashMap;
import java.util.Map;

public class FirebasePlayerList extends PlayerListCache {

    private final HashMap<String, Player> players;

    public FirebasePlayerList() {
        players = new HashMap<>();
        MyApplication.getFirebase().child("players").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Player player = postSnapshot.getValue(Player.class);
                    players.put(player.uniqueIdentifier(), player);
                }
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    @Override
    public Map<String, Player> getPlayers() {
        while (players.isEmpty()) {
            try {
                Thread.sleep(100);
                System.out.println("Waiting for players");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return players;
    }
}
