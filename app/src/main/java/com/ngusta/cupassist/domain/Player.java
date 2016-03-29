package com.ngusta.cupassist.domain;

import java.io.Serializable;

public class Player implements Serializable {
    private Integer rank;
    private String firstName;
    private String lastName;
    private String club;
    private int rankingPoints;
    private int entryPoints;

    private int mixRankingPoints;

    private int mixEntryPoints;

    public Player(String firstName, String lastName, String playerClub) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.club = playerClub;
    }

    public Player(Integer rank, String firstName, String lastName, String club, int rankingPoints, int entryPoints) {
        this.rank = rank;
        this.firstName = firstName;
        this.lastName = lastName;
        this.club = club;
        this.rankingPoints = rankingPoints;
        this.entryPoints = entryPoints;
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

    public int getMixEntryPoints() {
        return mixEntryPoints;
    }

    public void setMixEntryPoints(int mixEntryPoints) {
        this.mixEntryPoints = mixEntryPoints;
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

    public String getClub() {
        return club;
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
}
