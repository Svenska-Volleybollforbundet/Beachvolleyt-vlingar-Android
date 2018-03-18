package com.ngusta.cupassist.adapters;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.Player;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerHolder> {

    private List<Player> players;

    private Clazz clazz;

    public PlayerAdapter(List<Player> players, Clazz clazz) {
        Collections.sort(players, new Player.PlayerComparator(clazz));
        this.players = players;
        this.clazz = clazz;
    }

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
        String text = (position + 1) + " " + players.get(position).getName();
        holder.playerName.setText(text);
        holder.entryPoints.setText(String.valueOf(players.get(position).getEntryPoints(clazz)));
    }

    public class PlayerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView playerName;

        TextView entryPoints;

        private Context context;

        public PlayerHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            playerName = (TextView) itemView.findViewById(R.id.player_name);
            entryPoints = (TextView) itemView.findViewById(R.id.entry_points);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Player player = players.get(position);
                showPlayerInfoDialog(player);
            }
        }

        private void showPlayerInfoDialog(Player player) {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(player.getName());
            alertDialog.setMessage(
                    "Klubb: " + player.getClub() +
                            "\nEntrypoäng: " + player.getEntryPoints(player.getClazz()) +
                            "\nRankingpoäng: " + player.getRankingPoints(player.getClazz()) +
                            "\nMixentrypoäng: " + player.getEntryPoints(Clazz.MIXED) +
                            "\nMixrankingpoäng: " + player.getRankingPoints(Clazz.MIXED));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }
}
