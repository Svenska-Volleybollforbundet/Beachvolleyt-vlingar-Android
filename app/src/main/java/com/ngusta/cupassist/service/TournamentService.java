package com.ngusta.cupassist.service;

import android.content.Context;
import com.ngusta.cupassist.domain.CompetitionPeriod;
import com.ngusta.cupassist.domain.Tournament;
import com.ngusta.cupassist.io.TournamentListCache;

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


    public List<Tournament> getTournamentsFromCurrentCompetitionPeriodAndLater() {
        CompetitionPeriod currentCompetitionPeriod = CompetitionPeriod.findPeriodByDate(new Date());
        List<Tournament> allTournaments = tournamentListCache.getTournaments();
        for (int i = 0; i < allTournaments.size(); i++) {
            if (allTournaments.get(i).getCompetitionPeriod().equals(currentCompetitionPeriod)) {
                return allTournaments.subList(i, allTournaments.size());
            }
        }
        return allTournaments;
    }

    public void loadTournamentTeams(Tournament tournament) {
        tournamentListCache.getTeams(tournament, playerService.getPlayers());
    }
}
