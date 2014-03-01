package com.ngusta.cupassist.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.ngusta.cupassist.R;
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
        ((TextView) findViewById(R.id.level)).setText(tournament.getLevel());
        ((TextView) findViewById(R.id.classes)).setText(tournament.getClasses());
        ((TextView) findViewById(R.id.club)).setText(tournament.getClub());
        ((TextView) findViewById(R.id.period)).setText(tournament.getPeriod());
        ((TextView) findViewById(R.id.start_date)).setText(tournament.getFormattedStartDate());
    }
}
