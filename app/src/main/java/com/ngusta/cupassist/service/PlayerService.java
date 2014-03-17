package com.ngusta.cupassist.service;

import android.content.Context;
import com.ngusta.cupassist.domain.Player;
import com.ngusta.cupassist.io.PlayerListCache;

import java.util.Set;

public class PlayerService {

    private PlayerListCache playerListCache;

    public PlayerService() {
        playerListCache = new PlayerListCache();
    }

    public PlayerService(Context context) {
        playerListCache = new PlayerListCache(context);
    }

    public Set<Player> getPlayers() {
        return playerListCache.getPlayers();
    }
}
