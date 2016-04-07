package com.ngusta.cupassist.service;

import com.ngusta.beachvolley.domain.Player;
import com.ngusta.cupassist.io.FirebasePlayerList;
import com.ngusta.cupassist.io.PlayerListCache;

import android.content.Context;

import java.util.Map;

public class PlayerService {

    private PlayerListCache playerListCache;

    public PlayerService() {
        playerListCache = new PlayerListCache();
    }

    public PlayerService(Context context) {
        playerListCache = new FirebasePlayerList();
    }

    public Map<String, Player> getPlayers() {
        return playerListCache.getPlayers();
    }
}
