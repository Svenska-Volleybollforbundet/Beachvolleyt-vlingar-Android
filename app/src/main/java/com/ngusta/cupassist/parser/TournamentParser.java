package com.ngusta.cupassist.parser;

import com.ngusta.cupassist.domain.Clazz;
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
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TournamentParser {

    private static final String REGEXP_PATTERN_FOR_REGISTRATION_URL
            = "pamelding/redirect.php\\?tknavn=(.*?)\", \"_blank\"";

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

    private Player findPlayer(Map<String, Player> allPlayers, String playerFirstName,
            String playerLastName, String playerClub) {
        Player newPlayer = new Player(playerFirstName, playerLastName, playerClub);
        if (allPlayers.containsKey(newPlayer.getNameAndClub())) {
            return allPlayers.get(newPlayer.getNameAndClub());
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
        Elements elements = document
                .select("tr:has(td:contains(Klassdetaljer)) > td:nth-child(2) tr:gt(0) td:lt(2)");

        Map<Clazz, Integer> maxNumberOfTeamsMap = new HashMap<>(2);
        Iterator<Element> iterator = elements.iterator();

        while (iterator.hasNext()) {
            Element clazzElem = iterator.next();
            Element maxNumberOfTeamsElem = iterator.hasNext() ? iterator.next() : null;

            if (maxNumberOfTeamsElem != null) {
                Clazz clazz = Clazz.parse(clazzElem.text());
                int maxNumberOfTeams = Integer.parseInt(maxNumberOfTeamsElem.text());
                maxNumberOfTeamsMap.put(clazz, maxNumberOfTeams);
            }
        }
        return maxNumberOfTeamsMap;
    }
}
