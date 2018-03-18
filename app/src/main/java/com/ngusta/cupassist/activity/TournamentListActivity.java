package com.ngusta.cupassist.activity;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.adapters.TournamentListAdapter;
import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.CompetitionPeriod;
import com.ngusta.cupassist.domain.Region;
import com.ngusta.cupassist.domain.Tournament;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TournamentListActivity extends ListActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new RequestTournamentsTask().execute();
        setContentView(R.layout.activity_tournament_list);
        mProgressContainer = findViewById(R.id.progressContainer);
        mListContainer = findViewById(R.id.listContainer);
        setListShown(false, false);

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
    protected void onListItemClick(ListView l, View v, int position, long id) {
        ListAdapter adapter = l.getAdapter();
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
        menu.add(Menu.NONE, R.id.menu_item_today, Menu.NONE, R.string.current_competition_period)
                .setIcon(android.R.drawable.ic_menu_today)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(Menu.NONE, R.id.menu_item_clazzes, Menu.NONE, R.string.show_clazzes)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(Menu.NONE, R.id.menu_item_levels, Menu.NONE, R.string.show_levels)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(Menu.NONE, R.id.menu_item_regions, Menu.NONE, R.string.show_regions)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(Menu.NONE, R.id.menu_item_players, Menu.NONE, "Visa spelare")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        //Add when Feedback fran Samuel: Mojlighet att dolja passerade tavlingar is done
        //menu.add(Menu.NONE, R.id.menu_item_old_tournaments, Menu.NONE, R.string.show_old_tournaments)
        //        .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);

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
            case R.id.menu_item_old_tournaments:
                mShowOldTournaments = true;
                updateList();
                return true;
            case R.id.menu_item_players:
                PlayerListActivity.startActivity(this);
                return true;
        }
        return false;
    }

    @Override
    public void setListAdapter(ListAdapter adapter) {
        super.setListAdapter(adapter);
        selectCurrentCompetitionPeriod();
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
        SectionIndexer listAdapter = (SectionIndexer) getListAdapter();
        CompetitionPeriod period = CompetitionPeriod.findPeriodByDate(new Date());
        int position = listAdapter.getPositionForSection(period.getPeriodNumber() - 1);
        setSelection(position);
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
        setListAdapter(new TournamentListAdapter(this, filteredTournaments, mShowOldTournaments));
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