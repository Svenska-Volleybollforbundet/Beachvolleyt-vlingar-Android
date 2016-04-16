package com.ngusta.cupassist.adapters;

import com.ngusta.beachvolley.domain.Player;
import com.ngusta.cupassist.R;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerHolder> {

    private List<Player> players;

    public PlayerAdapter(List<Player> players) {
        this.players = players;
    }

    @Override
    public PlayerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.player_list_item, parent, false);
        return new PlayerHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    @Override
    public void onBindViewHolder(PlayerHolder holder, int position) {
        holder.playerName.setText(players.get(position).getName());
        holder.entryPoints.setText(String.valueOf(players.get(position).getEntryPoints()));
    }

    public class PlayerHolder extends RecyclerView.ViewHolder {

        TextView playerName;

        TextView entryPoints;

        public PlayerHolder(View itemView) {
            super(itemView);
            playerName = (TextView) itemView.findViewById(R.id.player_name);
            entryPoints = (TextView) itemView.findViewById(R.id.entry_points);
        }
    }
}
