package com.ngusta.cupassist.domain;

import java.util.List;

import static java.util.Arrays.asList;

public enum Region {
    NORTH(asList("IK Studenterna", "Lule Volley")),
    STOCKHOLM(asList("Beachhallen BVC", "Bromma KFUK-KFUM", "Fyrishov BC", "Södertelge VBK", "Beachbrothers BC", "Stockholm BVC")),
    MIDDLE(asList("Linköping Beach AC", "KFUM Gymnastik & IA Karskrona", "Jönköpings BC", "Ljungby VBK", "Föreningen Beachvolley-Aid", "Kalmar VBK",
            "Växjö VK", "Mariestads VBK")),
    SOUTH(asList("Göteborg BC", "Malmö BC", "Morgondagens BC", "Lunds VK", "Svedala VBK", "IFK Helsingborg"));

    private List<String> clubs;

    Region(List<String> clubs) {
        this.clubs = clubs;
    }

    public static Region findRegionByClub(String club) {
        if (NORTH.clubs.contains(club)) {
            return NORTH;
        }
        if (STOCKHOLM.clubs.contains(club)) {
            return STOCKHOLM;
        }
        if (MIDDLE.clubs.contains(club)) {
            return MIDDLE;
        }
        if (SOUTH.clubs.contains(club)) {
            return SOUTH;
        }
        return null;
    }
}
