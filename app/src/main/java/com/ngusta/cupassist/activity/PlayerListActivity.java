package com.ngusta.cupassist.activity;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.ngusta.beachvolley.domain.Clazz;
import com.ngusta.beachvolley.domain.Player;
import com.ngusta.cupassist.PlayerListFragment;
import com.ngusta.cupassist.R;
import com.ngusta.cupassist.adapters.PagerAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerListActivity extends AppCompatActivity {

    private PlayerListFragment womenFragment;

    private PlayerListFragment menFragment;

    private PlayerListFragment mixedFragment;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PlayerListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.beachvolleyicon);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        womenFragment = new PlayerListFragment();
        menFragment = new PlayerListFragment();
        mixedFragment = new PlayerListFragment();
        adapter.addFragment(womenFragment, "Damer");
        adapter.addFragment(menFragment, "Herrar");
        adapter.addFragment(mixedFragment, "Mixed");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        MyApplication.getPlayersFirebase().addValueEventListener(getPlayerListListener());
    }

    private ValueEventListener getPlayerListListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Player> newPlayers = new HashMap<>();
                List<Player> women = new ArrayList<>();
                List<Player> men = new ArrayList<>();
                List<Player> mixed = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Player player = postSnapshot.getValue(Player.class);
                    if (player.getClazz() == Clazz.MEN) {
                        men.add(player);
                    }
                    if (player.getClazz() == Clazz.WOMEN) {
                        women.add(player);
                    }
                    if (player.getMixRankingPoints() != 0) {
                        mixed.add(player);
                    }
                    newPlayers.put(player.uniqueIdentifier(), player);
                }
                menFragment.addPlayers(men, Clazz.MEN);
                womenFragment.addPlayers(women, Clazz.WOMEN);
                mixedFragment.addPlayers(mixed, Clazz.MIXED);
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        };
    }
}
