package com.ngusta.cupassist.domain;

import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Comparator;

import androidx.annotation.NonNull;

public class Player implements Serializable {

    private Integer rank;
    private int entryRank;
    private int mixedEntryRank;
    private final String firstName;
    private final String lastName;
    private final String club;
    private int entryPoints;
    private int mixedEntryPoints;
    private String playerId;
    private String age;
    private PlayerResults results;
    private PlayerResults mixedResults;
    private Clazz clazz;

    public Player(String firstName) {
        this.firstName = firstName;
        this.lastName = "";
        this.club = "";
    }

    public Player(String firstName, String lastName, String playerClub, int points, Clazz clazz) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.club = playerClub;
        this.entryPoints = points;
        this.clazz = clazz;
    }

    public Player(Integer rank, String firstName, String lastName, String club, int rankingPoints, int entryPoints, String playerId, String clazz) {
        this.rank = rank;
        this.entryRank = rank;
        this.firstName = firstName;
        this.lastName = lastName;
        this.club = club;
        this.entryPoints = entryPoints;
        this.playerId = playerId;
        this.clazz = Clazz.parse(clazz);
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getMixedOnlyEntryPoints() {
        return mixedEntryPoints - (int) Math.round(entryPoints * 0.1);
    }

    public int getMixedPoints() {
        return mixedEntryPoints;
    }

    public void setMixedEntryPoints(int mixedEntryPoints) {
        this.mixedEntryPoints = mixedEntryPoints;
    }

    public int getEntryPoints() {
        return entryPoints;
    }

    public void setEntryPoints(int entryPoints) {
        this.entryPoints = entryPoints;
    }

    public int getEntryPoints(Clazz clazz) {
        if (clazz == Clazz.MIXED) {
            return mixedEntryPoints;
        }
        return entryPoints;
    }

    public String getClub() {
        return club;
    }

    public void setClazz(Clazz clazz) {
        this.clazz = clazz;
    }

    public Clazz getClazz() {
        return clazz;
    }

    public void setEntryRank(int entryRank) {
        this.entryRank = entryRank;
    }

    public int getEntryRank() {
        return entryRank;
    }

    public void setMixedEntryRank(int mixedEntryRank) {
        this.mixedEntryRank = mixedEntryRank;
    }

    public int getMixedEntryRank() {
        return mixedEntryRank;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public PlayerResults getResults(Clazz clazz) {
        if (clazz == Clazz.MIXED) {
            return mixedResults;
        }
        return results;
    }

    public PlayerResults getResults() {
        return results;
    }

    public void setResults(PlayerResults results) {
        results.markEntryResults();
        this.results = results;
    }

    public PlayerResults getMixedResults() {
        return mixedResults;
    }

    public void setMixedResults(PlayerResults mixedResults) {
        mixedResults.markEntryResults();
        this.mixedResults = mixedResults;
    }

    public boolean isOnlyMixedPlayer() {
        return clazz == Clazz.MIXED;
    }

    public String getNameAndClub() {
        return (firstName + " " + lastName + " " + club).replaceAll("/|\\.|#|\\$|\\[|\\]", "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Player player = (Player) o;
        return entryPoints == player.entryPoints &&
                mixedEntryPoints == player.mixedEntryPoints &&
                Objects.equal(firstName, player.firstName) &&
                Objects.equal(lastName, player.lastName) &&
                Objects.equal(club, player.club);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entryPoints, mixedEntryPoints, firstName, lastName, club);
    }

    @NonNull
    @Override
    public String toString() {
        return rank + " " +
                firstName + " " +
                lastName + " " +
                club + " " +
                entryPoints;
    }

    public static class CurrentEntryPlayerComparator implements Comparator<Player> {

        private final Clazz clazz;

        public CurrentEntryPlayerComparator(Clazz clazz) {
            this.clazz = clazz;
        }

        @Override
        public int compare(Player p1, Player p2) {
            int cmp = Double.compare(p2.getEntryPoints(clazz), p1.getEntryPoints(clazz));
            return cmp != 0 ? cmp : p1.getName().compareTo(p2.getName());
        }
    }

    public static class CompetitionPeriodPlayerComparator implements Comparator<Player> {

        private final Clazz clazz;

        private final CompetitionPeriod competitionPeriod;

        public CompetitionPeriodPlayerComparator(Clazz clazz, CompetitionPeriod period) {
            this.clazz = clazz;
            this.competitionPeriod = period;
        }

        @Override
        public int compare(Player p1, Player p2) {
            int cmp = Double
                    .compare(p2.getResults(clazz).getEntryForPeriod(competitionPeriod), p1.getResults(clazz).getEntryForPeriod(competitionPeriod));
            if (cmp != 0) {
                return cmp;
            }
            cmp = Double.compare(p2.getResults(clazz).getRankingPointsForPeriod(competitionPeriod),
                    p1.getResults(clazz).getRankingPointsForPeriod(competitionPeriod));
            return cmp != 0 ? cmp : p1.getName().compareTo(p2.getName());
        }
    }
}
