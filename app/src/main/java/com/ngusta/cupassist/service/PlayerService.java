package com.ngusta.cupassist.service;

import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.io.PlayerListDownloader;

import android.content.Context;

import java.util.Map;

public class PlayerService {

    private PlayerListDownloader playerListDownloader;

    public PlayerService() {
        playerListDownloader = new PlayerListDownloader();
    }

    public PlayerService(Context context) {
        playerListDownloader = new PlayerListDownloader(context);
    }

    public Map<String, Player> getPlayers() {
        return playerListDownloader.getPlayers();
    }
}
