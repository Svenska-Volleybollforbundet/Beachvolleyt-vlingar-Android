package com.ngusta.cupassist.activity;

import com.google.gson.Gson;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.domain.Team;
import com.ngusta.cupassist.domain.Tournament;
import com.ngusta.cupassist.service.TournamentService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TournamentActivity extends Activity {

    public static final String TOURNAMENT_INTENT = "tournament";

    private Tournament tournament;

    public static void startActivity(Context context, Tournament tournament) {
        Intent intent = new Intent(context, TournamentActivity.class);
        String json = new Gson().toJson(tournament);
        intent.putExtra(TOURNAMENT_INTENT, json);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tournament_view);
        Gson gson = new Gson();
        tournament = gson.fromJson(getIntent().getStringExtra("tournament"), Tournament.class);
        initInfo();
        new RequestTournamentDetailsTask().execute();
    }

    private void initInfo() {
        getActionBar().setTitle(tournament.getName());
        ((TextView) findViewById(R.id.level)).setText(tournament.getLevelString());
        ((TextView) findViewById(R.id.club)).setText(tournament.getClub());
        ((TextView) findViewById(R.id.period)).setText(tournament.getCompetitionPeriod().getName());
        ((TextView) findViewById(R.id.start_date)).setText(tournament.getFormattedStartDate());
    }

    private class RequestTournamentDetailsTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            new TournamentService(TournamentActivity.this).loadTournamentDetails(tournament);
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            ((TextView) findViewById(R.id.classes))
                    .setText(tournament.getClassesWithMaxNumberOfTeamsString());
            initTeams();
        }
    }

    private void initTeams() {
        ListView teamListView = (ListView) findViewById(R.id.teamList);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        teamListView.setAdapter(adapter);
        adapter.setNotifyOnChange(false);
        for (Tournament.TournamentClazz clazz : tournament.getClazzes()) {
            for (Tournament.TeamGroupPosition position : tournament.getTeamGroupPositionsForClazz(clazz)) {
                Team team = position.team;
                adapter.add(String.format("%s. %s (%d)", position.group, team.getNames(), team.getEntryPoints()));
            }
        }
        adapter.notifyDataSetChanged();
    }
}
