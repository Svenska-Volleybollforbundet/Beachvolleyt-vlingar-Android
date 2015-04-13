package com.ngusta.cupassist.domain;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CompetitionPeriod implements Serializable {

    public static final CompetitionPeriod[] COMPETITION_PERIODS = {
            new CompetitionPeriod(1, "TP 01", "2015-01-01", "2015-03-22"),
            new CompetitionPeriod(2, "TP 02", "2015-03-23", "2015-05-03"),
            new CompetitionPeriod(3, "TP 03", "2015-05-04", "2015-05-24"),
            new CompetitionPeriod(4, "TP 04", "2015-05-25", "2015-05-31"),
            new CompetitionPeriod(5, "TP 05", "2015-06-01", "2015-06-07"),
            new CompetitionPeriod(6, "TP 06", "2015-06-08", "2015-06-21"),
            new CompetitionPeriod(7, "TP 07", "2015-06-22", "2015-06-28"),
            new CompetitionPeriod(8, "TP 08", "2015-06-29", "2015-07-05"),
            new CompetitionPeriod(9, "TP 09", "2015-07-06", "2015-07-12"),
            new CompetitionPeriod(10, "TP 10", "2015-07-13", "2015-07-19"),
            new CompetitionPeriod(11, "TP 11", "2015-07-20", "2015-07-26"),
            new CompetitionPeriod(12, "TP 12", "2015-07-27", "2015-08-02"),
            new CompetitionPeriod(13, "TP 13", "2015-08-03", "2015-08-09"),
            new CompetitionPeriod(14, "TP 14", "2015-08-10", "2015-08-16"),
            new CompetitionPeriod(15, "TP 15", "2015-08-17", "2015-08-30"),
            new CompetitionPeriod(16, "TP 16", "2015-08-31", "2015-12-31")
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
            if (competitionPeriod.startDate.compareTo(date) <= 0
                    && competitionPeriod.endDate.compareTo(date) >= 0) {
                return competitionPeriod;
            }
        }
        throw new IllegalArgumentException(
                "There is no competition period for the given date " + date.toString());
    }

    public static CompetitionPeriod findPeriodByNumber(int periodNumber) {
        return COMPETITION_PERIODS[periodNumber - 1];
    }

    public static boolean qualifiesForSm(CompetitionPeriod period) {
        return period.getPeriodNumber() >= 2 && period.getPeriodNumber() <= 11;
    }

    public int getPeriodNumber() {
        return periodNumber;
    }

    public String getName() {
        return name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public boolean isCurrent() {
        Date now = new Date();
        return startDate.before(now) && endDate.after(now);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        return periodNumber == ((CompetitionPeriod) o).periodNumber;
    }

    @Override
    public int hashCode() {
        return periodNumber;
    }

    @Override
    public String toString() {
        return getName();
    }
}
