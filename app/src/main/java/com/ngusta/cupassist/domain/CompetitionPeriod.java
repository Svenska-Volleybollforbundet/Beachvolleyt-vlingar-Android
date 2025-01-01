package com.ngusta.cupassist.domain;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CompetitionPeriod implements Serializable {

    private static final CompetitionPeriod[] COMPETITION_PERIODS_PREVIOUS_YEARS = {
            new CompetitionPeriod(1, "TP 01", "2016-01-01", "2016-04-03"),
            new CompetitionPeriod(2, "TP 02", "2016-04-04", "2016-05-22"),
            new CompetitionPeriod(3, "TP 03", "2016-05-23", "2016-06-06"),
            new CompetitionPeriod(4, "TP 04", "2016-06-07", "2016-06-12"),
            new CompetitionPeriod(5, "TP 05", "2016-06-13", "2016-06-26"),
            new CompetitionPeriod(6, "TP 06", "2016-06-27", "2016-07-03"),
            new CompetitionPeriod(7, "TP 07", "2016-07-04", "2016-07-10"),
            new CompetitionPeriod(8, "TP 08", "2016-07-11", "2016-07-17"),
            new CompetitionPeriod(9, "TP 09", "2016-07-18", "2016-07-24"),
            new CompetitionPeriod(10, "TP 10", "2016-07-25", "2016-07-31"),
            new CompetitionPeriod(11, "TP 11", "2016-08-01", "2016-08-07"),
            new CompetitionPeriod(12, "TP 12", "2016-08-08", "2016-08-14"),
            new CompetitionPeriod(13, "TP 13", "2016-08-15", "2016-08-21"),
            new CompetitionPeriod(14, "TP 14", "2016-08-22", "2016-09-04"),
            new CompetitionPeriod(15, "TP 15", "2016-09-05", "2016-10-16"),
            new CompetitionPeriod(16, "TP 16", "2016-10-17", "2016-12-31"),
            new CompetitionPeriod(1, "TP 01", "2017-01-01", "2017-04-02"),
            new CompetitionPeriod(2, "TP 02", "2017-04-03", "2017-05-21"),
            new CompetitionPeriod(3, "TP 03", "2017-05-22", "2017-06-04"),
            new CompetitionPeriod(4, "TP 04", "2017-06-05", "2017-06-11"),
            new CompetitionPeriod(5, "TP 05", "2017-06-12", "2017-06-25"),
            new CompetitionPeriod(6, "TP 06", "2017-06-26", "2017-07-02"),
            new CompetitionPeriod(7, "TP 07", "2017-07-03", "2017-07-09"),
            new CompetitionPeriod(8, "TP 08", "2017-07-10", "2017-07-16"),
            new CompetitionPeriod(9, "TP 09", "2017-07-17", "2017-07-23"),
            new CompetitionPeriod(10, "TP 10", "2017-07-24", "2017-07-30"),
            new CompetitionPeriod(11, "TP 11", "2017-07-31", "2017-08-06"),
            new CompetitionPeriod(12, "TP 12", "2017-08-07", "2017-08-13"),
            new CompetitionPeriod(13, "TP 13", "2017-08-14", "2017-08-20"),
            new CompetitionPeriod(14, "TP 14", "2017-08-21", "2017-09-03"),
            new CompetitionPeriod(15, "TP 15", "2017-09-04", "2017-10-15"),
            new CompetitionPeriod(16, "TP 16", "2017-10-16", "2017-12-31"),
            new CompetitionPeriod(1, "TP 01", "2018-01-01", "2018-04-02"),
            new CompetitionPeriod(2, "TP 02", "2018-04-03", "2018-05-20"),
            new CompetitionPeriod(3, "TP 03", "2018-05-21", "2018-06-03"),
            new CompetitionPeriod(4, "TP 04", "2018-06-04", "2018-06-10"),
            new CompetitionPeriod(5, "TP 05", "2018-06-11", "2018-06-24"),
            new CompetitionPeriod(6, "TP 06", "2018-06-25", "2018-07-01"),
            new CompetitionPeriod(7, "TP 07", "2018-07-02", "2018-07-08"),
            new CompetitionPeriod(8, "TP 08", "2018-07-09", "2018-07-15"),
            new CompetitionPeriod(9, "TP 09", "2018-07-16", "2018-07-22"),
            new CompetitionPeriod(10, "TP 10", "2018-07-23", "2018-07-29"),
            new CompetitionPeriod(11, "TP 11", "2018-07-30", "2018-08-05"),
            new CompetitionPeriod(12, "TP 12", "2018-08-06", "2018-08-12"),
            new CompetitionPeriod(13, "TP 13", "2018-08-13", "2018-08-19"),
            new CompetitionPeriod(14, "TP 14", "2018-08-20", "2018-09-02"),
            new CompetitionPeriod(15, "TP 15", "2018-09-03", "2018-10-14"),
            new CompetitionPeriod(16, "TP 16", "2018-10-15", "2018-12-31"),
            new CompetitionPeriod(1, "TP 01", "2019-01-01", "2019-03-31"),
            new CompetitionPeriod(2, "TP 02", "2019-04-01", "2019-05-18"),
            new CompetitionPeriod(3, "TP 03", "2019-05-19", "2019-06-02"),
            new CompetitionPeriod(4, "TP 04", "2019-06-03", "2019-06-09"),
            new CompetitionPeriod(5, "TP 05", "2019-06-10", "2019-06-23"),
            new CompetitionPeriod(6, "TP 06", "2019-06-24", "2019-06-30"),
            new CompetitionPeriod(7, "TP 07", "2019-07-01", "2019-07-07"),
            new CompetitionPeriod(8, "TP 08", "2019-07-08", "2019-07-14"),
            new CompetitionPeriod(9, "TP 09", "2019-07-15", "2019-07-21"),
            new CompetitionPeriod(10, "TP 10", "2019-07-22", "2019-07-28"),
            new CompetitionPeriod(11, "TP 11", "2019-07-29", "2019-08-04"),
            new CompetitionPeriod(12, "TP 12", "2019-08-05", "2019-08-11"),
            new CompetitionPeriod(13, "TP 13", "2019-08-12", "2019-08-18"),
            new CompetitionPeriod(14, "TP 14", "2019-08-19", "2019-09-01"),
            new CompetitionPeriod(15, "TP 15", "2019-09-02", "2019-10-13"),
            new CompetitionPeriod(16, "TP 16", "2019-10-14", "2019-12-31"),
            new CompetitionPeriod(1, "TP 01", "2020-01-01", "2020-03-29"),
            new CompetitionPeriod(2, "TP 02", "2020-03-30", "2020-06-21"),
            new CompetitionPeriod(3, "TP 03", "2020-06-22", "2020-06-28"),
            new CompetitionPeriod(4, "TP 04", "2020-06-29", "2020-07-05"),
            new CompetitionPeriod(5, "TP 05", "2020-07-06", "2020-07-12"),
            new CompetitionPeriod(6, "TP 06", "2020-07-13", "2020-07-19"),
            new CompetitionPeriod(7, "TP 07", "2020-07-20", "2020-08-02"),
            new CompetitionPeriod(8, "TP 08", "2020-08-03", "2020-08-09"),
            new CompetitionPeriod(9, "TP 09", "2020-08-10", "2020-08-16"),
            new CompetitionPeriod(10, "TP 10", "2020-08-17", "2020-08-30"),
            new CompetitionPeriod(11, "TP 11", "2020-08-31", "2020-10-11"),
            new CompetitionPeriod(12, "TP 12", "2020-10-12", "2020-12-31"),
            new CompetitionPeriod(1, "TP 01", "2021-01-01", "2021-06-06"),
            new CompetitionPeriod(2, "TP 02", "2021-06-07", "2021-06-13"),
            new CompetitionPeriod(3, "TP 03", "2021-06-14", "2021-06-27"),
            new CompetitionPeriod(4, "TP 04", "2021-06-28", "2021-07-04"),
            new CompetitionPeriod(5, "TP 05", "2021-07-05", "2021-07-11"),
            new CompetitionPeriod(6, "TP 06", "2021-07-12", "2021-07-18"),
            new CompetitionPeriod(7, "TP 07", "2021-07-19", "2021-07-25"),
            new CompetitionPeriod(8, "TP 08", "2021-07-26", "2021-08-08"),
            new CompetitionPeriod(9, "TP 09", "2021-08-09", "2021-08-15"),
            new CompetitionPeriod(10, "TP 10", "2021-08-16", "2021-08-22"),
            new CompetitionPeriod(11, "TP 11", "2021-08-23", "2021-09-05"),
            new CompetitionPeriod(12, "TP 12", "2021-09-06", "2021-10-17"),
            new CompetitionPeriod(13, "TP 13", "2021-10-18", "2021-12-31"),
            new CompetitionPeriod(1, "TP 01", "2022-01-01", "2022-04-03"),
            new CompetitionPeriod(2, "TP 02", "2022-04-04", "2022-05-22"),
            new CompetitionPeriod(3, "TP 03", "2022-05-23", "2022-05-29"),
            new CompetitionPeriod(4, "TP 04", "2022-05-30", "2022-06-05"),
            new CompetitionPeriod(5, "TP 05", "2022-06-06", "2022-06-12"),
            new CompetitionPeriod(6, "TP 06", "2022-06-13", "2022-06-26"),
            new CompetitionPeriod(7, "TP 07", "2022-06-27", "2022-07-03"),
            new CompetitionPeriod(8, "TP 08", "2022-07-04", "2022-07-10"),
            new CompetitionPeriod(9, "TP 09", "2022-07-11", "2022-07-17"),
            new CompetitionPeriod(10, "TP 10", "2022-07-18", "2022-07-24"),
            new CompetitionPeriod(11, "TP 11", "2022-07-25", "2022-08-07"),
            new CompetitionPeriod(12, "TP 12", "2022-08-08", "2022-08-14"),
            new CompetitionPeriod(13, "TP 13", "2022-08-15", "2022-08-21"),
            new CompetitionPeriod(14, "TP 14", "2022-08-22", "2022-09-04"),
            new CompetitionPeriod(15, "TP 15", "2022-09-05", "2022-10-16"),
            new CompetitionPeriod(16, "TP 16", "2022-10-17", "2022-12-31"),
            new CompetitionPeriod(1, "TP 01", "2023-01-01", "2023-04-02"),
            new CompetitionPeriod(2, "TP 02", "2023-04-03", "2023-05-21"),
            new CompetitionPeriod(3, "TP 03", "2023-05-22", "2023-05-28"),
            new CompetitionPeriod(4, "TP 04", "2023-05-29", "2023-06-04"),
            new CompetitionPeriod(5, "TP 05", "2023-06-05", "2023-06-11"),
            new CompetitionPeriod(6, "TP 06", "2023-06-12", "2023-06-25"),
            new CompetitionPeriod(7, "TP 07", "2023-06-26", "2023-07-02"),
            new CompetitionPeriod(8, "TP 08", "2023-07-03", "2023-07-09"),
            new CompetitionPeriod(9, "TP 09", "2023-07-10", "2023-07-16"),
            new CompetitionPeriod(10, "TP 10", "2023-07-17", "2023-07-23"),
            new CompetitionPeriod(11, "TP 11", "2023-07-24", "2023-08-06"),
            new CompetitionPeriod(12, "TP 12", "2023-08-07", "2023-08-13"),
            new CompetitionPeriod(13, "TP 13", "2023-08-14", "2023-08-20"),
            new CompetitionPeriod(14, "TP 14", "2023-08-21", "2023-09-03"),
            new CompetitionPeriod(15, "TP 15", "2023-09-04", "2023-10-15"),
            new CompetitionPeriod(16, "TP 16", "2023-10-16", "2023-12-31"),
            new CompetitionPeriod(1, "TP 01", "2024-01-01", "2024-04-01"),
            new CompetitionPeriod(2, "TP 02", "2024-04-02", "2024-05-19"),
            new CompetitionPeriod(3, "TP 03", "2024-05-20", "2024-05-26"),
            new CompetitionPeriod(4, "TP 04", "2024-05-27", "2024-06-02"),
            new CompetitionPeriod(5, "TP 05", "2024-06-03", "2024-06-09"),
            new CompetitionPeriod(6, "TP 06", "2024-06-10", "2024-06-23"),
            new CompetitionPeriod(7, "TP 07", "2024-06-24", "2024-06-30"),
            new CompetitionPeriod(8, "TP 08", "2024-07-01", "2024-07-07"),
            new CompetitionPeriod(9, "TP 09", "2024-07-08", "2024-07-14"),
            new CompetitionPeriod(10, "TP 10", "2024-07-15", "2024-07-21"),
            new CompetitionPeriod(11, "TP 11", "2024-07-22", "2024-08-04"),
            new CompetitionPeriod(12, "TP 12", "2024-08-05", "2024-08-11"),
            new CompetitionPeriod(13, "TP 13", "2024-08-12", "2024-08-18"),
            new CompetitionPeriod(14, "TP 14", "2024-08-19", "2024-09-01"),
            new CompetitionPeriod(15, "TP 15", "2024-09-02", "2024-10-13"),
            new CompetitionPeriod(16, "TP 16", "2024-10-14", "2024-12-31")
    };

    public static final CompetitionPeriod[] COMPETITION_PERIODS = {
            new CompetitionPeriod(1, "TP 01", "2025-01-01", "2025-03-30"),
            new CompetitionPeriod(2, "TP 02", "2025-03-31", "2025-05-18"),
            new CompetitionPeriod(3, "TP 03", "2025-05-19", "2025-05-25"),
            new CompetitionPeriod(4, "TP 04", "2025-05-26", "2025-06-01"),
            new CompetitionPeriod(5, "TP 05", "2025-06-02", "2025-06-08"),
            new CompetitionPeriod(6, "TP 06", "2025-06-09", "2025-06-22"),
            new CompetitionPeriod(7, "TP 07", "2025-06-23", "2025-06-29"),
            new CompetitionPeriod(8, "TP 08", "2025-06-30", "2025-07-06"),
            new CompetitionPeriod(9, "TP 09", "2025-07-07", "2025-07-13"),
            new CompetitionPeriod(10, "TP 10", "2025-07-14", "2025-07-20"),
            new CompetitionPeriod(11, "TP 11", "2025-07-21", "2025-08-03"),
            new CompetitionPeriod(12, "TP 12", "2025-08-04", "2025-08-10"),
            new CompetitionPeriod(13, "TP 13", "2025-08-11", "2025-08-17"),
            new CompetitionPeriod(14, "TP 14", "2025-08-18", "2025-08-31"),
            new CompetitionPeriod(15, "TP 15", "2025-09-01", "2025-10-12"),
            new CompetitionPeriod(16, "TP 16", "2025-10-13", "2025-12-31"),
    };

    private static final CompetitionPeriod[] COMPETITION_PERIODS_NEXT_YEAR = {
            new CompetitionPeriod(1, "TP 01", "2026-01-01", "2026-03-29"),
            new CompetitionPeriod(2, "TP 02", "2026-03-30", "2026-05-17"),
            new CompetitionPeriod(3, "TP 03", "2026-05-18", "2026-05-24"),
            new CompetitionPeriod(4, "TP 04", "2026-05-25", "2026-05-31"),
            new CompetitionPeriod(5, "TP 05", "2026-06-01", "2026-06-07"),
            new CompetitionPeriod(6, "TP 06", "2026-06-08", "2026-06-21"),
            new CompetitionPeriod(7, "TP 07", "2026-06-22", "2026-06-28"),
            new CompetitionPeriod(8, "TP 08", "2026-06-29", "2026-07-05"),
            new CompetitionPeriod(9, "TP 09", "2026-07-06", "2026-07-12"),
            new CompetitionPeriod(10, "TP 10", "2026-07-13", "2026-07-19"),
            new CompetitionPeriod(11, "TP 11", "2026-07-20", "2026-08-02"),
            new CompetitionPeriod(12, "TP 12", "2026-08-03", "2026-08-09"),
            new CompetitionPeriod(13, "TP 13", "2026-08-10", "2026-08-16"),
            new CompetitionPeriod(14, "TP 14", "2026-08-17", "2026-08-30"),
            new CompetitionPeriod(15, "TP 15", "2026-08-31", "2026-10-11"),
            new CompetitionPeriod(16, "TP 16", "2026-10-12", "2026-12-31"),
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
        return findByName(name, 2018);
    }

    static CompetitionPeriod findByName(String name, int year) {
        for (CompetitionPeriod cp : COMPETITION_PERIODS) {
            if (cp.getName().equalsIgnoreCase(name) ||
                    cp.getName().replace(" 0", "").replace(" ", "").equalsIgnoreCase(name)) {
                if (getYear(cp.getStartDate()) == year) {
                    return cp;
                }
            }
        }
        for (CompetitionPeriod cp : COMPETITION_PERIODS_PREVIOUS_YEARS) {
            if (cp.getName().equalsIgnoreCase(name) ||
                    cp.getName().replace(" 0", "").replace(" ", "").equalsIgnoreCase(name)) {
                if (getYear(cp.getStartDate()) == year) {
                    return cp;
                }
            }
        }
        for (CompetitionPeriod cp : COMPETITION_PERIODS_NEXT_YEAR) {
            if (cp.getName().equalsIgnoreCase(name) ||
                    cp.getName().replace(" 0", "").replace(" ", "").equalsIgnoreCase(name)) {
                if (getYear(cp.getStartDate()) == year) {
                    return cp;
                }
            }
        }
        throw new IllegalArgumentException("Not a valid competition period: " + name);
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

    public static CompetitionPeriod findPeriodByNumber(int periodNumber, int year) {
        int thisYear = getYear(new Date());
        if (year == thisYear) {
            return COMPETITION_PERIODS[periodNumber - 1];
        } else if (year == thisYear + 1) {
            return COMPETITION_PERIODS_NEXT_YEAR[periodNumber - 1];
        }
        throw new IllegalArgumentException("Year must be either this year or next");
    }

    public static CompetitionPeriod findPeriodByNumber(int periodNumber) {
        return COMPETITION_PERIODS[periodNumber - 1];
    }

    public static int getNumberOfPeriodsThisYear() {
        return COMPETITION_PERIODS.length;
    }

    public static int getNumberOfPeriods(int year) {
        int thisYear = getYear(new Date());
        if (year == thisYear) {
            return COMPETITION_PERIODS.length;
        }
        switch (year) {
            case 2020: //Covid year
                return 12;
            case 2021:
                return 13;
            default:
                return 16; //Normal year
        }
    }

    public static boolean qualifiesForSm(CompetitionPeriod period) {
        if (getYear(new Date()) == 2020) { //13-16 2019 & 1-6 2020
            return getYear(period.getStartDate()) == 2019 && period.getPeriodNumber() >= 13 && period.getPeriodNumber() <= 16
                    || getYear(period.getStartDate()) == 2020 && period.getPeriodNumber() >= 1 && period.getPeriodNumber() <= 6;
        }
        return getYear(new Date()) == getYear(period.getStartDate()) && period.getPeriodNumber() >= 1 && period.getPeriodNumber() <= 10;
    }

    private static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    boolean isValidAsEntryForPeriod(CompetitionPeriod period) {
        int yearOfPeriod = getYear(getStartDate());
        int yearDiff = getYear(period.getStartDate()) - yearOfPeriod;
        int diff = (yearDiff * getNumberOfPeriods(yearOfPeriod) + period.getPeriodNumber()) - getPeriodNumber();
        return diff > 0 && diff <= 10;
    }

    boolean isValidAsRankingForPeriod(CompetitionPeriod period) {
        int yearOfPeriod = getYear(period.getStartDate());
        int yearDiff = yearOfPeriod - getYear(getStartDate());
        int diff = (yearDiff * getNumberOfPeriods(yearOfPeriod) + period.getPeriodNumber()) - getPeriodNumber();
        return diff > 0 && diff <= 16;
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

    public int getYear() {
        return CompetitionPeriod.getYear(startDate);
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

    public static void main(String[] args) {
        int offsetInPeriodsfromLastYear = 32;
        CompetitionPeriod current = COMPETITION_PERIODS[10];
        CompetitionPeriod test = COMPETITION_PERIODS[9];
        System.out.println(current.getName() + " - " + test.getName() + ": " + test.isValidAsEntryForPeriod(current) + " : " + (
                test.isValidAsEntryForPeriod(current) == true));

        current = COMPETITION_PERIODS[10];
        test = COMPETITION_PERIODS[0];
        System.out.println(current.getName() + " - " + test.getName() + ": " + test.isValidAsEntryForPeriod(current) + " : " + (
                test.isValidAsEntryForPeriod(current) == true));

        current = COMPETITION_PERIODS[11];
        test = COMPETITION_PERIODS[0];
        System.out.println(current.getName() + " - " + test.getName() + ": " + test.isValidAsEntryForPeriod(current) + " : " + (
                test.isValidAsEntryForPeriod(current) == false));

        current = COMPETITION_PERIODS[10];
        test = COMPETITION_PERIODS_PREVIOUS_YEARS[offsetInPeriodsfromLastYear + 15];
        System.out.println(current.getName() + " - " + test.getName() + ": " + test.isValidAsEntryForPeriod(current) + " : " + (
                test.isValidAsEntryForPeriod(current) == false));

        current = COMPETITION_PERIODS[9];
        test = COMPETITION_PERIODS_PREVIOUS_YEARS[offsetInPeriodsfromLastYear + 15];
        System.out.println(current.getName() + " - " + test.getName() + ": " + test.isValidAsEntryForPeriod(current) + " : " + (
                test.isValidAsEntryForPeriod(current) == true));

        current = COMPETITION_PERIODS[9];
        test = COMPETITION_PERIODS_PREVIOUS_YEARS[offsetInPeriodsfromLastYear + 14];
        System.out.println(current.getName() + " - " + test.getName() + ": " + test.isValidAsEntryForPeriod(current) + " : " + (
                test.isValidAsEntryForPeriod(current) == false));

        current = COMPETITION_PERIODS[6];
        test = COMPETITION_PERIODS_PREVIOUS_YEARS[offsetInPeriodsfromLastYear + 12];
        System.out.println(current.getName() + " - " + test.getName() + ": " + test.isValidAsEntryForPeriod(current) + " : " + (
                test.isValidAsEntryForPeriod(current) == true));

        current = COMPETITION_PERIODS[6];
        test = COMPETITION_PERIODS_PREVIOUS_YEARS[offsetInPeriodsfromLastYear + 11];
        System.out.println(current.getName() + " - " + test.getName() + ": " + test.isValidAsEntryForPeriod(current) + " : " + (
                test.isValidAsEntryForPeriod(current) == false));

        current = COMPETITION_PERIODS[6];
        test = COMPETITION_PERIODS[7];
        System.out.println(current.getName() + " - " + test.getName() + ": " + test.isValidAsEntryForPeriod(current) + " : " + (
                test.isValidAsEntryForPeriod(current) == false));

        current = COMPETITION_PERIODS[6];
        test = COMPETITION_PERIODS[6];
        System.out.println(current.getName() + " - " + test.getName() + ": " + test.isValidAsEntryForPeriod(current) + " : " + (
                test.isValidAsEntryForPeriod(current) == false));

        current = COMPETITION_PERIODS_PREVIOUS_YEARS[offsetInPeriodsfromLastYear + 15];
        test = COMPETITION_PERIODS[0];
        System.out.println(current.getName() + " - " + test.getName() + ": " + test.isValidAsEntryForPeriod(current) + " : " + (
                test.isValidAsEntryForPeriod(current) == false));

        current = COMPETITION_PERIODS[0];
        test = COMPETITION_PERIODS_PREVIOUS_YEARS[offsetInPeriodsfromLastYear + 15];
        System.out.println(current.getName() + " - " + test.getName() + ": " + test.isValidAsEntryForPeriod(current) + " : " + (
                test.isValidAsEntryForPeriod(current) == true));

        current = COMPETITION_PERIODS[15];
        test = COMPETITION_PERIODS[5];
        System.out.println(current.getName() + " - " + test.getName() + ": " + test.isValidAsEntryForPeriod(current) + " : " + (
                test.isValidAsEntryForPeriod(current) == true));

        current = COMPETITION_PERIODS[15];
        test = COMPETITION_PERIODS[4];
        System.out.println(current.getName() + " - " + test.getName() + ": " + test.isValidAsEntryForPeriod(current) + " : " + (
                test.isValidAsEntryForPeriod(current) == false));

        current = COMPETITION_PERIODS[15];
        test = COMPETITION_PERIODS[3];
        System.out.println(current.getName() + " - " + test.getName() + ": " + test.isValidAsEntryForPeriod(current) + " : " + (
                test.isValidAsEntryForPeriod(current) == false));

        current = COMPETITION_PERIODS[14];
        test = COMPETITION_PERIODS[4];
        System.out.println(current.getName() + " - " + test.getName() + ": " + test.isValidAsEntryForPeriod(current) + " : " + (
                test.isValidAsEntryForPeriod(current) == true));

        current = COMPETITION_PERIODS[14];
        test = COMPETITION_PERIODS[3];
        System.out.println(current.getName() + " - " + test.getName() + ": " + test.isValidAsEntryForPeriod(current) + " : " + (
                test.isValidAsEntryForPeriod(current) == false));
    }
}
