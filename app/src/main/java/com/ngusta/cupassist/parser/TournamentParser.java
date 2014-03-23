package com.ngusta.cupassist.parser;

import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.domain.Team;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TournamentParser {

    public List<Team> parseTeams(String source, Set<Player> allPlayers) {
        ArrayList<Team> teams = new ArrayList<Team>();
        Document document = Jsoup.parse(source);
        Elements tableRows = document.select("table:first-of-type tr:gt(0)");

        if (tableRows.isEmpty()) {
            return teams;
        }

        Map<String, Player> allPlayersMap = new HashMap<>();
        for (Player player : allPlayers) {
            allPlayersMap.put(player.getNameAndClub(), player);
        }

        for (Element tableRow : tableRows) {
            Team team = readTeamFromTableRow(allPlayersMap, tableRow);
            if (team != null) {
                teams.add(team);
            }
        }
        return teams;
    }

    private Team readTeamFromTableRow(Map<String, Player> allPlayersMap, Element tableRow) {
        String[] names = tableRow.child(0).text().split("[,/]");

        if (names.length != 4) {
            System.err.print("Skipping incomplete Team table row: " + tableRow.text());
            return null;
        }

        String club = tableRow.child(1).text();
        String clazz = tableRow.child(2).text();

        Date registrationDate = null;
        try {
            registrationDate
                    = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(tableRow.child(3).text());
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
        String playerALastName = names[0].trim();
        String playerBFirstName = names[3].trim();
        String playerBLastName = names[2].trim();
        playerAFirstName = excludeParenthesisFromName(playerAFirstName);
        playerBFirstName = excludeParenthesisFromName(playerBFirstName);
        Player playerA = findPlayer(allPlayersMap, playerAFirstName, playerALastName, playerAClub);
        Player playerB = findPlayer(allPlayersMap, playerBFirstName, playerBLastName, playerBClub);
        return new Team(playerA, playerB, registrationDate, clazz);
    }

    private String excludeParenthesisFromName(String playerName) {
        if (playerName.contains("(")) {
            return playerName.substring(0, playerName.indexOf("(")).trim();
        }
        return playerName;
    }

    private Player findPlayer(Map<String, Player> allPlayers, String playerFirstName, String playerLastName, String playerClub) {
        Player newPlayer = new Player(playerFirstName, playerLastName, playerClub);
        if (allPlayers.containsKey(newPlayer.getNameAndClub())) {
            return allPlayers.get(newPlayer.getNameAndClub());
        }
        return newPlayer;
    }
}
