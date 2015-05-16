package com.ngusta.cupassist.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PlayerList implements Serializable {

    private Map<String, Player> players;

    private CompetitionPeriod competitionPeriodOfSave;

    private Date created;

    public PlayerList(CompetitionPeriod competitionPeriodOfSave) {
        this.competitionPeriodOfSave = competitionPeriodOfSave;
        this.players = new HashMap<>();
        this.created = new Date();
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, Player> players) {
        this.players = players;
    }

    public boolean isFromCurrentCompetitionPeriod() {
        return competitionPeriodOfSave.equals(CompetitionPeriod.findPeriodByDate(new Date()));
    }

    public Date getCreated() {
        return created;
    }
}
