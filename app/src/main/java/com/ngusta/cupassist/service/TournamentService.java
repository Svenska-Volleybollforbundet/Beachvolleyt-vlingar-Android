package com.ngusta.cupassist.service;

import com.ngusta.cupassist.domain.CompetitionPeriod;
import com.ngusta.cupassist.domain.Tournament;
import com.ngusta.cupassist.io.TournamentListCache;

import android.content.Context;

import java.util.Date;
import java.util.List;

public class TournamentService {

    private TournamentListCache tournamentListCache;
    private PlayerService playerService;

    public TournamentService() {
        tournamentListCache = new TournamentListCache();
        playerService = new PlayerService();
    }

    public TournamentService(Context context) {
        tournamentListCache = new TournamentListCache(context);
        playerService = new PlayerService(context);
    }


    public List<Tournament> getAllTournaments() {
        return tournamentListCache.getTournaments();
    }

    public void loadTournamentDetails(Tournament tournament) {
        tournamentListCache.getTournamentDetails(tournament, playerService.getPlayers());
    }
}
