package com.ngusta.cupassist.domain;

import android.support.annotation.Nullable;

import java.util.function.Predicate;

import static java.lang.Integer.compare;

public class Match implements Comparable<Match> {
    private final int number;
    private final String type;
    private final String clazz;
    private final String teamA;
    private final String teamB;
    private final String result;

    public Match(String matchNumber, String type, String clazz, String teamA, String teamB, String result) {
        this.number = Integer.parseInt(matchNumber);
        this.type = type;
        this.clazz = clazz;
        this.teamA = teamA;
        this.teamB = teamB;
        this.result = result;
    }

    @Override
    public String toString() {
        return clazz + "-" + type + ": " + getFirstNames(teamA) + " - " + getFirstNames(teamB) + " " + result;
    }

    private String getFirstNames(String team) {
        team = team.replaceAll("#[0-9]+ ", "");
        return team.replaceFirst(" .*", "") + "/" + team.replaceFirst(".*/ ", "").replaceFirst(" .*", "");
    }

    @Override
    public int compareTo(Match o) {
        int clazzCompare = clazz.compareTo(o.clazz);
        if (clazzCompare != 0) {
            return clazzCompare;
        }
        int typeCompare = compare(getMatchTypeCompareNumber(), o.getMatchTypeCompareNumber());
        if (typeCompare != 0) {
            return typeCompare;
        }
        return compare(number, o.number);
    }

    private int getMatchTypeCompareNumber() {
        if (type.length() == 1) { //A, B, C, D...
            return ((int) type.toLowerCase().charAt(0)) - 96;
        }
        if (type.contains("Åttondel")) {
            return 20;
        }
        if (type.contains("Kvart")) {
            return 21;
        }
        if (type.contains("Semi")) {
            return 22;
        }
        if (type.contains("Final")) {
            return 23;
        }
        return -1;
    }

    public static Predicate<Match> hasClazz(final String clazz) {
        return m -> m.clazz.equals(clazz);
    }
}
