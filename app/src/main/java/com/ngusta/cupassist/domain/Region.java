package com.ngusta.cupassist.domain;

import java.util.List;

import static java.util.Arrays.asList;

public enum Region {
    NORTH(asList("Back to the beach", "IK Studenterna i Umeå", "Lule Volley", "Härnösands VBK", "Stöcke IF", "IFK Söderhamn")),
    EAST(asList("Back to the beach", "08 Beachvolley", "08 Beachvolley Club", "Bromma KFUK-KFUM", "Fyrishov BC", "Södertelge VBK", "Stockholm BVC", "Linköping Beach AC",
            "Team Gotland Volleybollklubb", "Sollentuna VK", "Västerås VBK", "Team Gotland", "Nyköpings FK", "Sigtuna BVC")),
    WEST(asList("Back to the beach", "08 Beachvolley Club", "Beachbrothers BC", "Kalmar VBK", "Mariestads VBK", "Morgondagens BC", "Göteborg BC", "Habo WK 87",
            "Karlstads VK", "Habo Wolley", "Västra Ämterviks IF", "Falköpings VK", "VBF RIG Falköping", "Trollhättans VK", "Skara Volleybollklubb", "IK Ymer", "Mölltorp Beachvolley", "SVBF")),
    SOUTH(asList("Back to the beach", "Jönköpings BC", "Föreningen Beachvolstey-Aid", "Beachvolley-Aid", "Växjö VK", "Ljungby VBK", "KFUM Gymnastik & IA Karskrona",
            "Karlskrona Volleyboll", "Malmö BC", "Slow Down",
            "Lunds VK", "Svedala VBK", "IFK Helsingborg", "Falkenbergs VBK", "KFUM Kristianstad Volleybollklubb", "KFUM Kristianstad"));

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
        System.err.println("Club " + club + " is not part of a region.");
        return null;
    }
}
