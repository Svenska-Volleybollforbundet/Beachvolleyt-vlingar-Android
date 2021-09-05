package com.ngusta.cupassist.domain;

import java.util.function.Predicate;

import static java.lang.Integer.compare;

public class Match implements Comparable<Match> {
    private final int number;
    private final MatchType type;
    private final String clazz;
    private final String teamA;
    private final String teamB;
    private final String setResult;
    private final String pointResult;

    public Match(String matchNumber, String type, String clazz, String teamA, String teamB, String setResult, String pointResult) {
        this.number = Integer.parseInt(matchNumber);
        this.type = new MatchType(type, clazz);
        this.clazz = clazz;
        this.teamA = teamA;
        this.teamB = teamB;
        this.setResult = setResult;
        this.pointResult = pointResult;
    }

    @Override
    public String toString() {
        return getLastNames(teamA) + " - " + getLastNames(teamB) + "\n" + setResult + " (" + pointResult + ")";
    }

    private String getFirstNames(String team) {
        team = team.replaceAll("#[0-9]+ ", "");
        return team.replaceFirst(" .*", "") + "/" + team.replaceFirst(".*/ ", "").replaceFirst(" .*", "");
    }

    private String getLastNames(String team) {
        team = team.replaceAll("#[0-9]+ ", "").trim();
        String[] players = team.split("/");
        if (players.length == 2) {
            String player1 = players[0].trim().charAt(0) + " " + players[0].trim().substring(players[0].trim().lastIndexOf(" "));
            String player2 = players[1].trim().charAt(0) + " " + players[1].trim().substring(players[1].trim().lastIndexOf(" "));
            return player1 + "/" + player2;
        }
        return team;
    }

    public MatchType getType() {
        return type;
    }

    @Override
    public int compareTo(Match o) {
        int clazzCompare = clazz.compareTo(o.clazz);
        if (clazzCompare != 0) {
            return clazzCompare;
        }
        int typeCompare = type.compareTo(o.type);
        if (typeCompare != 0) {
            return typeCompare;
        }
        return compare(number, o.number);
    }

    public static Predicate<Match> hasClazz(final String clazz) {
        return m -> m.clazz.equals(clazz);
    }
}
