package com.ngusta.cupassist.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class Tournament implements Serializable {
    private Date startDate;
    private String period;
    private String club;
    private String name;
    private String url;
    private String level;
    private String classes;
    private String redirectUrl;
    private int maxNumberOfTeams;
    private List<Team> teams;

    public Tournament(Date startDate, String period, String club, String name, String url, String level, String classes) {
        this.startDate = startDate;
        this.period = period;
        this.club = club;
        this.name = name;
        this.url = url;
        this.level = level;
        this.classes = classes;
        maxNumberOfTeams = 16;//TODO Parse this info
    }

    public String getFormattedStartDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(startDate);
    }

    public List<Team> getSeededTeams() {
        List<Team> seeded = new ArrayList<>(teams);
        Collections.sort(seeded, new Comparator<Team>() {
            @Override
            public int compare(Team lhs, Team rhs) {
                if (lhs.getRegistrationDate() == null) {
                    return rhs.getRegistrationDate() == null ? 0 : -1;
                }
                return lhs.getRegistrationDate().compareTo(rhs.getRegistrationDate());
            }
        });
        if (seeded.size() > maxNumberOfTeams) {
            seeded = seeded.subList(0, maxNumberOfTeams);
        }
        Collections.sort(seeded);
        return seeded;
    }

    public String getPeriod() {
        return period;
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

    public String getLevel() {
        return level;
    }

    public String getClasses() {
        return classes;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public void addTeam(Team team) {
        if (teams == null) {
            teams = new ArrayList<>();
        }
        teams.add(team);
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "startDate=" + getFormattedStartDate() +
                ", period='" + period + '\'' +
                ", club='" + club + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", level='" + level + '\'' +
                ", classes='" + classes + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                '}';
    }
}