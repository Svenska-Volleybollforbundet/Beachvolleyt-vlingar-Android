package com.ngusta.cupassist.parser;

import com.ngusta.cupassist.domain.PlayerResults;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PlayerDetailsParser {

    public enum ParseMode {
        ALL_RESULTS,
        ONLY_MIXED
    }

    private static final SimpleDateFormat BIRTH_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private static final SimpleDateFormat ID_FORMAT = new SimpleDateFormat("ddMMyy", Locale.getDefault());

    public String parseAge(String source) {
        Document document = Jsoup.parse(source);
        String nameAgeClubString = document.child(0).child(1).child(1).text();
        String idOrBirthdate = nameAgeClubString.substring(nameAgeClubString.indexOf("(") + 1, nameAgeClubString.indexOf(")"));
        String age = null;
        try {
            if (isIdFormat(idOrBirthdate)) {
                age = calculateAge(ID_FORMAT.parse(idOrBirthdate.substring(1, 7)));
            } else {
                age = calculateAge(BIRTH_DATE_FORMAT.parse(idOrBirthdate.replace(".", "-")));
            }
        } catch (ParseException e) {
            System.err.println("Could not parse age: " + nameAgeClubString);
        }
        return age;
    }

    private boolean isIdFormat(String idOrBirthdate) {
        return idOrBirthdate.matches("^[KM].*");
    }

    private String calculateAge(Date dateOfBirth) {
        Calendar now = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        dob.setTime(dateOfBirth);
        if (dob.after(now)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }
        int year1 = now.get(Calendar.YEAR);
        int year2 = dob.get(Calendar.YEAR);
        int age = year1 - year2;
        int month1 = now.get(Calendar.MONTH);
        int month2 = dob.get(Calendar.MONTH);
        if (month2 > month1) {
            age--;
        } else if (month1 == month2) {
            int day1 = now.get(Calendar.DAY_OF_MONTH);
            int day2 = dob.get(Calendar.DAY_OF_MONTH);
            if (day2 > day1) {
                age--;
            }
        }
        return String.valueOf(age);
    }

    public PlayerResults parseResults(String source, ParseMode parseMode) {
        PlayerResults playerResults = new PlayerResults();
        Document document = Jsoup.parse(source);
        Element table = document.child(0).getElementsByTag("body").get(0).getElementsByTag("table").get(0);
        if (!table.children().isEmpty()) {
            Elements resultRows = table.child(0).children();
            for (Element resultRow : resultRows) {
                if (ignoreNonMixedResults(resultRow, parseMode)) {
                    continue;
                }
                String tp = resultRow.child(0).text();
                String year = resultRow.child(1).text();
                String tournamentName = resultRow.child(2).text();
                String points = resultRow.child(3).text();
                playerResults.addResult(tp, year, tournamentName, points);
            }
        }
        return playerResults;
    }

    private boolean ignoreNonMixedResults(Element resultRow, ParseMode parseMode) {
        return parseMode == ParseMode.ONLY_MIXED && !resultRow.child(4).text().startsWith("(M");
    }
}
