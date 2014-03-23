package com.ngusta.cupassist.parser;

import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.Tournament;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TournamentListParser {

    private static final String REGEXP_PATTERN_FOR_REGISTRATION_URL = "pamelding/redirect.php\\?tknavn=(.*?)\", \"_blank\"";

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
        Date startDate = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).parse(tableRow.child(0).text());
        String period = tableRow.child(2).text();
        String club = tableRow.child(3).text();
        String name = tableRow.child(4).text();
        String url = tableRow.child(4).select("a[href]").attr("href");
        String level = tableRow.child(5).text();
        String classes = tableRow.child(6).text();
        return new Tournament(startDate, period, club, name, url, level, classes);
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
                int maxNumberOfTeams = Integer.parseInt(maxNumberOfTeamsElem.text());
                maxNumberOfTeamsMap.put(clazz, maxNumberOfTeams);
            }
        }

        return maxNumberOfTeamsMap;
    }
}