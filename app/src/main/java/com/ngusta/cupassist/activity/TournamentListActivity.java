package com.ngusta.cupassist.activity;

import com.hb.views.PinnedSectionListView;
import com.ngusta.cupassist.R;
import com.ngusta.cupassist.adapters.TournamentListAdapter;
import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.CompetitionPeriod;
import com.ngusta.cupassist.domain.Region;
import com.ngusta.cupassist.domain.Tournament;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.SectionIndexer;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TournamentListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private View mProgressContainer;

    private View mListContainer;

    private boolean mListShown;

    private List<Clazz> mClazzesToFilter;

    private List<Tournament.Level> mLevelsToFilter;

    private List<Region> mRegionsToFilter;

    private AlertDialog.Builder mClazzDialog;

    private AlertDialog.Builder mLevelDialog;

    private AlertDialog.Builder mRegionDialog;

    private List<Tournament> mTournaments;

    private boolean mShowOldTournaments;

    private PinnedSectionListView mListView;

    private Menu menu;

    private CommonActivityHelper commonActivityHelper;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, TournamentListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new RequestTournamentsTask().execute();
        setContentView(R.layout.activity_tournament_list);

        commonActivityHelper = new CommonActivityHelper(this);
        commonActivityHelper.initToolbarAndNavigation();

        mProgressContainer = findViewById(R.id.progressContainer);
        mListContainer = findViewById(R.id.listContainer);
        setListShown(false, false);

        mListView = findViewById(android.R.id.list);
        mListView.setOnItemClickListener(this);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        mClazzesToFilter = TournamentListDialogs.getDefaultClazzes(preferences);
        mClazzDialog = TournamentListDialogs.createClazzFilterDialog(this, preferences);
        mLevelsToFilter = TournamentListDialogs.getDefaultLevels(preferences);
        mLevelDialog = TournamentListDialogs.createLevelFilterDialog(this, preferences);
        mRegionsToFilter = TournamentListDialogs.getDefaultRegions(preferences);
        mRegionDialog = TournamentListDialogs.createRegionFilterDialog(this, preferences);
        mShowOldTournaments = true; //Should be false when Feedback fran Samuel: Mojlighet att dolja passerade tavlingar is done

        makeActionOverflowMenuShown();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        commonActivityHelper.syncDrawerToggleState();
    }

    private void makeActionOverflowMenuShown() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListAdapter adapter = mListView.getAdapter();
        if (adapter.getItemViewType(position) != TournamentListAdapter.VIEW_TYPE_TOURNAMENT) {
            return;
        }
        Tournament tournament = (Tournament) adapter.getItem(position);
        if (!tournament.getUrl().isEmpty()) {
            TournamentActivity.startActivity(this, tournament);
        } else {
            Toast.makeText(this, R.string.registration_not_open, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        menu.add(Menu.NONE, R.id.menu_item_today, Menu.NONE, R.string.current_competition_period)
                .setIcon(android.R.drawable.ic_menu_today)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(Menu.NONE, R.id.menu_item_clazzes, Menu.NONE, R.string.show_clazzes)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(Menu.NONE, R.id.menu_item_levels, Menu.NONE, R.string.show_levels)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(Menu.NONE, R.id.menu_item_regions, Menu.NONE, R.string.show_regions)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_item_today:
                selectCurrentCompetitionPeriod();
                return true;
            case R.id.menu_item_clazzes:
                mClazzDialog.show();
                return true;
            case R.id.menu_item_levels:
                mLevelDialog.show();
                return true;
            case R.id.menu_item_regions:
                mRegionDialog.show();
                return true;

        }
        return false;
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
                mListContainer
                        .startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            } else {
                mProgressContainer.clearAnimation();
                mListContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.GONE);
            mListContainer.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mProgressContainer
                        .startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
                mListContainer.startAnimation(
                        AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
            } else {
                mProgressContainer.clearAnimation();
                mListContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.VISIBLE);
            mListContainer.setVisibility(View.GONE);
        }
    }

    private void selectCurrentCompetitionPeriod() {
        SectionIndexer listAdapter = (SectionIndexer) mListView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        CompetitionPeriod period = CompetitionPeriod.findPeriodByDate(new Date());
        int position = listAdapter.getPositionForSection(period.getPeriodNumber() - 1);
        mListView.setSelection(position);

    }

    private class RequestTournamentsTask extends AsyncTask<Void, String, List<Tournament>> {

        @Override
        protected List<Tournament> doInBackground(Void... voids) {
            return ((MyApplication) getApplication()).getTournamentService().getAllTournaments();
        }

        @Override
        protected void onPostExecute(List<Tournament> tournaments) {
            mTournaments = tournaments;
            updateList();
        }
    }

    void updateList() {
        List<Tournament> filteredTournaments = filter(mTournaments);
        if (filteredTournaments.isEmpty()) {
            Toast.makeText(this, R.string.no_matching_tournaments, Toast.LENGTH_SHORT).show();
        }
        mListView.setAdapter(new TournamentListAdapter(this, filteredTournaments, mShowOldTournaments));
        selectCurrentCompetitionPeriod();
        setListShown(true, true);
    }

    private List<Tournament> filter(List<Tournament> tournaments) {
        List<Tournament> filteredTournaments = new ArrayList<>();
        for (Tournament tournament : tournaments) {
            for (Tournament.TournamentClazz tournamentClazz : tournament.getClazzes()) {
                if (mClazzesToFilter.contains(tournamentClazz.getClazz()) && (tournament.getLevel() == Tournament.Level.UNKNOWN || mLevelsToFilter
                        .contains(tournament.getLevel()))
                        && (tournament.getRegion() == null || mRegionsToFilter.contains(tournament.getRegion()))
                        && (mShowOldTournaments || tournament.getCompetitionPeriod().getEndDate().after(new Date()))) {
                    filteredTournaments.add(tournament);
                    break;
                }
            }
        }
        return filteredTournaments;
    }

    public void setClazzesToFilter(List<Clazz> clazzesToFilter) {
        mClazzesToFilter = clazzesToFilter;
    }

    public void setLevelsToFilter(List<Tournament.Level> levelsToFilter) {
        mLevelsToFilter = levelsToFilter;
    }

    public void setRegionsToFilter(List<Region> regionsToFilter) {
        mRegionsToFilter = regionsToFilter;
    }
}