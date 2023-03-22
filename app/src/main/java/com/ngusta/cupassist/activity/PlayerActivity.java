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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import java.util.Date;

public class PlayerActivity extends AppCompatActivity {

    public static final String INTENT_PLAYER = "player";

    private Player player;

    private CommonActivityHelper commonActivityHelper;

    public static void startActivity(Context context, Player player) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("player", player);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        commonActivityHelper = new CommonActivityHelper(this);
        commonActivityHelper.initToolbarAndNavigation();

        player = (Player) getIntent().getSerializableExtra(INTENT_PLAYER);
        setTitle();
        initInfoCard();
        initResultCard();
        initMixedResultCard();
        initFutureEntryCard();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        commonActivityHelper.syncDrawerToggleState();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setTitle() {
        if (player.getAge() != null) {
            getSupportActionBar().setTitle(player.getName() + ", " + player.getAge() + " år");
        } else {
            getSupportActionBar().setTitle(player.getName());
        }
    }

    private void initInfoCard() {
        ExpandableCardView playerInfoCard = findViewById(R.id.player_info);
        playerInfoCard.setTitle("Om " + player.getFirstName());

        TextView playerInfo = findViewById(R.id.info);
        String clazzString = player.getClazz() == Clazz.MEN ? "herrklassen" : "damklassen";
        String playerInfoText = getString(R.string.player_info_general_part, player.getFirstName(),
                player.getClub(), player.getEntryPoints(), clazzString, player.getEntryRank());
        if (player.getMixedEntryRank() > 0) {
            playerInfoText += " " + getString(R.string.player_info_mixed_part, player.getFirstName(), player.getMixedOnlyEntryPoints(),
                    player.getEntryPoints(Clazz.MIXED), clazzString, player.getMixedEntryRank());
        }
        playerInfoText += "\n";
        playerInfo.setText(Html.fromHtml(playerInfoText));
    }

    private void initResultCard() {
        TextView playerResultsText = findViewById(R.id.player_results_text);
        String introText = "* betyder att resultaten är en del av spelarens nuvarande poäng.\n\n";
        if (player.getResults() == null || player.getResults().getTournamentResults().isEmpty()) {
            playerResultsText.setText(R.string.no_results);
        } else {
            playerResultsText.setText(introText + PlayerResults.print(player.getResults().getTournamentResults()) + "\n");
        }
    }

    private void initMixedResultCard() {
        String introText = "* betyder att resultaten är en del av spelarens nuvarande poäng.\n\n";
        TextView playerMixedResultsText = findViewById(R.id.player_results_mixed_text);
        if (player.getMixedResults() == null || player.getMixedResults().getTournamentResults().isEmpty()) {
            playerMixedResultsText.setText(R.string.no_mixed_results);
        } else {
            playerMixedResultsText.setText(introText + PlayerResults.print(player.getMixedResults().getTournamentResults()) + "\n");
        }
    }

    private void initFutureEntryCard() {
        TextView playerFutureEntryText = findViewById(R.id.player_future_entry_text);
        CompetitionPeriod currentCP = CompetitionPeriod.findPeriodByDate(new Date());
        int futureCPYear = currentCP.getYear();
        int futureCP = currentCP.getPeriodNumber();

        String clazzString = player.getClazz() == Clazz.MEN ? "Herrentry" : "Damentry";
        String text = "Spelarens poäng i kommande tävlingsperioder med nuvarande resultat.\n\n     " + clazzString + " / Mixedentry\n";

        for (int i = 0; i < 10; i++) {
            CompetitionPeriod period = CompetitionPeriod.findPeriodByNumber(futureCP, futureCPYear);
            int entryForPeriod = player.getResults().getEntryForPeriod(period);
            int mixedEntryForPeriod = player.getMixedResults() != null ? player.getMixedResults().getEntryForPeriod(period) : 0;
            text += period.getName() + ": " + entryForPeriod + " / " + mixedEntryForPeriod + "\n";
            if (++futureCP == (CompetitionPeriod.getNumberOfPeriodsThisYear() + 1)) {
                futureCP = 1;
                futureCPYear++;
            }
        }
        text += "\n";
        playerFutureEntryText.setText(text);

    }
}
