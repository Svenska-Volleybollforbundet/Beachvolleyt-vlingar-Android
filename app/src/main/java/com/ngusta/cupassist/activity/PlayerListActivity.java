package com.ngusta.cupassist.activity;

import com.google.common.collect.HashMultimap;

import com.ngusta.cupassist.PlayerListFragment;
import com.ngusta.cupassist.R;
import com.ngusta.cupassist.adapters.PagerAdapter;
import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.service.PlayerService;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class PlayerListActivity extends AppCompatActivity {

    private View mProgressContainer;

    private boolean mListShown;

    private PlayerListFragment womenFragment;

    private PlayerListFragment menFragment;

    private PlayerListFragment mixedFragment;

    private ViewPager mViewPager;

    private EditText filterTextView;

    private boolean loadedPlayers = false;

    private PlayerService playerService;

    private CommonActivityHelper commonActivityHelper;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PlayerListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

        commonActivityHelper = new CommonActivityHelper(this);
        commonActivityHelper.initToolbarAndNavigation();

        playerService = ((MyApplication) getApplicationContext()).getPlayerService();
        new RequestPlayersTask().execute();
        mProgressContainer = findViewById(R.id.progressContainer);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setListShown(false, true);

        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        womenFragment = new PlayerListFragment();
        menFragment = new PlayerListFragment();
        mixedFragment = new PlayerListFragment();
        womenFragment.setPlayerService(playerService);
        menFragment.setPlayerService(playerService);
        mixedFragment.setPlayerService(playerService);
        adapter.addFragment(womenFragment, "Damer");
        adapter.addFragment(menFragment, "Herrar");
        adapter.addFragment(mixedFragment, "Mixed");
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        filterTextView = findViewById(R.id.player_name_filter);
        filterTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (loadedPlayers) {
                    womenFragment.getAdapter().getFilter().filter(charSequence);
                    menFragment.getAdapter().getFilter().filter(charSequence);
                    mixedFragment.getAdapter().getFilter().filter(charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        commonActivityHelper.syncDrawerToggleState();
    }

    private class RequestPlayersTask extends AsyncTask<Void, String, HashMultimap<String, Player>> {

        @Override
        protected HashMultimap<String, Player> doInBackground(Void... voids) {
            playerService.clearPlayers();
            return playerService.getPlayers();
        }

        @Override
        protected void onPostExecute(HashMultimap<String, Player> players) {
            updatePlayers(players);
            loadedPlayers = true;
        }
    }

    public void updatePlayers(HashMultimap<String, Player> players) {
        List<Player> women = new ArrayList<>();
        List<Player> men = new ArrayList<>();
        List<Player> mixed = new ArrayList<>();
        for (Player player : players.values()) {
            if (player.getClazz() == Clazz.MEN) {
                men.add(player);
            }
            if (player.getClazz() == Clazz.WOMEN) {
                women.add(player);
            }
            if (player.getMixRankingPoints() != 0) {
                mixed.add(player);
            }
        }
        menFragment.addPlayers(men, Clazz.MEN);
        womenFragment.addPlayers(women, Clazz.WOMEN);
        mixedFragment.addPlayers(mixed, Clazz.MIXED);
        setListShown(true, true);
    }

    private void setListShown(boolean shown, boolean animate) {
        if (mListShown == shown) {
            return;
        }
        mListShown = shown;
        if (shown) {
            if (animate) {
                mProgressContainer.startAnimation(
                        AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                mViewPager
                        .startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            } else {
                mProgressContainer.clearAnimation();
                mViewPager.clearAnimation();
            }
            mProgressContainer.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mProgressContainer
                        .startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
                mViewPager.startAnimation(
                        AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
            } else {
                mProgressContainer.clearAnimation();
                mViewPager.clearAnimation();
            }
            mProgressContainer.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.GONE);
        }
    }
}
