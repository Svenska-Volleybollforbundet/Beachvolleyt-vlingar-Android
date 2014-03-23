package com.ngusta.cupassist.domain;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CompetitionPeriod implements Serializable {

    public static final CompetitionPeriod[] COMPETITION_PERIODS = {
            new CompetitionPeriod(1, "TP 01", "2014-01-01", "2014-03-23"),
            new CompetitionPeriod(2, "TP 02", "2014-03-24", "2014-05-04"),
            new CompetitionPeriod(3, "TP 03", "2014-05-05", "2014-05-25"),
            new CompetitionPeriod(4, "TP 04", "2014-05-26", "2014-06-01"),
            new CompetitionPeriod(5, "TP 05", "2014-06-02", "2014-06-08"),
            new CompetitionPeriod(6, "TP 06", "2014-06-09", "2014-06-22"),
            new CompetitionPeriod(7, "TP 07", "2014-06-23", "2014-06-29"),
            new CompetitionPeriod(8, "TP 08", "2014-06-30", "2014-07-06"),
            new CompetitionPeriod(9, "TP 09", "2014-07-07", "2014-07-13"),
            new CompetitionPeriod(10, "TP 10", "2014-07-14", "2014-07-20"),
            new CompetitionPeriod(11, "TP 11", "2014-07-21", "2014-07-27"),
            new CompetitionPeriod(12, "TP 12", "2014-07-28", "2014-08-03"),
            new CompetitionPeriod(13, "TP 13", "2014-08-04", "2014-08-10"),
            new CompetitionPeriod(14, "TP 14", "2014-08-11", "2014-08-17"),
            new CompetitionPeriod(15, "TP 15", "2014-08-18", "2014-08-31"),
            new CompetitionPeriod(16, "TP 16", "2014-09-01", "2014-12-31")
    };

    private int periodNumber;
    private String name;
    private Date startDate;
    private Date endDate;

    public CompetitionPeriod(int periodNumber, String name, String startDate, String endDate) {
        this.periodNumber = periodNumber;
        this.name = name;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        GregorianCalendar calendar = new GregorianCalendar();
        try {
            this.startDate = formatter.parse(startDate);
            calendar.setTime(formatter.parse(endDate));
            calendar.set(Calendar.HOUR, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            this.endDate = calendar.getTime();

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

    public static CompetitionPeriod findPeriodByDate(Date date) {
        for (CompetitionPeriod competitionPeriod : COMPETITION_PERIODS) {
            if (competitionPeriod.startDate.compareTo(date) <= 0 && competitionPeriod.endDate.compareTo(date) >= 0) {
                return competitionPeriod;
            }
        }
        throw new IllegalArgumentException("There is no competition period for the given date " + date.toString());
    }

    public int getPeriodNumber() {
        return periodNumber;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        return periodNumber == ((CompetitionPeriod) o).periodNumber;
    }

    @Override
    public int hashCode() {
        return periodNumber;
    }
}
