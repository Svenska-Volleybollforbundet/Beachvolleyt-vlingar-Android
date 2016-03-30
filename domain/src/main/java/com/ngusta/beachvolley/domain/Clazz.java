package com.ngusta.beachvolley.domain;

import java.util.Arrays;
import java.util.List;

public enum Clazz {
    MEN("H"), WOMEN("D"), MIXED("M"), U13F("U13 F"), U13P("U13 P"), U15F("U15 F"), U15P("U15 P"),
    U17F("U17 F"), U17P("U17 P"), U19F("U19 F"), U19P("U19 P"), U21F("U21 F"), U21P("U21 P"),
    V35D("V35+ D"), V35H("V35+ H"), V40D("V40+ D"), V40H("V40+ H"), V45D("V45+ D"), V45H("V45+ H"),
    V55D("V55+ D"), V55H("V55+ H"), UNKNOWN("Unknown");

    private final String clazzString;

    private Clazz(String clazzString) {
        this.clazzString = clazzString;
    }

    public String getInitialLetter() {
        return clazzString.substring(0, 1);
    }

    @Override
    public String toString() {
        return clazzString;
    }

    public static Clazz parse(String clazzString) {
        int leftParenthesisIndex = clazzString.indexOf("(");
        if (leftParenthesisIndex > -1) {
            clazzString = clazzString.substring(0, leftParenthesisIndex).trim();
        }
        switch (clazzString) {
            case "H":
                return MEN;
            case "D":
                return WOMEN;
            case "M":
                return MIXED;
            case "U13 F":
                return U13F;
            case "U13 P":
                return U13P;
            case "U15 F":
                return U15F;
            case "U15 P":
                return U15P;
            case "U17 F":
                return U17F;
            case "U17 P":
                return U17P;
            case "U19 F":
                return U19F;
            case "U19 P":
                return U19P;
            case "U21 F":
                return U21F;
            case "U21 P":
                return U21P;
            case "V35+ D":
                return V35D;
            case "V35+ H":
                return V35H;
            case "V40+ D":
                return V40D;
            case "V40+ H":
                return V40H;
            case "V45+ D":
                return V45D;
            case "V45+ H":
                return V45H;
            case "V55+ D":
                return V55D;
            case "V55+ H":
                return V55H;
        }
        return UNKNOWN;
    }

    public static List<Clazz> getYouthClazzes() {
        Clazz[] youthClazzes = {U13F, U13P, U15F, U15P, U17F, U17P, U19F, U19P, U21F, U21P, UNKNOWN};
        return Arrays.asList(youthClazzes);
    }

    public static List<Clazz> getVeteranClazzes() {
        Clazz[] youthClazzes = {V35D, V35H, V40D, V40H, V45D, V45H, V55D, V55H, UNKNOWN};
        return Arrays.asList(youthClazzes);
    }
}
