package com.ngusta.cupassist.activity;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.adapters.TeamAdapter;
import com.ngusta.cupassist.adapters.TournamentListAdapter;
import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.domain.Team;
import com.ngusta.cupassist.domain.Tournament;
import com.ngusta.cupassist.io.TournamentListCache;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class TournamentActivity extends AppCompatActivity {

    public static final String TOURNAMENT_INTENT = "tournamentId";

    private View mProgressContainer;

    private View mTeamList;

    private boolean mListShown;

    private Tournament tournament;

    private Menu menu;

    private CommonActivityHelper commonActivityHelper;

    public static void startActivity(Context context, Tournament tournament) {
        Intent intent = new Intent(context, TournamentActivity.class);
        intent.putExtra(TOURNAMENT_INTENT, tournament.getId());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament);

        commonActivityHelper = new CommonActivityHelper(this);
        commonActivityHelper.initToolbarAndNavigation();

        tournament = ((MyApplication) getApplication()).getTournamentService().getTournamentById(getIntent().getIntExtra(TOURNAMENT_INTENT, -1));
        mProgressContainer = findViewById(R.id.progressContainer);
        mTeamList = findViewById(R.id.teamList);
        setListShown(false, false);
        initInfo();
        makeActionOverflowMenuShown();
        new RequestTournamentDetailsTask().execute();
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

    private void setListShown(boolean shown, boolean animate) {
        if (mListShown == shown) {
            return;
        }
        mListShown = shown;
        if (shown) {
            if (animate) {
                mProgressContainer.startAnimation(
                        AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                mTeamList
                        .startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            } else {
                mProgressContainer.clearAnimation();
                mTeamList.clearAnimation();
            }
            mProgressContainer.setVisibility(View.GONE);
            mTeamList.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mProgressContainer
                        .startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
                mTeamList.startAnimation(
                        AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
            } else {
                mProgressContainer.clearAnimation();
                mTeamList.clearAnimation();
            }
            mProgressContainer.setVisibility(View.VISIBLE);
            mTeamList.setVisibility(View.GONE);
        }
    }

    private void initInfo() {
        getSupportActionBar().setTitle(tournament.getName());
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(
                TournamentListAdapter.getLevelIndicatorResource(tournament))));
        ((TextView) findViewById(R.id.club)).setText(tournament.getClub());
        ((TextView) findViewById(R.id.start_date)).setText(tournament.getFormattedStartDate());
    }

    private void makeActionOverflowMenuShown() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
        }
    }

    private class RequestTournamentDetailsTask extends AsyncTask<Void, String, Exception> {

        @Override
        protected Exception doInBackground(Void... voids) {
            try {
                ((MyApplication) getApplication()).getTournamentService().loadTournamentDetails(tournament);
            } catch (IOException e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception exception) {
            if (exception == null && tournament.isRegistrationOpen()) {
                initClazzSpinner();
                Tournament.TournamentClazz clazz = tournament.getClazzes().get(0);
                setTeamsInfo(clazz);
                updateTeams(clazz);
                enableMenu();
            } else if (exception != null) {
                Toast.makeText(TournamentActivity.this, "Kunde inte ladda lag. Kolla din internetanslutning.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(TournamentActivity.this, "Registreringen har inte öppnat.", Toast.LENGTH_LONG).show();
            }
            setListShown(true, true);
        }
    }

    private void enableMenu() {
        menu.findItem(R.id.menu_item_see_result_icon).setEnabled(true).getIcon().setAlpha(255);
        menu.findItem(R.id.menu_item_info).setEnabled(true);
        menu.findItem(R.id.menu_item_see_result).setEnabled(true);
        menu.findItem(R.id.menu_item_sign_up).setEnabled(true);
    }

    private void initClazzSpinner() {
        if (tournament.getClazzes().size() <= 1) {
            return;
        }
        Spinner clazzSpinner = findViewById(R.id.clazz_spinner);
        clazzSpinner.setVisibility(View.VISIBLE);
        ArrayAdapter<Tournament.TournamentClazz> adapter = new ArrayAdapter<>(this, R.layout.clazz_spinner_item);
        adapter.setDropDownViewResource(R.layout.clazz_spinner_dropdown_item);
        adapter.addAll(tournament.getClazzes());
        clazzSpinner.setAdapter(adapter);
        clazzSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Tournament.TournamentClazz clazz = (Tournament.TournamentClazz) parent.getSelectedItem();
                setTeamsInfo(clazz);
                updateTeams(clazz);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setTeamsInfo(Tournament.TournamentClazz clazz) {
        Integer maxNumberOfTeams = clazz.getMaxNumberOfTeams();
        ((TextView) findViewById(R.id.teams_info)).setText(
                getResources().getString(R.string.registered) + " " + tournament.getNumberOfCompleteTeamsForClazz(clazz) + "/" + (
                        maxNumberOfTeams == 1000 ? "?" : maxNumberOfTeams));
    }

    private void updateTeams(Tournament.TournamentClazz clazz) {
        ListView teamListView = findViewById(R.id.teamList);
        List<Tournament.TeamGroupPosition> teams = tournament.getTeamGroupPositionsForClazz(clazz);
        final ArrayAdapter<Tournament.TeamGroupPosition> adapter = new TeamAdapter(this, R.layout.team_list_item, teams, clazz.getMaxNumberOfTeams());
        teamListView.setAdapter(adapter);
        teamListView.setOnItemClickListener(new OnTeamClickListener(this, teams));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem menuItem = menu.add(Menu.NONE, R.id.menu_item_see_result_icon, Menu.NONE, R.string.see_result)
                .setIcon(R.drawable.external_link_enabled)
                .setEnabled(false);
        menuItem.getIcon().setAlpha(100);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menuItem = menu.add(Menu.NONE, R.id.menu_item_info, Menu.NONE, R.string.see_info)
                .setEnabled(false);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        menuItem = menu.add(Menu.NONE, R.id.menu_item_sign_up, Menu.NONE, R.string.sign_up)
                .setEnabled(false);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        menuItem = menu.add(Menu.NONE, R.id.menu_item_see_result, Menu.NONE, R.string.see_result)
                .setEnabled(false);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        menuItem = menu.add(Menu.NONE, R.id.menu_item_register_result, Menu.NONE, R.string.register_result);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        menuItem = menu.add(Menu.NONE, R.id.menu_item_ranking_points, Menu.NONE, R.string.see_ranking_points);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        //TODO enable
        //menuItem = menu.add(Menu.NONE, R.id.menu_item_see_result_in_app, Menu.NONE, R.string.see_result_in_app);
        //menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_item_see_result:
            case R.id.menu_item_see_result_icon:
                openBrowser(TournamentListCache.PROFIXIO_BASE_RESULT_URL + tournament.getUrlName());
                return true;
            case R.id.menu_item_register_result:
                openBrowser(TournamentListCache.PROFIXIO_BASE_RESULT_REPORTING_URL + tournament.getUrlName());
                return true;
            case R.id.menu_item_info:
                openBrowser(TournamentListCache.CUP_ASSIST_BASE_URL + "fx/" + tournament.getUrl());
                return true;
            case R.id.menu_item_sign_up:
                openBrowser(TournamentListCache.CUP_ASSIST_TOURNAMENT_URL + tournament.getUrlName());
                return true;
            case R.id.menu_item_ranking_points:
                Intent intent = new Intent(this, RankingTableActivity.class);

                intent.putExtra(RankingTableActivity.INTENT_PARAM_ACTIVITY_TITLE, tournament.getName());
                intent.putExtra(RankingTableActivity.INTENT_PARAM_ACTIVITY_TITLE_COLOR,
                        getResources().getColor(TournamentListAdapter.getLevelIndicatorResource(tournament)));
                intent.putExtra(RankingTableActivity.INTENT_PARAM_STARS, tournament.getLevel().getStars());
                int teams = Math.min(tournament.getClazzes().get(0).getMaxNumberOfTeams(),
                        tournament.getNumberOfCompleteTeamsForClazz(tournament.getClazzes().get(0)));
                intent.putExtra(RankingTableActivity.INTENT_PARAM_CLAZZ_ONE_TEAMS, teams);
                intent.putExtra(RankingTableActivity.INTENT_PARAM_CLAZZ_ONE_NAME, tournament.getClazzes().get(0).getClazz().toString());
                if (tournament.getClazzes().size() > 1) {
                    teams = Math.min(tournament.getClazzes().get(1).getMaxNumberOfTeams(),
                            tournament.getNumberOfCompleteTeamsForClazz(tournament.getClazzes().get(1)));
                    intent.putExtra(RankingTableActivity.INTENT_PARAM_CLAZZ_TWO_TEAMS, teams);
                    intent.putExtra(RankingTableActivity.INTENT_PARAM_CLAZZ_TWO_NAME, tournament.getClazzes().get(1).getClazz().toString());
                }
                startActivity(intent);
                return true;
            case R.id.menu_item_see_result_in_app:
                try {
                    List<String> matches = ((MyApplication) getApplication()).getTournamentService().getResult(tournament);
                    String tot = "";
                    for (String res : matches) {
                        tot += res + "\n";
                    }
                    new AlertDialog.Builder(this)
                            .setMessage(tot)
                            .show();
                    return true;
                } catch (IOException e) {
                    new AlertDialog.Builder(this)
                            .setMessage("Oops")
                            .show();
                }
        }
        return false;
    }

    private void openBrowser(String uriString) {
        Uri cupAssistUri = Uri.parse(uriString);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, cupAssistUri);
        startActivity(browserIntent);
    }

    private class OnTeamClickListener implements AdapterView.OnItemClickListener {

        private final Context context;

        private final List<Tournament.TeamGroupPosition> teams;

        OnTeamClickListener(Context context, List<Tournament.TeamGroupPosition> teams) {
            this.context = context;
            this.teams = teams;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Team team = teams.get(position).team;
            final Player[] players = new Player[]{team.getPlayerA(), team.getPlayerB()};
            String[] options = new String[]{team.getPlayerA().getName(), team.getPlayerB().getName()};

            AlertDialog.Builder builder = new AlertDialog.Builder(TournamentActivity.this, R.style.MyDialogTheme);
            builder.setTitle("Välj spelare:");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startPlayerActivity(players[which]);
                }
            });
            builder.show();
        }

        private void startPlayerActivity(Player player) {
            boolean hasPlayerDetails = ((MyApplication) getApplication()).getPlayerService().updatePlayerWithDetailsAndResults(player);
            if (!hasPlayerDetails) {
                Toast.makeText(getBaseContext(), "Finns ingen mer data om den här spelaren.", Toast.LENGTH_SHORT).show();
            } else {
                PlayerActivity.startActivity(context, player);
            }
        }
    }
}
