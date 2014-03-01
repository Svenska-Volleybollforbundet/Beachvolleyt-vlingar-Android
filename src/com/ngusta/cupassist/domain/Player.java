package com.ngusta.cupassist.domain;

import java.io.Serializable;

public class Player implements Serializable {
    Integer rank;
    String firstName;
    String lastName;
    String club;
    int ranking;
    int entry;

    public Player(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Player(Integer rank, String firstName, String lastName, String club, int ranking, int entry) {
        this.rank = rank;
        this.firstName = firstName;
        this.lastName = lastName;
        this.club = club;
        this.ranking = ranking;
        this.entry = entry;
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
                ranking + " " +
                entry;
    }
}
