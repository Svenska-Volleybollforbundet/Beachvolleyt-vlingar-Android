package com.ngusta.cupassist.activity;

import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.CompetitionPeriod;
import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.io.PlayerListDownloader;
import com.ngusta.cupassist.service.CourtService;
import com.ngusta.cupassist.service.PlayerService;
import com.ngusta.cupassist.service.TournamentService;

import android.app.Application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MyApplication extends Application {

    public static boolean RUN_AS_ANDROID_APP = true;

    public static final boolean CACHE_PLAYERS = true;

    public static final boolean CACHE_TOURNAMENTS = false;

    private PlayerService playerService;

    private TournamentService tournamentService;

    private CourtService courtService;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public PlayerService getPlayerService() {
        if (playerService == null) {
            playerService = new PlayerService(getApplicationContext());
        }
        return playerService;
    }

    public TournamentService getTournamentService() {
        if (tournamentService == null) {
            tournamentService = new TournamentService(getApplicationContext(), getPlayerService());
        }
        return tournamentService;
    }

    public CourtService getCourtService() {
        if (courtService == null) {
            courtService = new CourtService();
        }
        return courtService;
    }

    private static void desktopRun() throws IOException {
        readCourts();
/*      long start = System.currentTimeMillis();
        PlayerListDownloader playerListDownloader = new PlayerListDownloader();
        Map<String, Player> players = playerListDownloader.getPlayers();
        System.out.println("Players loaded, took " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        printPlayersWithSmEntry(players, playerListDownloader);
        System.out.println("Player results loaded, took " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        TournamentListCache tournamentListCache = new TournamentListCache();
        List<Tournament> tournaments = tournamentListCache.getTournaments();
        System.out.println(
                "Tournaments loaded, took " + (System.currentTimeMillis() - start) + " ms");
        for (Tournament tournament : tournaments) {
            if (tournament.getName().equals("BBVC Challenger II")) {

                System.out.println("\n" + tournament);
                tournamentListCache.getTournamentDetails(tournament, players);

                for (Tournament.TournamentClazz clazz : tournament.getClazzes()) {
                    for (Tournament.TeamGroupPosition groupTeamPair : tournament
                            .getTeamGroupPositionsForClazz(clazz)) {
                        int group = groupTeamPair.group;
                        Team team = groupTeamPair.team;
                        System.out.println(
                                group + " " + team.getPlayerA().getName() + " och " + team
                                        .getPlayerB().getName()
                                        + " entry: " + (team.getPlayerA().getEntryPoints() + team
                                        .getPlayerB().getEntryPoints()) + " Paid: " + team.hasPaid()
                        );
                    }
                    System.out.println();
                }
            }
        }*/
    }

    private static void readCourts() {

       /* try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream("courtsData.csv"), StandardCharsets.ISO_8859_1);
            List<String[]> csvCourts = new CSVReader(inputStreamReader, ';').readAll();
            boolean first = true;
            ArrayList<Court> courts = new ArrayList<>();
            for (String[] csvCourt : csvCourts) {
                if (first) {
                    System.out.println(Arrays.toString(csvCourt));
                    first = false;
                } else if (csvCourt[17].length() > 0 && csvCourt[18].length() > 0) {
                    //System.out.println(csvCourt[8] + "," + csvCourt[9] + "," + csvCourt[11] + "," + csvCourt[17] + "     " + csvCourt[18]);
                    double lat = Double.parseDouble(csvCourt[17]);
                    double lng = Double.parseDouble(csvCourt[18]);
                    String title = csvCourt[8];
                    String description = csvCourt[11].startsWith("Nej") || csvCourt[11].startsWith("nej") ? "" : csvCourt[11];
                    int numCourts = csvCourt[9].isEmpty() ? 1 : Integer.parseInt(csvCourt[9]);
                    boolean hasNet = csvCourt[13].equalsIgnoreCase("ja");
                    boolean hasLines = csvCourt[13].equalsIgnoreCase("ja");
                    courts.add(new Court(lat, lng, title, description, null, numCourts, hasNet, hasLines, false));
                }
            }
            System.out.println(new Gson().toJson(courts));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private static void printPlayersWithSmEntry(Map<String, Player> players, PlayerListDownloader playerListDownloader) {
        List<Player> women = new ArrayList<>();
        List<Player> men = new ArrayList<>();
        List<Player> mixed = new ArrayList<>();
        for (Map.Entry<String, Player> entry : players.entrySet()) {
            Player player = entry.getValue();
            if (player.getClazz() == Clazz.MEN) {
                men.add(player);
            }
            if (player.getClazz() == Clazz.WOMEN) {
                women.add(player);
            }
            if (player.getMixedPoints() != 0) {
                mixed.add(player);
            }
        }
        System.out.println("\nRanking,Namn,Entry,Rankingpoäng");
        printSmEntryForClazz(playerListDownloader, men, Clazz.MEN);
        printSmEntryForClazz(playerListDownloader, women, Clazz.WOMEN);
    }

    private static void printSmEntryForClazz(PlayerListDownloader playerListDownloader, List<Player> men, Clazz clazz) {
        List<Player> bestPlayers = new ArrayList<>();
        Collections.sort(men, new Player.CurrentEntryPlayerComparator(clazz));
        for (int i = 0; i < 100 && i < men.size(); i++) {
            Player player = men.get(i);
            playerListDownloader.updatePlayerWithDetailsAndResults(player);
            bestPlayers.add(player);
        }
        Collections.sort(bestPlayers, new Player.CompetitionPeriodPlayerComparator(clazz, CompetitionPeriod.findPeriodByNumber(11)));
        int rank = 1;
        for (Player player : bestPlayers) {
            System.out.println(rank++ + "," + player.getName() + "," +
                    player.getResults().getEntryForPeriod(CompetitionPeriod.findPeriodByNumber(11)) + "," +
                    player.getResults().getRankingPointsForPeriod(CompetitionPeriod.findPeriodByNumber(11)));
        }
    }

    public static void main(String[] args) {
        RUN_AS_ANDROID_APP = false;
        try {
            desktopRun();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
