package com.ngusta.cupassist.domain;

import java.io.Serializable;

public class Player implements Serializable {
    Integer rank;
    String firstName;
    String lastName;
    String club;
    int rankPoints;
    int entryPoints;

    public Player(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Player(Integer rank, String firstName, String lastName, String club, int rankPoints, int entryPoints) {
        this.rank = rank;
        this.firstName = firstName;
        this.lastName = lastName;
        this.club = club;
        this.rankPoints = rankPoints;
        this.entryPoints = entryPoints;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return rank + " " +
                firstName + " " +
                lastName + " " +
                club + " " +
                rankPoints + " " +
                entryPoints;
    }
}
