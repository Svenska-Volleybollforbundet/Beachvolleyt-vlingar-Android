package com.ngusta.cupassist.service;

import android.content.Context;
import com.ngusta.cupassist.domain.Tournament;
import com.ngusta.cupassist.io.PlayerListCache;
import com.ngusta.cupassist.io.TournamentListCache;

import java.util.List;

public class TournamentService {

    private TournamentListCache tournamentListCache;
    private PlayerListCache playerListCache;

    public TournamentService() {
        tournamentListCache = new TournamentListCache();
        playerListCache = new PlayerListCache();
    }

    public TournamentService(Context context) {
        tournamentListCache = new TournamentListCache(context);
        playerListCache = new PlayerListCache(context);
    }


    public List<Tournament> getTournamentsFromCurrentCompetitionPeriod() {
        return tournamentListCache.getTournaments();
    }

    public void loadTournamentTeams(Tournament tournament) {
        tournamentListCache.getTeams(tournament, playerListCache.getPlayers());
    }
}
