package com.ngusta.cupassist.domain;

import java.io.Serializable;
import java.util.Date;

import static java.lang.Double.compare;

public class Team implements Serializable, Comparable<Team> {
    private Player playerA;
    private Player playerB;
    private Date registrationTime;
    private Clazz clazz;

    private boolean completeTeam;

    public Team(Player playerA, Player playerB, Date registrationTime, Clazz clazz) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.registrationTime = registrationTime;
        this.clazz = clazz;
        this.completeTeam = true;
    }

    public Team(Player playerA, Date registrationDate, Clazz clazz) {
        this.playerA = playerA;
        this.playerB = new Player("Partner sökes", "", "");
        this.registrationTime = registrationDate;
        this.clazz = clazz;
        this.completeTeam = false;
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
        if (clazz == Clazz.MIXED) {
            return (int) (Math.round(playerA.getEntryPoints() * 0.15) + playerA.getMixEntryPoints() + Math.round(playerB.getEntryPoints() * 0.15)
                    + playerB.getMixEntryPoints());
        }
        return playerA.getEntryPoints() + playerB.getEntryPoints();
    }

    public int getRankingPoints() {
        if (clazz == Clazz.MIXED) {
            return (int) (Math.round(playerA.getRankingPoints() * 0.15) + playerA.getMixRankingPoints() + Math
                    .round(playerB.getRankingPoints() * 0.15) + playerB.getMixRankingPoints());
        }
        return playerA.getRankingPoints() + playerB.getRankingPoints();
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
        if (clazz == Clazz.MIXED) {
            return Math.max(playerA.getMixEntryPoints(), playerB.getMixEntryPoints());
        }
        return Math.max(playerA.getEntryPoints(), playerB.getEntryPoints());
    }

    private int getHighestRankingPoints() {
        if (clazz == Clazz.MIXED) {
            return Math.max(playerA.getMixRankingPoints(), playerB.getMixRankingPoints());
        }
        return Math.max(playerA.getRankingPoints(), playerB.getRankingPoints());
    }

    public Date getRegistrationTime() {
        return registrationTime;
    }

    public Clazz getClazz() {
        return clazz;
    }
}
