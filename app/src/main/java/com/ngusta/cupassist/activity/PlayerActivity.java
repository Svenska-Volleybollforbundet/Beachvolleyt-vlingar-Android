package com.ngusta.cupassist.activity;

import com.alespero.expandablecardview.ExpandableCardView;
import com.ngusta.cupassist.R;
import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.domain.PlayerResults;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class PlayerActivity extends AppCompatActivity {

    public static final String INTENT_PLAYER = "player";

    private Player player;

    public static void startActivity(Context context, Player player) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("player", player);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_view);
        player = (Player) getIntent().getSerializableExtra(INTENT_PLAYER);
        initToolbar();

        initInfoCard();

        initResultCard();

        initMixedResultCard();


    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        if (player.getAge() != null) {
            toolbar.setTitle(player.getName() + ", " + player.getAge() + " år");
        } else {
            toolbar.setTitle(player.getName());
        }
        setSupportActionBar(toolbar);
    }

    private void initInfoCard() {
        ExpandableCardView playerInfoCard = findViewById(R.id.player_info);
        playerInfoCard.setTitle("Om " + player.getFirstName());

        TextView playerInfo = findViewById(R.id.info);
        playerInfo.setText("Spelar för " + player.getClub() + ".\n"
                + "Har " + player.getEntryPoints() + " entrypoäng i " + (player.getClazz() == Clazz.MEN ? "herrklassen" : "damklassen")
                + " (rankad " + player.getEntryRank() + ")"
                + " och " + player.getMixedEntryPoints() + " i mixedklassen (rankad " + player.getMixedEntryRank() + ").");
    }

    private void initResultCard() {
        TextView playerResultsText = findViewById(R.id.player_results_text);
        playerResultsText.setText(PlayerResults.print(player.getResults().getTournamentResults()));
    }

    private void initMixedResultCard() {
        TextView playerMixedResultsText = findViewById(R.id.player_results_mixed_text);
        playerMixedResultsText.setText(PlayerResults.print(player.getMixedResults().getTournamentResults()));
    }
}
