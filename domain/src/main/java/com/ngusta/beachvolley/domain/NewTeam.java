package com.ngusta.beachvolley.domain;

import java.io.Serializable;
import java.util.Date;

import static java.lang.Double.compare;

public class NewTeam implements Serializable, Comparable<NewTeam> {

    public static final String PARTNER_SÖKES = "Partner sökes";

    private String playerA;

    private String playerB;

    private Date registrationTime;

    private Clazz clazz;

    private boolean paid;

    private boolean completeTeam;

    private int entryPoints;

    private int rankingPoints;

    private int highestEntryPoints;

    private int highestRankingPoints;

    public NewTeam() {
    }

    public NewTeam(Player playerA, Player playerB, Date registrationTime, Clazz clazz, boolean paid) {
        this.playerA = playerA.uniqueIdentifier();
        this.playerB = playerB.uniqueIdentifier();
        this.registrationTime = registrationTime;
        this.clazz = clazz;
        this.completeTeam = true;
        this.paid = paid;
        this.entryPoints = calculateEntryPoints(playerA, playerB);
        this.rankingPoints = calculateRankingPoints(playerA, playerB);
        this.highestEntryPoints = calculateHighestEntryPoints(playerA, playerB);
        this.highestRankingPoints = calculateHighestRankingPoints(playerA, playerB);
    }

    public NewTeam(Player playerA, Date registrationDate, Clazz clazz, boolean paid) {
        this.playerA = playerA.uniqueIdentifier();
        this.playerB = PARTNER_SÖKES;
        this.registrationTime = registrationDate;
        this.clazz = clazz;
        this.completeTeam = false;
        this.paid = paid;
        Player playerB = new Player("Partner sökes", "", "");
        this.entryPoints = calculateEntryPoints(playerA, playerB);
        this.rankingPoints = calculateRankingPoints(playerA, playerB);
        this.highestEntryPoints = calculateHighestEntryPoints(playerA, playerB);
        this.highestRankingPoints = calculateHighestRankingPoints(playerA, playerB);
    }

    public String getPlayerA() {
        return playerA;
    }

    public String getPlayerB() {
        return playerB;
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

    public boolean isPaid() {
        return paid;
    }

    public int getEntryPoints() {
        return entryPoints;
    }

    public int getRankingPoints() {
        return rankingPoints;
    }

    public int getHighestEntryPoints() {
        return highestEntryPoints;
    }

    public int getHighestRankingPoints() {
        return highestRankingPoints;
    }

    private int calculateEntryPoints(Player playerA, Player playerB) {
        if (clazz == Clazz.MIXED) {
            return (int) (Math.round(playerA.getEntryPoints() * 0.1) + playerA.getMixEntryPoints() + Math.round(playerB.getEntryPoints() * 0.1)
                    + playerB.getMixEntryPoints());
        }
        return playerA.getEntryPoints() + playerB.getEntryPoints();
    }

    private int calculateRankingPoints(Player playerA, Player playerB) {
        if (clazz == Clazz.MIXED) {
            return (int) (Math.round(playerA.getRankingPoints() * 0.1) + playerA.getMixRankingPoints() + Math
                    .round(playerB.getRankingPoints() * 0.1) + playerB.getMixRankingPoints());
        }
        return playerA.getRankingPoints() + playerB.getRankingPoints();
    }

    private int calculateHighestEntryPoints(Player playerA, Player playerB) {
        if (clazz == Clazz.MIXED) {
            return Math.max(playerA.getMixEntryPoints(), playerB.getMixEntryPoints());
        }
        return Math.max(playerA.getEntryPoints(), playerB.getEntryPoints());
    }

    private int calculateHighestRankingPoints(Player playerA, Player playerB) {
        if (clazz == Clazz.MIXED) {
            return Math.max(playerA.getMixRankingPoints(), playerB.getMixRankingPoints());
        }
        return Math.max(playerA.getRankingPoints(), playerB.getRankingPoints());
    }

    @Override
    public int compareTo(NewTeam another) {
        if (completeTeam && !another.completeTeam) {
            return -1;
        } else if (!completeTeam && another.completeTeam) {
            return 1;
        }

        int cmp = compare(another.entryPoints, this.entryPoints);
        if (cmp != 0) {
            return cmp;
        }
        cmp = compare(another.rankingPoints, this.rankingPoints);
        if (cmp != 0) {
            return cmp;
        }
        cmp = compare(another.highestEntryPoints, this.highestEntryPoints);
        if (cmp != 0) {
            return cmp;
        }
        cmp = compare(another.highestRankingPoints, this.highestRankingPoints);
        return cmp;
    }

    @Override
    public String toString() {
        return playerA + "/" + playerB +
                ", entry: " + entryPoints +
                ", registrationTime=" + registrationTime +
                ", clazz=" + clazz +
                ", completeTeam=" + completeTeam +
                ", paid=" + paid;
    }
}
