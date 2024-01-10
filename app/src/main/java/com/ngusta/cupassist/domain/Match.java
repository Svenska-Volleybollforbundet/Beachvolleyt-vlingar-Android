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
        return getDisplayNames(teamA) + " - " + getDisplayNames(teamB) + "\n" + getDisplaySetResult(setResult) + " (" + getDisplayPointResult(pointResult) + ")";
    }

    private String getDisplayNames(String team) {
        team = team.replaceAll("#[0-9]+ ", "").trim();
        String[] players = team.split("/");
        if (players.length == 2) {
            String[] p1 = players[0].trim().split(" ");
            String p1First = p1[0].trim();
            String p1Last = p1[1].trim();
            String[] p2 = players[1].trim().split(" ");
            String p2First= p2[0].trim();
            String p2Last = p2[1].trim();
            return p1First + " " + (p1Last.length() > 3 ? p1Last.substring(0, 3) : p1Last) + "/" + p2First + " " + (p2Last.length() > 3 ? p2Last.substring(0, 3) : p2Last);
        }
        return team;
    }

    private String getDisplaySetResult(String result) {
        return result.replaceFirst(" ", "-");
    }

    private String getDisplayPointResult(String result) {
        return result.replaceAll(" ", "-").replaceAll("/", " ");
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
