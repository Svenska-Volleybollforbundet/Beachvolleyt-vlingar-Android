package com.ngusta.cupassist.domain;

import java.io.Serializable;
import java.util.Date;

import static java.lang.Double.compare;

public class Team implements Serializable, Comparable<Team> {
    private Player playerA;
    private Player playerB;
    private Date registrationTime;
    private Clazz clazz;

    private int entryPoints;

    public Team(Player playerA, Player playerB, Date registrationTime, Clazz clazz) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.registrationTime = registrationTime;
        this.clazz = clazz;
        entryPoints = playerA.getEntryPoints() + playerB.getEntryPoints();
    }

    public Team(Player playerA, Player playerB, Date registrationTime, int individualEntryPointsPlayerA, int individualEntryPointsPlayerB) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.registrationTime = registrationTime;
        this.clazz = Clazz.MIXED;
        entryPoints = playerA.getEntryPoints() + playerB.getEntryPoints() + (int) Math.round(0.15 * individualEntryPointsPlayerA) + (int) Math
                .round(0.15 * individualEntryPointsPlayerB);
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

    public int getEntryPoints() {
        return entryPoints;
    }

    public int getRankingPoints() {
        return playerA.getRankingPoints() + playerB.getRankingPoints();
    }

    @Override
    public int compareTo(Team another) {
        int cmp = compare(another.getEntryPoints(), this.getEntryPoints());
        if (cmp != 0) {
            return cmp;
        }
        cmp = compare(another.getRankingPoints(), this.getRankingPoints());
        if (cmp != 0) {
            return cmp;
        }
        cmp = compare(another.getHighestEntryPoints(), this.getHighestEntryPoints());
        if (cmp != 0) {
            return cmp;
        }
        cmp = compare(another.getHighestRankingPoints(), this.getHighestRankingPoints());
        return cmp;
    }

    private int getHighestEntryPoints() {
        return Math.max(playerA.getEntryPoints(), playerB.getEntryPoints());
    }

    private int getHighestRankingPoints() {
        return Math.max(playerA.getRankingPoints(), playerB.getRankingPoints());
    }

    public Date getRegistrationTime() {
        return registrationTime;
    }

    public Clazz getClazz() {
        return clazz;
    }
}
