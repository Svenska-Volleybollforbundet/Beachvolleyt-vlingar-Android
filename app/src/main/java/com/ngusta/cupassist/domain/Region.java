package com.ngusta.cupassist.domain;

import java.util.List;

import static java.util.Arrays.asList;

public enum Region {
    NORTH(asList("Back to the beach", "IK Studenterna i Umeå", "Lule Volley", "Härnösands VBK")),
    EAST(asList("Back to the beach", "08 Beachvolley", "Bromma KFUK-KFUM", "Fyrishov BC", "Södertelge VBK", "Stockholm BVC", "Linköping Beach AC",
            "Team Gotland Volleybollklubb")),
    WEST(asList("Back to the beach", "Beachbrothers BC", "Kalmar VBK", "Mariestads VBK", "Morgondagens BC", "Göteborg BC", "Habo WK 87",
            "Karlstads VK")),
    SOUTH(asList("Back to the beach", "Jönköpings BC", "Föreningen Beachvolstey-Aid", "Växjö VK", "Ljungby VBK", "KFUM Gymnastik & IA Karskrona",
            "Malmö BC",
            "Lunds VK", "Svedala VBK", "IFK Helsingborg", "Falkenbergs VBK", "KFUM Kristianstad Volleybollklubb"));

    private List<String> clubs;

    Region(List<String> clubs) {
        this.clubs = clubs;
    }

    public static Region findRegionByClub(String club) {
        if (NORTH.clubs.contains(club)) {
            return NORTH;
        }
        if (EAST.clubs.contains(club)) {
            return EAST;
        }
        if (WEST.clubs.contains(club)) {
            return WEST;
        }
        if (SOUTH.clubs.contains(club)) {
            return SOUTH;
        }
        return null;
    }
}
