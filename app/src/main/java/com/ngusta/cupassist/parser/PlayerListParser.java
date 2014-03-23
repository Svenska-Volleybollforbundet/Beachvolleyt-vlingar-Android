package com.ngusta.cupassist.parser;

import com.ngusta.cupassist.domain.Player;

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
        Elements tableRows = document.select("table:nth-of-type(1) tr:gt(0)");

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
        if (tableRow.childNodeSize() < 5) {
            System.err.println(String.format("Too few children (%d) in Player table row: %s",
                    tableRow.childNodeSize(), tableRow.toString()));
            return null;
        }
        String rankString = tableRow.child(0).text();
        Integer rank = !rankString.equals("&nbsp;") ? Integer.parseInt(rankString) : null;
        String[] name = tableRow.child(1).text().split(",");
        String firstName = name[1].trim();
        String lastName = name[0].trim();
        String club = tableRow.child(2).text();
        int rankPoints = Integer.parseInt(tableRow.child(3).text());
        int entryPoints = Integer.parseInt(tableRow.child(4).text());
        return new Player(rank, firstName, lastName, club, rankPoints, entryPoints);
    }
}
