package com.ngusta.cupassist.adapters;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.domain.Tournament;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
        ViewGroup.LayoutParams layoutParams = viewHolder.levelIcon.getLayoutParams();
        layoutParams.width = layoutParams.height = viewHolder.name.getLineHeight();
        viewHolder.levelIcon.setImageDrawable(getLevelIconDrawableRes(tournament.getLevel()));

        return view;
    }

    private Drawable getLevelIconDrawableRes(Tournament.Level level) {
        int id;
        switch (level) {
            case OPEN:
                id = R.drawable.level_open;
                break;
            case OPEN_GREEN:
                id = R.drawable.level_open_green;
                break;
            case CHALLENGER:
                id = R.drawable.level_challenger;
                break;
            case YOUTH:
                id = R.drawable.level_youth;
                break;
            case VETERAN:
                id = R.drawable.level_veteran;
                break;
            case SWEDISH_BEACH_TOUR:
                id = R.drawable.level_sbt;
                break;
            case UNKNOWN:
            default:
                id = R.drawable.level_unknown;
                break;
        }
        return getContext().getResources().getDrawable(id);
    }

    private static final class ViewHolder {

        final TextView name;

        final TextView club;

        final TextView startDate;

        final ImageView levelIcon;

        private ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.name);
            club = (TextView) view.findViewById(R.id.club);
            startDate = (TextView) view.findViewById(R.id.start_date);
            levelIcon = (ImageView) view.findViewById(R.id.level_icon);
        }
    }
}
