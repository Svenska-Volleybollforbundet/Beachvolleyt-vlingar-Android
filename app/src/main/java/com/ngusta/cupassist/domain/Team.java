package com.ngusta.cupassist.domain;

import java.io.Serializable;
import java.util.Date;

import static java.lang.Double.compare;

public class Team implements Serializable, Comparable<Team> {

    private Player playerA;

    private Player playerB;

    private Date registrationTime;

    private Clazz clazz;

    private boolean paid;

    private boolean completeTeam;

    public Team(Player playerA, Player playerB, Date registrationTime, Clazz clazz, boolean paid) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.registrationTime = registrationTime;
        this.clazz = clazz;
        this.completeTeam = true;
        this.paid = paid;
    }

    public Team(Player playerA, Date registrationDate, Clazz clazz, boolean paid) {
        this.playerA = playerA;
        this.playerB = new Player("Partner sökes");
        this.registrationTime = registrationDate;
        this.clazz = clazz;
        this.completeTeam = false;
        this.paid = paid;
    }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public String getNames() {
        return playerA.getName() + "/" + playerB.getName();
    }

    public String getClubs() {
        return playerA.getClub() + "/" + playerB.getClub();
    }

    public double getEntryPoints() {
        if (clazz == Clazz.MIXED) {
            return playerA.getMixedPoints() + playerB.getMixedPoints();
        }
        return playerA.getEntryPoints() + playerB.getEntryPoints();
    }

    @Override
    public int compareTo(Team another) {
        if (completeTeam && !another.completeTeam) {
            return -1;
        } else if (!completeTeam && another.completeTeam) {
            return 1;
        }

        int cmp = compare(another.getEntryPoints(), this.getEntryPoints());
        if (cmp != 0) {
            return cmp;
        }
        cmp = compare(another.getHighestEntryPoints(), this.getHighestEntryPoints());
        return cmp;
    }

    private int getHighestEntryPoints() {
        if (clazz == Clazz.MIXED) {
            return Math.max(playerA.getMixedPoints(), playerB.getMixedPoints());
        }
        return Math.max(playerA.getEntryPoints(), playerB.getEntryPoints());
    }

    public Date getRegistrationTime() {
        return registrationTime;
    }

    public Clazz getClazz() {
        return clazz;
    }

    public boolean isCompleteTeam() {
        return completeTeam;
    }

    public boolean hasPaid() {
        return paid;
    }

    @Override
    public String toString() {
        return playerA.getName() + "/" + playerB.getName() +
                ", entry: " + getEntryPoints() +
                ", registrationTime=" + registrationTime +
                ", clazz=" + clazz +
                ", completeTeam=" + completeTeam +
                ", paid=" + paid;
    }
}
