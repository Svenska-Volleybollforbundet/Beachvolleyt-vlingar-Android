package com.ngusta.cupassist.activity;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.ngusta.beachvolley.domain.Player;
import com.ngusta.beachvolley.domain.Tournament;
import com.ngusta.cupassist.R;
import com.ngusta.cupassist.adapters.PlayerAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayerListActivity extends AppCompatActivity {

    private RecyclerView playerView;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PlayerListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_view);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        playerView = (RecyclerView) findViewById(R.id.player_list);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        playerView.setLayoutManager(mLayoutManager);
        playerView.setItemAnimator(new DefaultItemAnimator());

        MyApplication.getPlayersFirebase().addValueEventListener(getPlayerListListener());
    }

    private ValueEventListener getPlayerListListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Player> newPlayers = new HashMap<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Player player = postSnapshot.getValue(Player.class);
                    newPlayers.put(player.uniqueIdentifier(), player);
                }
                playerView.setAdapter(new PlayerAdapter(new ArrayList<>(newPlayers.values())));
                playerView.getAdapter().notifyDataSetChanged();
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        };
    }
}
