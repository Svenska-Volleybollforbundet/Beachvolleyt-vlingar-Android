package com.ngusta.cupassist.domain;

import com.google.common.collect.HashMultimap;

import java.io.Serializable;
import java.util.Date;

public class PlayerList implements Serializable {

    private HashMultimap<String, Player> players;

    private CompetitionPeriod competitionPeriodOfSave;

    private Date created;

    public PlayerList(CompetitionPeriod competitionPeriodOfSave) {
        this.competitionPeriodOfSave = competitionPeriodOfSave;
        this.players = HashMultimap.create();
        this.created = new Date();
    }

    public HashMultimap<String, Player> getPlayers() {
        return players;
    }

    public void setPlayers(HashMultimap<String, Player> players) {
        this.players = players;
    }

    public boolean isFromCurrentCompetitionPeriod() {
        return competitionPeriodOfSave.equals(CompetitionPeriod.findPeriodByDate(new Date()));
    }

    public Date getCreated() {
        return created;
    }
}
