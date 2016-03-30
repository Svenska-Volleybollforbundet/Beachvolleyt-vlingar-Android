package com.ngusta.cupassist.activity;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.adapters.TeamAdapter;
import com.ngusta.cupassist.adapters.TournamentListAdapter;
import com.ngusta.beachvolley.domain.Tournament;
import com.ngusta.cupassist.io.TournamentListCache;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
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
                initCALink();
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
        Spinner clazzSpinner = (Spinner) findViewById(R.id.clazz_spinner);
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

    private void initCALink() {
        final ImageButton button = (ImageButton) findViewById(R.id.open_cupassist);
        button.setImageResource(R.drawable.external_link_enabled);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri cupAssistUri = Uri.parse(TournamentListCache.CUP_ASSIST_TOURNAMENT_URL + tournament.getRegistrationUrl());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, cupAssistUri);
                startActivity(browserIntent);
            }
        });
    }

    private void setTeamsInfo(Tournament.TournamentClazz clazz) {
        ((TextView) findViewById(R.id.teams_info)).setText(
                getResources().getString(R.string.registered) + " " + tournament.getNumberOfCompleteTeamsForClazz(clazz) + "/" + clazz
                        .getMaxNumberOfTeams());
    }

    private void updateTeams(Tournament.TournamentClazz clazz) {
        ListView teamListView = (ListView) findViewById(R.id.teamList);
        List<Tournament.TeamGroupPosition> teams = tournament.getTeamGroupPositionsForClazz(clazz);
        final ArrayAdapter<Tournament.TeamGroupPosition> adapter = new TeamAdapter(this, R.layout.team_list_item, teams, clazz.getMaxNumberOfTeams());
        teamListView.setAdapter(adapter);
    }
}
