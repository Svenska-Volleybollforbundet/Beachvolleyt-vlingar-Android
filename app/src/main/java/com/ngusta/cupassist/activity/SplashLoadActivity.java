package com.ngusta.cupassist.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.ngusta.cupassist.R;
import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.domain.Tournament;

import java.util.List;
import java.util.Set;

public class SplashLoadActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        new RequestTournamentTask().execute();
    }

    private class RequestTournamentTask extends AsyncTask<Void, String, List<Tournament>> {
        @Override
        protected List<Tournament> doInBackground(Void... voids) {
            MyApplication myApplication = (MyApplication) getApplication();
            Set<Player> players = myApplication.getPlayerListCache().getPlayers();
            List<Tournament> tournaments = myApplication.getTournamentListCache().getTournaments();
            for (Tournament tournament : tournaments) {
                myApplication.getTournamentListCache().getTeams(tournament, players);
            }
            return tournaments;
        }

        @Override
        protected void onPostExecute(List<Tournament> tournaments) {
            super.onPostExecute(tournaments);
            startActivity(new Intent(SplashLoadActivity.this, TournamentListActivity.class));
        }
    }
}
