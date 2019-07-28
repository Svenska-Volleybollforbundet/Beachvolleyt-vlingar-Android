package com.ngusta.cupassist.adapters;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.Tournament;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import static java.util.Arrays.asList;

public class TeamAdapter extends ArrayAdapter<Tournament.TeamGroupPosition> {

    private final List<Tournament.TeamGroupPosition> teams;

    private final int resource;

    private final int maxNumberOfTeams;

    public TeamAdapter(Context context, int resource, List<Tournament.TeamGroupPosition> teams, int maxNumberOfTeams) {
        super(context, resource, teams);
        this.resource = resource;
        this.teams = teams;
        this.maxNumberOfTeams = maxNumberOfTeams;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TeamHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);

            holder = new TeamHolder();
            holder.team = row.findViewById(R.id.team);
            holder.group = row.findViewById(R.id.group);
            holder.playerAName = row.findViewById(R.id.playerA_name);
            holder.playerBName = row.findViewById(R.id.playerB_name);
            holder.playersPoints = row.findViewById(R.id.player_entry_points);
            holder.entryPoints = row.findViewById(R.id.team_entry_points);

            row.setTag(holder);
        } else {
            holder = (TeamHolder) row.getTag();
        }

        Tournament.TeamGroupPosition team = teams.get(position);
        if (team.team.hasPaid()) {
            holder.team.setBackgroundColor(0x2000c800);
        } else {
            holder.team.setBackgroundColor(Color.WHITE);
        }
        Clazz[] clazzesToDisplayGroupAndPointsFor = {Clazz.MEN, Clazz.WOMEN, Clazz.MIXED};
        boolean displayGroupAndPoints = asList(clazzesToDisplayGroupAndPointsFor).contains(team.team.getClazz());
        if (position < maxNumberOfTeams && displayGroupAndPoints) {
            holder.group.setText("" + team.group);
        } else {
            holder.group.setText("");
        }
        holder.playerAName.setText(team.team.getPlayerA().getName());
        holder.playerBName.setText(team.team.getPlayerB().getName());
        int playerAEntryPoints = team.team.getPlayerA().getEntryPoints(team.team.getClazz());
        int playerBEntryPoints = team.team.getPlayerB().getEntryPoints(team.team.getClazz());
        if (displayGroupAndPoints) {
            holder.playersPoints.setText("(" + playerAEntryPoints + "/" + playerBEntryPoints + ")");
            holder.entryPoints.setText(((int) Math.round(team.team.getEntryPoints())) + " pts");
        } else {
            holder.playersPoints.setText("");
            holder.entryPoints.setText("");
        }
        return row;
    }

    static class TeamHolder {

        RelativeLayout team;

        TextView group;

        TextView playerAName;

        TextView playerBName;

        TextView playersPoints;

        TextView entryPoints;
    }
}
