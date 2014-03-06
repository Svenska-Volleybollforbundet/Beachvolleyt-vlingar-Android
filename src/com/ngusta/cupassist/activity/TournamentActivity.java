package com.ngusta.cupassist.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.ngusta.cupassist.R;
import com.ngusta.cupassist.domain.Team;
import com.ngusta.cupassist.domain.Tournament;

import java.util.ArrayList;

public class TournamentActivity extends Activity {

    private Tournament tournament;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tournament_view);

        int tournamentIndex = getIntent().getIntExtra("tournamentIndex", 0);
        tournament = ((MyApplication) getApplication()).getTournamentListCache().getTournaments().get(tournamentIndex);

        initLayout();
    }

    private void initLayout() {
        ((TextView) findViewById(R.id.name)).setText(tournament.getName());
        ((TextView) findViewById(R.id.level)).setText(tournament.getLevel());
        ((TextView) findViewById(R.id.classes)).setText(tournament.getClasses());
        ((TextView) findViewById(R.id.club)).setText(tournament.getClub());
        ((TextView) findViewById(R.id.period)).setText(tournament.getPeriod());
        ((TextView) findViewById(R.id.start_date)).setText(tournament.getFormattedStartDate());

        ListView teamListView = (ListView) findViewById(R.id.teamList);
        ArrayList<String> teams = new ArrayList<>();
        if (tournament.getTeams() != null) {
            int group = 0;
            int operator = 1;
            for (Team team : tournament.getSeededTeams()) {
                group += operator;
                if (group == 5) {
                    group = 4;
                    operator = -1;
                } else if (group == 0) {
                    group = 1;
                    operator = 1;
                }
                teams.add(group + " " + team.getNames() + " " + team.getEntryPoints());
            }
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, teams);
        teamListView.setAdapter(adapter);
    }
}
