package com.ngusta.cupassist.adapters;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.domain.Tournament;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TournamentListAdapter extends ArrayAdapter<Tournament> {

    private LayoutInflater mInflater;

    public TournamentListAdapter(Context context, List<Tournament> objects) {
        super(context, 0, objects);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        Tournament tournament = getItem(position);

        if (convertView == null) {
            view = mInflater.inflate(R.layout.tournament_list_item, parent, false);
            view.setTag(viewHolder = new ViewHolder(view));
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.name.setText(tournament.getName());
        viewHolder.club.setText(tournament.getClub());
        viewHolder.startDate.setText(
                DateUtils.formatDateTime(getContext(), tournament.getStartDate().getTime(), 0));

        return view;
    }

    private static final class ViewHolder {

        final TextView name;

        final TextView club;

        final TextView startDate;

        private ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.name);
            club = (TextView) view.findViewById(R.id.club);
            startDate = (TextView) view.findViewById(R.id.start_date);
        }
    }
}
