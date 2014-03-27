package com.ngusta.cupassist.activity;

import com.ngusta.cupassist.adapters.TournamentListAdapter;
import com.ngusta.cupassist.domain.Tournament;
import com.ngusta.cupassist.service.TournamentService;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.List;

public class TournamentListActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new RequestTournamentsTask().execute();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Tournament item = (Tournament) l.getAdapter().getItem(position);
        TournamentActivity.startActivity(this, item);
    }

    private class RequestTournamentsTask extends AsyncTask<Void, String, List<Tournament>> {

        @Override
        protected List<Tournament> doInBackground(Void... voids) {
            return new TournamentService(TournamentListActivity.this)
                    .getTournamentsFromCurrentCompetitionPeriodAndLater();
        }

        @Override
        protected void onPostExecute(List<Tournament> tournaments) {
            setListAdapter(new TournamentListAdapter(TournamentListActivity.this, tournaments));
        }
    }
}