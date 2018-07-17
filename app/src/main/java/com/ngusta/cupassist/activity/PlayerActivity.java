package com.ngusta.cupassist.activity;

import com.alespero.expandablecardview.ExpandableCardView;
import com.ngusta.cupassist.R;
import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.CompetitionPeriod;
import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.domain.PlayerResults;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.widget.TextView;

import java.util.Date;

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
        initFutureEntryCard();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.app_toolbar);
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
        String clazzString = player.getClazz() == Clazz.MEN ? "herrklassen" : "damklassen";
        String playerInfoText = getString(R.string.player_info_general_part, player.getFirstName(),
                player.getClub(), player.getEntryPoints(), clazzString, player.getEntryRank());
        if (player.getMixedEntryRank() > 0) {
            playerInfoText += " " + getString(R.string.player_info_mixed_part, player.getFirstName(), player.getMixedEntryPoints(),
                    player.getEntryPoints(Clazz.MIXED), clazzString, player.getMixedEntryRank());
        } else {
            playerInfoText += ".";
        }
        playerInfo.setText(Html.fromHtml(playerInfoText));
    }

    private void initResultCard() {
        TextView playerResultsText = findViewById(R.id.player_results_text);
        String introText = "* betyder att resultaten är en del av spelarens nuvarande entrypoäng.\n\n";
        playerResultsText.setText(introText + PlayerResults.print(player.getResults().getTournamentResults()));
    }

    private void initMixedResultCard() {
        String introText = "* betyder att resultaten är en del av spelarens nuvarande entrypoäng.\n\n";
        TextView playerMixedResultsText = findViewById(R.id.player_results_mixed_text);
        if (player.getMixedResults().getTournamentResults().isEmpty()) {
            playerMixedResultsText.setText(R.string.no_mixed_results);
        } else {
            playerMixedResultsText.setText(introText + PlayerResults.print(player.getMixedResults().getTournamentResults()));
        }
    }

    private void initFutureEntryCard() {
        TextView playerFutureEntryText = findViewById(R.id.player_future_entry_text);
        CompetitionPeriod currentCP = CompetitionPeriod.findPeriodByDate(new Date());
        int futureCP = currentCP.getPeriodNumber();

        String clazzString = player.getClazz() == Clazz.MEN ? "Herrentry" : "Damentry";
        String text = "Spelarens entrypoäng i kommande tävlingsperioder med nuvarande resultat.\n\n     " + clazzString + " / Mixedentry\n";
        while (futureCP <= 16) {
            CompetitionPeriod period = CompetitionPeriod.findPeriodByNumber(futureCP++);
            int entryForPeriod = player.getResults().getEntryForPeriod(period);
            int mixedEntryForPeriod = player.getMixedResults().getEntryForPeriod(period);
            text += period.getName() + ": " + entryForPeriod + " / " + mixedEntryForPeriod + "\n";
        }
        playerFutureEntryText.setText(text);

    }
}