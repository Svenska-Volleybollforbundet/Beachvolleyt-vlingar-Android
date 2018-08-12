package com.ngusta.cupassist.activity;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.io.TournamentListCache;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;

public class CommonActivityHelper implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    private AppCompatActivity activity;

    private ActionBarDrawerToggle drawerToggle;

    CommonActivityHelper(AppCompatActivity activity) {
        this.activity = activity;
    }

    void initToolbarAndNavigation() {
        drawer = activity.findViewById(R.id.drawer_layout);

        Toolbar toolbar = activity.findViewById(R.id.app_toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);

        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (isTopNavigationActivity()) {
            drawerToggle = new ActionBarDrawerToggle(activity, drawer, toolbar, R.string.open, R.string.close) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                }
            };

            drawer.addDrawerListener(drawerToggle);
            checkCurrentMenuItem(navigationView);
        }
    }

    private void checkCurrentMenuItem(NavigationView navigationView) {
        if (isTournamentListActivity()) {
            navigationView.setCheckedItem(R.id.nav_competitions);
        } else if (isPlayerListActivity()) {
            navigationView.setCheckedItem(R.id.nav_players);
        } else if (isCourtActivity()) {
            navigationView.setCheckedItem(R.id.nav_courts);
        }
    }

    private boolean isTopNavigationActivity() {
        return isTournamentListActivity() || isPlayerListActivity() || isCourtActivity();
    }

    private boolean isTournamentListActivity() {
        return activity instanceof TournamentListActivity;
    }

    private boolean isPlayerListActivity() {
        return activity instanceof PlayerListActivity;
    }

    private boolean isCourtActivity() {
        return activity instanceof CourtActivity;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        drawer.closeDrawers();
        switch (item.getItemId()) {
            case R.id.nav_competitions:
                if (isTournamentListActivity()) {
                    break;
                }
                TournamentListActivity.startActivity(activity);
                break;
            case R.id.nav_players:
                if (isPlayerListActivity()) {
                    break;
                }
                PlayerListActivity.startActivity(activity);
                break;
            case R.id.nav_courts:
                if (isCourtActivity()) {
                    break;
                }
                CourtActivity.startActivity(activity);
                break;
            case R.id.nav_competition_calendar:
                setTextColorToBlack(item);
                openBrowser(TournamentListCache.CUP_ASSIST_TOURNAMENT_LIST_URL_WITHOUT_OLD_TOURNAMENTS);
                break;
            case R.id.nav_competition_regulations:
                setTextColorToBlack(item);
                openBrowser(TournamentListCache.COMPETITION_REGULATIONS_URL);
                break;
            case R.id.nav_rules:
                setTextColorToBlack(item);
                openBrowser(TournamentListCache.RULES_URL);
        }
        return true;
    }

    private void setTextColorToBlack(@NonNull MenuItem item) {
        SpannableString spanString = new SpannableString(item.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spanString.length(), 0); // fix the color to white
        item.setTitle(spanString);
    }

    private void openBrowser(String uriString) {
        Uri cupAssistUri = Uri.parse(uriString);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, cupAssistUri);
        activity.startActivity(browserIntent);
    }

    void syncDrawerToggleState() {
        if (isTopNavigationActivity()) {
            drawerToggle.syncState();
        }
    }
}
