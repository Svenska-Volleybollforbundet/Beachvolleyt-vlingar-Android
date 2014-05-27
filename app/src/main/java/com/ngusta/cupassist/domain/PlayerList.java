package com.ngusta.cupassist.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlayerList implements Serializable {

    private Map<Clazz, Set<Player>> players;

    private CompetitionPeriod competitionPeriodOfSave;

    public PlayerList(CompetitionPeriod competitionPeriodOfSave) {
        this.competitionPeriodOfSave = competitionPeriodOfSave;
        this.players = new HashMap<>();
    }

    public Map<Clazz, Set<Player>> getPlayers() {
        return players;
    }

    public boolean isFromCurrentCompetitionPeriod() {
        return competitionPeriodOfSave.equals(CompetitionPeriod.findPeriodByDate(new Date()));
    }
}
