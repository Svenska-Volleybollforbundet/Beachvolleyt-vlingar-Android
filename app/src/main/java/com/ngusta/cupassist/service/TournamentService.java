package com.ngusta.cupassist.service;

import com.ngusta.beachvolley.domain.Tournament;
import com.ngusta.cupassist.io.FirebaseTournamentList;
import com.ngusta.cupassist.io.TournamentListCache;

import android.content.Context;

import java.io.IOException;
import java.util.List;

public class TournamentService {

    private TournamentListCache tournamentListCache;
    private PlayerService playerService;

    public TournamentService() {
        tournamentListCache = new TournamentListCache();
        playerService = new PlayerService();
    }

    public TournamentService(Context context, PlayerService playerService) {
        tournamentListCache = new FirebaseTournamentList();
        this.playerService = playerService;
    }

    public Tournament getTournamentById(int id) {
        for (Tournament tournament : tournamentListCache.getTournaments()) {
            if (tournament.getId() == id) {
                return tournament;
            }
        }
        throw new IllegalStateException("Id: " + id + " not a valid tournament id");
    }

    public List<Tournament> getAllTournaments() {
        return tournamentListCache.getTournaments();
    }

    public void loadTournamentDetails(Tournament tournament) throws IOException {
        tournamentListCache.getTournamentDetails(tournament, playerService.getPlayers());
    }
}
