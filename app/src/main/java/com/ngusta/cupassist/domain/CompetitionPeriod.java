package com.ngusta.cupassist.domain;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CompetitionPeriod implements Serializable {

    public static final CompetitionPeriod[] COMPETITION_PERIODS = {
            new CompetitionPeriod("TP 01", "2014-01-01", "2014-03-23"),
            new CompetitionPeriod("TP 02", "2014-03-24", "2014-05-04"),
            new CompetitionPeriod("TP 03", "2014-05-05", "2014-05-25"),
            new CompetitionPeriod("TP 04", "2014-05-26", "2014-06-01"),
            new CompetitionPeriod("TP 05", "2014-06-02", "2014-06-08"),
            new CompetitionPeriod("TP 06", "2014-06-09", "2014-06-22"),
            new CompetitionPeriod("TP 07", "2014-06-23", "2014-06-29"),
            new CompetitionPeriod("TP 08", "2014-06-30", "2014-07-06"),
            new CompetitionPeriod("TP 09", "2014-07-07", "2014-07-13"),
            new CompetitionPeriod("TP 10", "2014-07-14", "2014-07-20"),
            new CompetitionPeriod("TP 11", "2014-07-21", "2014-07-27"),
            new CompetitionPeriod("TP 12", "2014-07-28", "2014-08-03"),
            new CompetitionPeriod("TP 13", "2014-08-04", "2014-08-10"),
            new CompetitionPeriod("TP 14", "2014-08-11", "2014-08-17"),
            new CompetitionPeriod("TP 15", "2014-08-18", "2014-08-31"),
            new CompetitionPeriod("TP 16", "2014-09-01", "2014-12-31")
    };

    private String name;
    private Date startDate;
    private Date endDate;

    public CompetitionPeriod(String name, String startDate, String endDate) {
        this.name = name;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            this.startDate = formatter.parse(startDate);
            this.endDate = formatter.parse(endDate);
        } catch (ParseException e) {
        }
    }

    static CompetitionPeriod findByName(String name) {
        for (CompetitionPeriod competitionPeriod : COMPETITION_PERIODS) {
            if (competitionPeriod.getName().equalsIgnoreCase(name)) {
                return competitionPeriod;
            }
        }
        throw new IllegalArgumentException("Not a valid competition period.");
    }

    public String getName() {
        return name;
    }
}
