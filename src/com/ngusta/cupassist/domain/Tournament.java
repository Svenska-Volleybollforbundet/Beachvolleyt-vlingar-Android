package com.ngusta.cupassist.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Tournament implements Serializable {
    private Date startDate;
    private String period;
    private String club;
    private String name;
    private String url;
    private String level;
    private String classes;
    private String redirectUrl;
    private ArrayList<Team> teams;

    public Tournament(Date startDate, String period, String club, String name, String url, String level, String classes) {
        this.startDate = startDate;
        this.period = period;
        this.club = club;
        this.name = name;
        this.url = url;
        this.level = level;
        this.classes = classes;
    }

    public String getFormattedStartDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(startDate);
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

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
    }

    public void addTeam(Team team) {
        if (teams == null) {
            teams = new ArrayList<Team>();
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