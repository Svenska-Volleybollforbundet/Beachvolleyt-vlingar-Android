package com.ngusta.cupassist.domain;

import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tournament implements Serializable {
    public static final String TAG = Tournament.class.getSimpleName();
    private Date startDate;
    private CompetitionPeriod competitionPeriod;
    private String club;
    private String name;
    private String url;
    private Level level;
    private String levelString;

    private List<TournamentClazz> clazzes;

    private String registrationUrl;
    private Map<Clazz, List<Team>> teams;

    public Tournament(Date startDate, String period, String club, String name, String url, String level, String clazzes) {
        this.startDate = startDate;
        this.competitionPeriod = CompetitionPeriod.findByName(period);
        this.club = club;
        this.name = name;
        this.url = url;
        this.levelString = level;
        this.level = Level.parse(level);
        this.clazzes = parseClazzes(clazzes);
    }

    private List<TournamentClazz> parseClazzes(String clazzes) {
        ArrayList<TournamentClazz> parsedClazzes = new ArrayList<>();
        if (clazzes != null) {
            String[] clazzArray = clazzes.split(", ");
            for (String clazzString : clazzArray) {
                parsedClazzes.add(new TournamentClazz(Clazz.parse(clazzString)));
            }
        }
        return parsedClazzes;
    }

    public String getFormattedStartDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(startDate);
    }

    public List<Team> getSeededTeamsForClazz(TournamentClazz clazz) {
        if (getTeams().get(clazz.getClazz()) == null) {
            return Collections.emptyList();
        }
        List<Team> seeded = getTeams().get(clazz.getClazz());
        Collections.sort(seeded, level.getComparator());
        if (seeded.size() > clazz.getMaxNumberOfTeams()) {
            seeded = seeded.subList(0, clazz.getMaxNumberOfTeams());
        }
        Collections.sort(seeded);
        return seeded;
    }

    private int getNumberOfGroupsForClazz(TournamentClazz clazz) {
        if (clazz.getMaxNumberOfTeams() == 12) {
            return 4;
        } else {
            return clazz.getMaxNumberOfTeams() / 4;
        }
    }

    public CompetitionPeriod getCompetitionPeriod() {
        return competitionPeriod;
    }

    public String getClub() {
        return club;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getLevelString() {
        return levelString;
    }

    public List<TournamentClazz> getClazzes() {
        return clazzes != null ? clazzes : Collections.<TournamentClazz>emptyList();
    }

    public String getRegistrationUrl() {
        return registrationUrl;
    }

    public String getClassesWithMaxNumberOfTeamsString() {
        if (getClazzes().isEmpty()) {
            return "[]";
        }
        String str = "[";
        for (TournamentClazz clazz : getClazzes()) {
            str = str + clazz.getClazz() + "(" + clazz.getMaxNumberOfTeams() + "), ";
        }
        return str.substring(0, str.length() - 2) + "]";
    }

    public void setRegistrationUrl(String registrationUrl) {
        this.registrationUrl = registrationUrl;
    }

    public Map<Clazz, List<Team>> getTeams() {
        return teams != null ? teams : Collections.<Clazz, List<Team>>emptyMap();
    }

    public List<TeamGroupPosition> getTeamGroupPositionsForClazz(TournamentClazz clazz) {
        int group = 0;
        int operator = 1;
        int numberOfGroups = getNumberOfGroupsForClazz(clazz);
        List<TeamGroupPosition> teamGroupPositions = new ArrayList<>(clazz.getMaxNumberOfTeams());
        for (Team team : getSeededTeamsForClazz(clazz)) {
            group += operator;
            if (group == (numberOfGroups + 1)) {
                group = numberOfGroups;
                operator = -1;
            } else if (group == 0) {
                group = 1;
                operator = 1;
            }
            teamGroupPositions.add(new TeamGroupPosition(group, team));
        }

        return teamGroupPositions;
    }

    public void setMaxNumberOfTeams(Map<Clazz, Integer> maxNumberOfTeams) {
        clazzes.clear();
        for (Clazz clazz : maxNumberOfTeams.keySet()) {
            clazzes.add(new TournamentClazz(clazz, maxNumberOfTeams.get(clazz)));
        }
    }

    public void setTeams(List<Team> teamList) {
        teams = new HashMap<>();
        for (Team team : teamList) {
            if (!teams.containsKey(team.getClazz())) {
                teams.put(team.getClazz(), new ArrayList<Team>());
            }
            teams.get(team.getClazz()).add(team);
        }
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "startDate=" + getFormattedStartDate() +
                ", period='" + competitionPeriod.getName() + '\'' +
                ", club='" + club + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", level='" + level + '\'' +
                ", classes='" + clazzes + '\'' +
                ", redirectUrl='" + registrationUrl + '\'' +
                '}';
    }

    public enum Level {
        OPEN, OPEN_GREEN, CHALLENGER, SWEDISH_BEACH_TOUR, YOUTH, VETERAN, UNKNOWN;

        private static final Comparator<Team> REGISTRATION_TIME_COMPARATOR = new Comparator<Team>() {
            @Override
            public int compare(Team lhs, Team rhs) {
                if (lhs.getRegistrationTime() == null) {
                    return rhs.getRegistrationTime() == null ? 0 : -1;
                }
                return lhs.getRegistrationTime().compareTo(rhs.getRegistrationTime());
            }
        };

        private static final Comparator<Team> ENTRY_POINTS_COMPARATOR = new Comparator<Team>() {
            @Override
            public int compare(Team lhs, Team rhs) {
                return lhs.compareTo(rhs);
            }
        };

        public static Level parse(String levelString) {
            if (levelString == null) {
                return UNKNOWN;
            }
            if (levelString.contains("Open (Svart)")) {
                return OPEN;
            } else if (levelString.contains("Open (Grön)")) {
                return OPEN_GREEN;
            } else if (levelString.contains("Challenger")) {
                return CHALLENGER;
            } else if (levelString.contains("Swedish Beach Tour")) {
                return SWEDISH_BEACH_TOUR;
            } else if (levelString.contains("Veteran")) {
                return VETERAN;
            } else if (levelString.contains("Ungdom") || levelString.contains("3-beach") || levelString.contains("Kidsvolley")) {
                return YOUTH;
            }
            return UNKNOWN;
        }

        public Comparator<Team> getComparator() {
            switch (this) {
                case OPEN:
                case OPEN_GREEN:
                    return REGISTRATION_TIME_COMPARATOR;
                default:
                    return ENTRY_POINTS_COMPARATOR;
            }
        }
    }

    public static class TeamGroupPosition {
        public final int group;
        public final Team team;

        public TeamGroupPosition(int group, Team team) {
            this.group = group;
            this.team = team;
        }
    }

    public class TournamentClazz {

        private Clazz clazz;

        private Integer maxNumberOfTeams;

        TournamentClazz(Clazz clazz) {
            this.clazz = clazz;
        }

        public TournamentClazz(Clazz clazz, Integer maxNumberOfTeams) {
            this.clazz = clazz;
            this.maxNumberOfTeams = maxNumberOfTeams;
        }

        public Clazz getClazz() {
            return clazz;
        }

        public Integer getMaxNumberOfTeams() {
            if (maxNumberOfTeams != null) {
                return maxNumberOfTeams;
            } else {
                int guess = level == Level.OPEN ? 16 : 12;
                Log.i(TAG, String.format("Guessing number of teams in '%s' for clazz %s: %d", name,
                        clazz, guess));
                return guess;
            }
        }

        public void setMaxNumberOfTeams(Integer maxNumberOfTeams) {
            this.maxNumberOfTeams = maxNumberOfTeams;
        }
    }
}