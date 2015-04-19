package com.ngusta.cupassist.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class TournamentList implements Serializable {

    private static final long TIME_TO_CACHE = 24 * 60 * 60 * 1000;

    private List<Tournament> tournaments;

    private Date downloaded;

    public TournamentList(List<Tournament> tournaments) {
        this.tournaments = tournaments;
        downloaded = new Date();
    }

    public List<Tournament> getTournaments() {
        return tournaments;
    }

    public boolean isValid() {
        return new Date().getTime() - downloaded.getTime() < TIME_TO_CACHE;
    }
}
