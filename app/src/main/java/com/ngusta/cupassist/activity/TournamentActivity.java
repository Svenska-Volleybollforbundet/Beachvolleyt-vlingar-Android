package com.ngusta.cupassist.activity;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.adapters.TeamAdapter;
import com.ngusta.cupassist.adapters.TournamentListAdapter;
import com.ngusta.cupassist.domain.Tournament;
import com.ngusta.cupassist.io.TournamentListCache;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class TournamentActivity extends Activity {

    public static final String TOURNAMENT_INTENT = "tournamentId";

    private View mProgressContainer;

    private View mTeamList;

    private boolean mListShown;

    private Tournament tournament;

    public static void startActivity(Context context, Tournament tournament) {
        Intent intent = new Intent(context, TournamentActivity.class);
        intent.putExtra(TOURNAMENT_INTENT, tournament.getId());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tournament_view);
        tournament = ((MyApplication) getApplication()).getTournamentService().getTournamentById(getIntent().getIntExtra(TOURNAMENT_INTENT, -1));
        mProgressContainer = findViewById(R.id.progressContainer);
        mTeamList = findViewById(R.id.teamList);
        setListShown(false, false);
        initInfo();
        makeActionOverflowMenuShown();
        new RequestTournamentDetailsTask().execute();
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
                mTeamList
                        .startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            } else {
                mProgressContainer.clearAnimation();
                mTeamList.clearAnimation();
            }
            mProgressContainer.setVisibility(View.GONE);
            mTeamList.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mProgressContainer
                        .startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
                mTeamList.startAnimation(
                        AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
            } else {
                mProgressContainer.clearAnimation();
                mTeamList.clearAnimation();
            }
            mProgressContainer.setVisibility(View.VISIBLE);
            mTeamList.setVisibility(View.GONE);
        }
    }

    private void initInfo() {
        getActionBar().setTitle(tournament.getName());
        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(
                TournamentListAdapter.getLevelIndicatorResource(tournament))));
        ((TextView) findViewById(R.id.club)).setText(tournament.getClub());
        ((TextView) findViewById(R.id.start_date)).setText(tournament.getFormattedStartDate());
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

    private class RequestTournamentDetailsTask extends AsyncTask<Void, String, Exception> {

        @Override
        protected Exception doInBackground(Void... voids) {
            try {
                ((MyApplication) getApplication()).getTournamentService().loadTournamentDetails(tournament);
            } catch (IOException e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception exception) {
            if (exception == null && tournament.isRegistrationOpen()) {
                initClazzSpinner();
                Tournament.TournamentClazz clazz = tournament.getClazzes().get(0);
                setTeamsInfo(clazz);
                updateTeams(clazz);
            } else if (exception != null) {
                Toast.makeText(TournamentActivity.this, "Kunde inte ladda lag. Kolla din internetanslutning.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(TournamentActivity.this, "Registreringen har inte öppnat.", Toast.LENGTH_LONG).show();
            }
            setListShown(true, true);
        }
    }

    private void initClazzSpinner() {
        if (tournament.getClazzes().size() <= 1) {
            return;
        }
        Spinner clazzSpinner = (Spinner) findViewById(R.id.clazz_spinner);
        clazzSpinner.setVisibility(View.VISIBLE);
        ArrayAdapter<Tournament.TournamentClazz> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.addAll(tournament.getClazzes());
        clazzSpinner.setAdapter(adapter);
        clazzSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Tournament.TournamentClazz clazz = (Tournament.TournamentClazz) parent.getSelectedItem();
                setTeamsInfo(clazz);
                updateTeams(clazz);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setTeamsInfo(Tournament.TournamentClazz clazz) {
        Integer maxNumberOfTeams = clazz.getMaxNumberOfTeams();
        ((TextView) findViewById(R.id.teams_info)).setText(
                getResources().getString(R.string.registered) + " " + tournament.getNumberOfCompleteTeamsForClazz(clazz) + "/" + (
                        maxNumberOfTeams == 1000 ? "?" : maxNumberOfTeams));
    }

    private void updateTeams(Tournament.TournamentClazz clazz) {
        ListView teamListView = (ListView) findViewById(R.id.teamList);
        List<Tournament.TeamGroupPosition> teams = tournament.getTeamGroupPositionsForClazz(clazz);
        final ArrayAdapter<Tournament.TeamGroupPosition> adapter = new TeamAdapter(this, R.layout.team_list_item, teams, clazz.getMaxNumberOfTeams());
        teamListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, R.id.menu_item_see_result, Menu.NONE, R.string.see_result)
                .setIcon(R.drawable.external_link_enabled)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(Menu.NONE, R.id.menu_item_register_result, Menu.NONE, R.string.register_result)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_item_see_result:
                openBrowser(TournamentListCache.PROFIXIO_BASE_RESULT_URL + TournamentActivity.this.tournament.getUrlName());
                return true;
            case R.id.menu_item_register_result:
                openBrowser(TournamentListCache.PROFIXIO_BASE_RESULT_REPORTING_URL + TournamentActivity.this.tournament.getUrlName());
                return true;
        }
        return false;
    }

    private void openBrowser(String uriString) {
        Uri cupAssistUri = Uri.parse(uriString);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, cupAssistUri);
        startActivity(browserIntent);
    }
}
