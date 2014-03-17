package com.ngusta.cupassist.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.gson.Gson;
import com.ngusta.cupassist.R;
import com.ngusta.cupassist.domain.Tournament;
import com.ngusta.cupassist.service.TournamentService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TournamentListActivity extends Activity {

    private List<Tournament> tournaments;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tournament_listview);
        listView = (ListView) findViewById(R.id.listview);
        new RequestTournamentsTask().execute();
    }

    private class RequestTournamentsTask extends AsyncTask<Void, String, List<Tournament>> {
        @Override
        protected List<Tournament> doInBackground(Void... voids) {
            return new TournamentService(TournamentListActivity.this).getTournamentsFromCurrentCompetitionPeriod();
        }

        @Override
        protected void onPostExecute(List<Tournament> tournaments) {
            super.onPostExecute(tournaments);
            TournamentListActivity.this.tournaments = tournaments;
            final ArrayList<String> list = new ArrayList<>();
            for (Tournament tournament : tournaments) {
                list.add(tournament.getName());
            }
            final StableArrayAdapter adapter = new StableArrayAdapter(TournamentListActivity.this,
                    android.R.layout.simple_list_item_1, list);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    Gson gson = new Gson();
                    Intent intent = new Intent(TournamentListActivity.this, TournamentActivity.class);
                    intent.putExtra("tournament", gson.toJson(TournamentListActivity.this.tournaments.get(position)));
                    startActivity(intent);
                }
            });
        }
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {
        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}