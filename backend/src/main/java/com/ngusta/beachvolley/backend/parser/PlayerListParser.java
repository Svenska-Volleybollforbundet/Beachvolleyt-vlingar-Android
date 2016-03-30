package com.ngusta.beachvolley.backend.parser;

import com.ngusta.beachvolley.domain.Player;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

public class PlayerListParser {

    public Set<Player> parsePlayerList(String source) {
        Set<Player> players = new HashSet<>();
        Document document = Jsoup.parse(source);
        Elements tableRows = document.select("table:nth-of-type(2) tr:gt(0)");

        if (tableRows.isEmpty()) {
            return players;
        }

        for (Element tableRow : tableRows) {
            Player player = createPlayer(tableRow);
            if (player != null) {
                players.add(player);
            }
        }

        return players;
    }

    private Player createPlayer(Element tableRow) {
        if (isHeaderForPlayersWithoutLicense(tableRow)) {
            return null;
        }
        String rankString = tableRow.child(0).text();
        Integer rank = !hasRanking(rankString) ? Integer.parseInt(rankString) : null;
        String[] name = tableRow.child(1).text().split(",");
        String firstName = name[1].trim();
        if (firstName.charAt(firstName.length() - 1) == '*') {
            firstName = firstName.substring(0, firstName.length() - 1);
        }
        String lastName = name[0].trim();
        String club = tableRow.child(2).text();
        int rankPoints = Integer.parseInt(tableRow.child(3).text());
        int entryPoints = Integer.parseInt(tableRow.child(4).text());
        return new Player(rank, firstName, lastName, club, rankPoints, entryPoints);
    }

    private boolean isHeaderForPlayersWithoutLicense(Element tableRow) {
        return tableRow.childNodeSize() < 5;
    }

    private boolean hasRanking(String rankString) {
        return rankString.length() == 1 && rankString.charAt(0) == 160;
    }
}
