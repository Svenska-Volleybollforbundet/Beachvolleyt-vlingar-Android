package com.ngusta.cupassist.parser;

import com.ngusta.cupassist.domain.Tournament;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TournamentListParser {

    public List<Tournament> parseTournamentList(String source) {
        Document document = Jsoup.parse(source);
        Elements tableRows = document.select("table tr[class]");
        List<Tournament> tournaments = new ArrayList<>(tableRows.size());

        for (Element tableRow : tableRows) {
            try {
                tournaments.add(createTournament(tableRow));
            } catch (ParseException e) {
                System.err.print("Failed to extract tournament from document: " + source);
                e.printStackTrace();
            }
        }

        return tournaments;
    }

    private Tournament createTournament(Element tableRow) throws ParseException {
        Date startDate = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                .parse(tableRow.child(0).text());
        String period = tableRow.child(2).text();
        String club = tableRow.child(3).text();
        String name = tableRow.child(4).text();
        String url = tableRow.child(4).select("a[href]").attr("href");
        String level = tableRow.child(5).text();
        String classes = tableRow.child(6).text();
        return new Tournament(startDate, period, club, name, url, level, classes);
    }
}