package com.ngusta.cupassist.domain;

import java.io.Serializable;
import java.util.Comparator;

public class Player implements Serializable {

    private Integer rank;
    private int entryRank;
    private int mixedEntryRank;
    private String firstName;
    private String lastName;
    private String club;
    private int rankingPoints;
    private int entryPoints;
    private int mixRankingPoints;

    private int mixedEntryPoints;

    private String playerId;

    private String age;

    private PlayerResults results;

    private PlayerResults mixedResults;

    private Clazz clazz;

    public Player(String firstName, String lastName, String playerClub) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.club = playerClub;
    }

    public Player(Integer rank, String firstName, String lastName, String club, int rankingPoints, int entryPoints, String playerId) {
        this.rank = rank;
        this.firstName = firstName;
        this.lastName = lastName;
        this.club = club;
        this.rankingPoints = rankingPoints;
        this.entryPoints = entryPoints;
        this.playerId = playerId;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public String getNameAndClub() {
        return firstName + " " + lastName + " " + club;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getMixRankingPoints() {
        return mixRankingPoints;
    }

    public void setMixRankingPoints(int mixRankingPoints) {
        this.mixRankingPoints = mixRankingPoints;
    }

    public int getMixedEntryPoints() {
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

    public int getRankingPoints() {
        return rankingPoints;
    }

    public void setRankingPoints(int rankingPoints) {
        this.rankingPoints = rankingPoints;
    }

    public int getEntryPoints(Clazz clazz) {
        if (clazz == Clazz.MIXED) {
            return (int) Math.round(entryPoints * 0.1) + mixedEntryPoints;
        }
        return entryPoints;
    }

    public int getRankingPoints(Clazz clazz) {
        if (clazz == Clazz.MIXED) {
            return (int) Math.round(rankingPoints * 0.1) + mixRankingPoints;
        }
        return rankingPoints;
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

    public String uniqueIdentifier() {
        return (firstName + " " + lastName + " " + club).replaceAll("/|\\.|#|\\$|\\[|\\]", "");
    }

    @Override
    public String toString() {
        return rank + " " +
                firstName + " " +
                lastName + " " +
                club + " " +
                rankingPoints + " " +
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
            if (cmp != 0) {
                return cmp;
            }
            cmp = Double.compare(p2.getRankingPoints(clazz), p1.getRankingPoints(clazz));
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
