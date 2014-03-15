package com.ngusta.cupassist.domain;

import java.io.Serializable;

public class Player implements Serializable {
    private Integer rank;
    private String firstName;
    private String lastName;
    private String club;
    private int rankingPoints;
    private int entryPoints;

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

    public int getEntryPoints() {
        return entryPoints;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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

    public String getClub() {
        return club;
    }

    public int getRankingPoints() {
        return rankingPoints;
    }
}
