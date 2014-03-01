package com.ngusta.cupassist.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.ngusta.cupassist.R;
import com.ngusta.cupassist.domain.Tournament;
import com.ngusta.cupassist.io.TournamentListCache;

import java.util.ArrayList;

public class SplashLoadActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        new RequestTask().execute();
    }

    private class RequestTask extends AsyncTask<Void, String, ArrayList<Tournament>> {
        @Override
        protected ArrayList<Tournament> doInBackground(Void... voids) {
            return ((MyApplication) getApplication()).getTournamentListCache().getTournaments();
        }

        @Override
        protected void onPostExecute(ArrayList<Tournament> tournaments) {
            super.onPostExecute(tournaments);
            startActivity(new Intent(SplashLoadActivity.this, TournamentListActivity.class));
        }
    }
}
