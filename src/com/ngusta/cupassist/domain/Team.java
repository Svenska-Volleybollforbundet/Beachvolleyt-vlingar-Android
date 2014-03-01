package com.ngusta.cupassist.domain;

import java.io.Serializable;

public class Team implements Serializable {
    private Player playerA;
    private Player playerB;

    public Team(Player playerA, Player playerB) {
        this.playerA = playerA;
        this.playerB = playerB;
    }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }
}
