package com.ngusta.cupassist.adapters;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.activity.PlayerActivity;
import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.service.PlayerService;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerHolder> implements Filterable {

    private List<Player> originalPlayers;

    private List<Player> players;

    private Clazz clazz;

    private PlayerService playerService;

    public PlayerAdapter(List<Player> players, Clazz clazz, PlayerService playerService) {
        Collections.sort(players, new Player.CurrentEntryPlayerComparator(clazz));
        originalPlayers = new ArrayList<>(players);
        this.players = players;
        this.clazz = clazz;
        this.playerService = playerService;
    }

    @NonNull
    @Override
    public PlayerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.player_list_item, parent, false);
        return new PlayerHolder(itemView, parent.getContext());
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    @Override
    public void onBindViewHolder(PlayerHolder holder, int position) {
        Player player = players.get(position);
        int rank = clazz == Clazz.MIXED ? player.getMixedEntryRank() : player.getEntryRank();
        String text = rank + " " + player.getName();
        holder.playerName.setText(text);
        holder.entryPoints.setText(String.valueOf(player.getEntryPoints(clazz)));
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String filterText = charSequence.toString().toLowerCase();
                List<Player> filteredPlayers = new ArrayList<>();
                for (Player player : originalPlayers) {
                    if (player.getName().toLowerCase().contains(filterText)) {
                        filteredPlayers.add(player);
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredPlayers;
                results.count = filteredPlayers.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                players = (List<Player>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class PlayerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView playerName;

        TextView entryPoints;

        private Context context;

        public PlayerHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            playerName = itemView.findViewById(R.id.player_name);
            entryPoints = itemView.findViewById(R.id.entry_points);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Player player = players.get(position);
                playerService.updatePlayerWithDetailsAndResults(player);
                PlayerActivity.startActivity(context, player);
            }
        }
    }
}
