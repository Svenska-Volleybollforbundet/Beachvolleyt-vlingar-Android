package com.ngusta.cupassist.activity;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.adapters.TournamentListAdapter;
import com.ngusta.cupassist.domain.CompetitionPeriod;
import com.ngusta.cupassist.domain.Tournament;
import com.ngusta.cupassist.service.TournamentService;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

import java.util.Date;
import java.util.List;

public class TournamentListActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new RequestTournamentsTask().execute();
        setContentView(R.layout.activity_tournament_list);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        ListAdapter adapter = l.getAdapter();
        if (adapter.getItemViewType(position) != TournamentListAdapter.VIEW_TYPE_TOURNAMENT) {
            return;
        }
        Tournament item = (Tournament) adapter.getItem(position);
        TournamentActivity.startActivity(this, item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, R.id.menu_item_today, Menu.NONE, R.string.current_competition_period)
                .setIcon(android.R.drawable.ic_menu_today)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.menu_item_today) {
            selectCurrentCompetitionPeriod();
            return true;
        }
        return false;
    }

    @Override
    public void setListAdapter(ListAdapter adapter) {
        super.setListAdapter(adapter);
        selectCurrentCompetitionPeriod();
    }

    private void selectCurrentCompetitionPeriod() {
        SectionIndexer listAdapter = (SectionIndexer) getListAdapter();
        CompetitionPeriod period = CompetitionPeriod.findPeriodByDate(new Date());
        int position = listAdapter.getPositionForSection(period.getPeriodNumber() - 1);
        setSelection(position);
    }

    private class RequestTournamentsTask extends AsyncTask<Void, String, List<Tournament>> {

        @Override
        protected List<Tournament> doInBackground(Void... voids) {
            return new TournamentService(TournamentListActivity.this).getAllTournaments();
        }

        @Override
        protected void onPostExecute(List<Tournament> tournaments) {
            setListAdapter(new TournamentListAdapter(TournamentListActivity.this, tournaments));
        }
    }
}