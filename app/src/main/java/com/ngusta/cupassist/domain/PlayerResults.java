package com.ngusta.cupassist.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class PlayerResults implements Serializable {

    private List<TournamentResult> results;

    public PlayerResults() {
        this.results = new ArrayList<>();
    }

    public void addResult(String tp, String year, String name, String points) {
        results.add(new TournamentResult(tp, year, name, points));
    }

    public void markEntryResults() {
        List<TournamentResult> entryResults = new ArrayList<>(results);
        Collections.sort(entryResults, ENTRY_POINTS_COMPARATOR);
        CompetitionPeriod currentPeriod = CompetitionPeriod.findPeriodByDate(new Date());
        int numberOfValidResults = 0;
        for (TournamentResult result : entryResults) {
            if (result.getPeriod().isValidAsEntryForPeriod(currentPeriod)) {
                result.setPartOfEntry(true);
                numberOfValidResults++;
            }
            if (numberOfValidResults == 5) {
                break;
            }
        }
    }

    public int getEntryForPeriod(CompetitionPeriod period) {
        List<TournamentResult> entryResults = new ArrayList<>(results);
        Collections.sort(entryResults, ENTRY_POINTS_COMPARATOR);
        int entry = 0;
        int numberOfValidResults = 0;
        for (TournamentResult result : entryResults) {
            if (result.getPeriod().isValidAsEntryForPeriod(period)) {
                entry += result.getPoints();
                numberOfValidResults++;
            }
            if (numberOfValidResults == 5) {
                break;
            }
        }
        return entry;
    }

    private static final Comparator<TournamentResult> ENTRY_POINTS_COMPARATOR = new Comparator<TournamentResult>() {
        @Override
        public int compare(TournamentResult lhs, TournamentResult rhs) {
            return (rhs.points < lhs.points) ? -1 : ((rhs.points == lhs.points) ? 0 : 1);
        }
    };

    public List<TournamentResult> getTournamentResults() {
        return results;
    }

    private class TournamentResult implements Serializable {

        private CompetitionPeriod period;

        private String name;

        private int points;

        private boolean partOfEntry;

        TournamentResult(String period, String year, String name, String points) {
            this.period = CompetitionPeriod.findByName(period, Integer.parseInt(year));
            this.name = name;
            this.points = Integer.parseInt(points.replace(".00", ""));
        }

        @Override
        public String toString() {
            return period.toString() + ": " + points + (partOfEntry ? "*" : "") + "\n";
        }

        void setPartOfEntry(boolean partOfEntry) {
            this.partOfEntry = partOfEntry;
        }

        CompetitionPeriod getPeriod() {
            return period;
        }

        public int getPoints() {
            return points;
        }
    }

    @Override
    public String toString() {
        return print(results);
    }

    public static String print(List<TournamentResult> results) {
        String res = "";
        for (TournamentResult result : results) {
            res += result.toString();
        }
        return res;
    }
}
