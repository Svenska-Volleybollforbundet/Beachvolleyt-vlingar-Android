package com.ngusta.cupassist.activity;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.domain.RankingScale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class RankingTableActivity extends Activity {

    public static final String INTENT_PARAM_STARS = "stars";

    public static final String INTENT_PARAM_CLAZZ_ONE_TEAMS = "teams1";

    public static final String INTENT_PARAM_CLAZZ_TWO_TEAMS = "teams2";

    public static final String INTENT_PARAM_CLAZZ_ONE_NAME = "clazzName1";

    public static final String INTENT_PARAM_CLAZZ_TWO_NAME = "clazzName2";

    public static final String INTENT_PARAM_ACTIVITY_TITLE = "title";

    public static final String INTENT_PARAM_ACITVITY_TITLE_COLOR = "color";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_table);

        Intent intent = getIntent();

        getActionBar().setTitle("Rankingtabell");
        getActionBar().setBackgroundDrawable(new ColorDrawable(intent.getIntExtra(INTENT_PARAM_ACITVITY_TITLE_COLOR, 2)));

        int stars = intent.getIntExtra(INTENT_PARAM_STARS, 0);
        int numberOfTeamsClazzOne = intent.getIntExtra(INTENT_PARAM_CLAZZ_ONE_TEAMS, 0);
        String nameClazzOne = intent.getStringExtra(INTENT_PARAM_CLAZZ_ONE_NAME);
        int numberOfTeamsClazzTwo = intent.getIntExtra(INTENT_PARAM_CLAZZ_TWO_TEAMS, 0);
        String nameClazzTwo = intent.getStringExtra(INTENT_PARAM_CLAZZ_TWO_NAME);

        TableLayout topTable = (TableLayout) findViewById(R.id.top_table);
        createTable(topTable, RankingScale.getRankingScale(stars, numberOfTeamsClazzOne));
        ((TextView) findViewById(R.id.top_table_title)).setText(nameClazzOne + " - " + numberOfTeamsClazzOne + " lag");

        TableLayout bottomTable = (TableLayout) findViewById(R.id.bottom_table);
        if (onlyOneClazz(numberOfTeamsClazzTwo)) {
            bottomTable.setVisibility(View.GONE);
        } else {
            createTable(bottomTable, RankingScale.getRankingScale(stars, numberOfTeamsClazzTwo));
            ((TextView) findViewById(R.id.bottom_table_title)).setText(nameClazzTwo + " - " + numberOfTeamsClazzTwo + " lag");
        }
    }

    private boolean onlyOneClazz(int numberOfTeamsClazzTwo) {
        return numberOfTeamsClazzTwo == 0;
    }

    private void createTable(TableLayout table, ArrayList<RankingScale.PositionPoint> rankingScale) {
        for (RankingScale.PositionPoint pp : rankingScale) {
            TableRow tableRow = new TableRow(this);

            TextView positionTextView = new TextView(this);
            positionTextView.setText(String.format("%d", pp.getPosition()));
            positionTextView.setPadding(3, 3, 3, 3);
            positionTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            positionTextView.setTextSize(25);
            tableRow.addView(positionTextView);

            TextView pointsTextView = new TextView(this);
            pointsTextView.setText(String.format("%d", pp.getPoints()));
            pointsTextView.setPadding(3, 3, 3, 3);
            pointsTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            pointsTextView.setTextSize(25);
            tableRow.addView(pointsTextView);

            table.addView(tableRow);
        }
    }
}
