package com.ngusta.cupassist.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Pair;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.ngusta.cupassist.R;
import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.Team;
import com.ngusta.cupassist.domain.Tournament;

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
        ((TextView) findViewById(R.id.level)).setText(tournament.getLevelString());
        ((TextView) findViewById(R.id.classes)).setText(tournament.getClassesWithMaxNumberOfTeamsString());
        ((TextView) findViewById(R.id.club)).setText(tournament.getClub());
        ((TextView) findViewById(R.id.period)).setText(tournament.getPeriod());
        ((TextView) findViewById(R.id.start_date)).setText(tournament.getFormattedStartDate());

        ListView teamListView = (ListView) findViewById(R.id.teamList);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        teamListView.setAdapter(adapter);
        adapter.setNotifyOnChange(false);

        for (Clazz clazz : tournament.getClazzes()) {
            for (Pair<Integer, Team> groupedTeam : tournament.getGroupedTeamsForClazz(clazz)) {
                Team team = groupedTeam.second;
                adapter.add(String.format("%s. %s (%d)", groupedTeam.first, team.getNames(), team.getEntryPoints()));
            }
        }

        adapter.notifyDataSetChanged();
    }
}
