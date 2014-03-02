package com.ngusta.cupassist.domain;

import java.io.Serializable;

public class Player implements Serializable {
    private Integer rank;
    private String firstName;
    private String lastName;
    private String club;
    private int ranking;
    private int entry;

    public Player(String firstName, String lastName, String playerClub) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.club = playerClub;
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

    public String getNameAndClub() {
        return firstName + " " + lastName + " " + club;
    }

    public int getEntry() {
        return entry;
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
                ranking + " " +
                entry;
    }

    public String getClub() {
        return club;
    }
}
