package com.ngusta.cupassist.parser;

import com.google.common.collect.HashMultimap;

import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.Match;
import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.domain.Team;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TournamentParser {

    private static final String REGEXP_PATTERN_FOR_REGISTRATION_URL = "/app/(.*?)\", \"_blank\"";

    public List<Team> parseTeams(String source, HashMultimap<String, Player> allPlayers, boolean isNewProfixio) {
        ArrayList<Team> teams = new ArrayList<Team>();
        Document document = Jsoup.parse(source);
        Elements tableRows = isNewProfixio ? document.select("main ul li") : document.select("table:first-of-type tr:gt(0)");

        if (tableRows.isEmpty()) {
            return teams;
        }

        for (Element tableRow : tableRows) {
            Team team = isNewProfixio ? newProfixioReadTeam(tableRow, allPlayers) : readTeamFromTableRow(tableRow, allPlayers, isNewProfixio);
            if (team != null) {
                teams.add(team);
            }
        }
        return teams;
    }

    private Team readTeamFromTableRow(Element tableRow,
            HashMultimap<String, Player> allPlayers, boolean isNewProfixio) {
        String[] names = tableRow.child(0).text().replace("Waiting list", "").split("[,/]");

        if (names.length < 2 || names.length > 4) {
            System.err.print("Skipping incomplete Team table row: " + tableRow.text());
            return null;
        }

        String club = tableRow.child(1).text();
        Clazz clazz = Clazz.parse(tableRow.child(2).text());
        int teamEntry;
        try {
            teamEntry = Integer.parseInt(tableRow.child(isNewProfixio ? 3 : 5).text());
        } catch (NumberFormatException nfe) {
            teamEntry = 0;
        }

        Date registrationDate = null;
        try {
            registrationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tableRow.child(isNewProfixio ? 5 : 3).text());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String playerAClub = club;
        String playerBClub = club;
        if (club.contains("/")) {
            String[] clubs = club.split("/");
            playerAClub = clubs[0].trim();
            playerBClub = clubs[1].trim();
        }

        String playerAFirstName = names[1].trim();
        playerAFirstName = excludeParenthesisFromName(playerAFirstName);
        String playerALastName = names[0].trim();
        Player playerA = findPlayer(allPlayers, playerAFirstName, playerALastName, playerAClub, teamEntry, clazz);

        boolean paid = "OK".equalsIgnoreCase(tableRow.child(isNewProfixio ? 4 : 6).text());
        if (names.length == 4) {
            String playerBFirstName = names[3].trim();
            playerBFirstName = excludeParenthesisFromName(playerBFirstName);
            String playerBLastName = names[2].trim();
            Player playerB = findPlayer(allPlayers, playerBFirstName, playerBLastName, playerBClub, teamEntry, clazz);

            return new Team(playerA, playerB, registrationDate, clazz, paid);
        } else {
            return new Team(playerA, registrationDate, clazz, paid);
        }
    }


    private Team newProfixioReadTeam(Element listItem, HashMultimap<String, Player> allPlayers) {
        Element teamData = listItem.select("div:not([class])").get(1);
        String[] names = teamData.child(0).child(0).text().replace("Waiting list", "").split("[,/]");

        if (names.length < 2 || names.length > 4) {
            System.err.print("Skipping incomplete Team table row: " + listItem.text());
            return null;
        }

        String club = teamData.child(1).child(0).child(0).text();
        Clazz clazz = Clazz.parse(teamData.child(1).child(1).child(0).text());
        int playerAPoints, playerBPoints;
        try {
            String[] points = teamData.child(1).child(1).child(1).child(0).child(1).text().split("\\/|\\ ");
            playerAPoints = Integer.parseInt(points[1]);
            playerBPoints = Integer.parseInt(points[2]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            playerAPoints = playerBPoints = 0;
        }

        Date registrationDate = null;
        try {
            registrationDate = new SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.getDefault()).parse(teamData.child(1).child(2).text());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String playerAClub = club;
        String playerBClub = club;
        if (club.contains("/")) {
            String[] clubs = club.split("/");
            playerAClub = clubs[0].trim();
            playerBClub = clubs[1].trim();
        }

        String playerAFirstName = names[1].trim();
        playerAFirstName = excludeParenthesisFromName(playerAFirstName);
        String playerALastName = names[0].trim();
        Player playerA = findPlayer(allPlayers, playerAFirstName, playerALastName, playerAClub, playerAPoints, clazz);
        if (clazz == Clazz.MIXED && playerA.getMixedPoints() == 0) {
            playerA.setMixedEntryPoints(playerAPoints);
        }

        boolean paid = "PAID".equalsIgnoreCase(teamData.child(1).child(0).child(1).text());
        if (names.length == 4) {
            String playerBFirstName = names[3].trim();
            playerBFirstName = excludeParenthesisFromName(playerBFirstName);
            String playerBLastName = names[2].trim();
            Player playerB = findPlayer(allPlayers, playerBFirstName, playerBLastName, playerBClub, playerBPoints, clazz);

            if (clazz == Clazz.MIXED && playerB.getMixedPoints() == 0) {
                playerB.setMixedEntryPoints(playerBPoints);
            }

            return new Team(playerA, playerB, registrationDate, clazz, paid);
        } else {
            return new Team(playerA, registrationDate, clazz, paid);
        }
    }

    private String excludeParenthesisFromName(String playerName) {
        if (playerName.contains("(")) {
            return playerName.substring(0, playerName.indexOf("(")).trim();
        }
        return playerName;
    }

    private Player findPlayer(HashMultimap<String, Player> allPlayers, String playerFirstName,
            String playerLastName, String playerClub, int playerPoints, Clazz clazz) {
        Player newPlayer = new Player(playerFirstName, playerLastName, playerClub, playerPoints, clazz);
        if (allPlayers.containsKey(newPlayer.getNameAndClub())) {
            Set<Player> players = allPlayers.get(newPlayer.getNameAndClub());
            if (players.size() == 1) {
                return players.iterator().next();
            }
            int smallestDiff = Integer.MAX_VALUE;
            Player mostLikelyPlayer = null;
            for (Player p : players) {
                int newDiff = Math.abs(playerPoints - p.getEntryPoints());
                if (newDiff < smallestDiff) {
                    smallestDiff = newDiff;
                    mostLikelyPlayer = p;
                }
            }
            return mostLikelyPlayer;
        }
        return newPlayer;
    }

    public String parseRegistrationUrl(String source) {
        Pattern pattern = Pattern.compile(REGEXP_PATTERN_FOR_REGISTRATION_URL);
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public Map<Clazz, Integer> parseMaxNumberOfTeams(String source) {
        Document document = Jsoup.parse(source);
        Elements elements = document.select("tr:has(td:contains(Klassdetaljer)) > td:nth-child(2) tr:gt(0) td:lt(2)");

        Map<Clazz, Integer> maxNumberOfTeamsMap = new HashMap<>(2);
        Iterator<Element> iterator = elements.iterator();

        while (iterator.hasNext()) {
            Element clazzElem = iterator.next();
            Element maxNumberOfTeamsElem = iterator.hasNext() ? iterator.next() : null;

            if (maxNumberOfTeamsElem != null) {
                Clazz clazz = Clazz.parse(clazzElem.text());
                if (clazz != Clazz.UNKNOWN) {
                    if (maxNumberOfTeamsElem.text().isEmpty()) {
                        if (!maxNumberOfTeamsMap.containsKey(clazz)) {
                            maxNumberOfTeamsMap.put(clazz, 1000);
                        }
                    } else {
                        int maxNumberOfTeams = Integer.parseInt(maxNumberOfTeamsElem.text());
                        maxNumberOfTeamsMap.put(clazz, maxNumberOfTeams);
                    }
                }
            }
        }
        return maxNumberOfTeamsMap;
    }

    public List<Match> parseMatches(String source) {
        Document document = Jsoup.parse(source);
        Elements elements = document.select("div[x-data~=display_matches_as_cards] ul li");

        List<Match> matches = new ArrayList<>();
        try {
            for (Element match : elements) {
                if (match.child(0).text().equalsIgnoreCase("No matches available")) {
                    break;
                }
                Element allContent = match.child(0).child(0).child(0);
                //String[] matchNumberAndType = allContent.child(0).child(0).child(0).text().trim().split(" ");
                String matchNumber = "0";//matchNumberAndType[0];
                String matchType = allContent.child(1).child(0).child(1).text();
                matchType = matchType.isEmpty() ? "Grupp" : matchType;
                String clazz = allContent.child(1).child(0).child(0).text().trim();
                String teamA = getTeamA(allContent);
                String teamB = getTeamB(allContent);
                boolean hasThirdSet = allContent.child(1).child(1).children().size() == 5;
                String setResult = hasThirdSet ? allContent.child(1).child(1).child(4).child(0).text().trim() : allContent.child(1).child(1).child(3).child(0).text().trim();
                String pointResult = allContent.child(1).child(1).child(1).text() + "/" + allContent.child(1).child(1).child(2).text() + (hasThirdSet ? "/" + allContent.child(1).child(1).child(3).text() : "");
                matches.add(new Match(matchNumber, matchType, clazz, teamA, teamB, setResult, pointResult));
            }
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Result parsing broken");
        }
        return matches;
    }

    private String getTeamA(Element allContent) {
        Element e = allContent.child(1).child(1).child(0).child(0);
        return e.text().trim();
    }

    private String getTeamB(Element allContent) {
        Element e = allContent.child(1).child(1).child(0).child(1);
        return e.text().trim();
    }
}
