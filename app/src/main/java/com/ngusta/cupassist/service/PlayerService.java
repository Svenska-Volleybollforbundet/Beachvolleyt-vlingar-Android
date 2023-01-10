package com.ngusta.cupassist.service;

import com.google.common.collect.HashMultimap;

import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.io.PlayerListDownloader;

import android.content.Context;

public class PlayerService {

    private PlayerListDownloader playerListDownloader;

    public PlayerService() {
        playerListDownloader = new PlayerListDownloader();
    }

    public PlayerService(Context context) {
        playerListDownloader = new PlayerListDownloader(context);
    }

    public HashMultimap<String, Player> getPlayers() {
        return playerListDownloader.getPlayers();
    }

    public void clearPlayers() {
        playerListDownloader.clearPlayers();
    }

    public boolean updatePlayerWithDetailsAndResults(Player player) {
        return playerListDownloader.updatePlayerWithDetailsAndResults(player);
    }
}
