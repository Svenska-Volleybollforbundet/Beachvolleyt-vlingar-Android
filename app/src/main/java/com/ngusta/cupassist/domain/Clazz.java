package com.ngusta.cupassist.domain;

import java.util.Arrays;
import java.util.List;

public enum Clazz {
    MEN("Herr"), WOMEN("Dam"), MIXED("Mixed"),
    D23("U23 D"), H23("H23 H"),
    F18("U18 F"), P18("U18 P"), M18("U18 M"),
    F16("U16 F"), P16("U16 P"), M16("U16 M"),
    MiniFGronBla("Mini F GrönBlå"), MiniFRod("Mini F Röd"), MiniFSvart("Mini F Svart"),
    MiniOGronBla("Mini Ö GrönBlå"), MiniORod("Mini Ö Röd"), MiniOSvart("Mini Ö Svart"),
    JuniorD("Junior D"), JuniorH("Junior H"), JuniorM("Junior M"),
    V35D("V35+ D"), V35H("V35+ H"),
    V40D("V40+ D"), V40H("V40+ H"),
    V45D("V45+ D"), V45H("V45+ H"),
    V50D("V50+ D"), V50H("V50+ H"),
    V55D("V55+ D"), V55H("V55+ H"),
    UNKNOWN("Unknown");

    private final String clazzString;

    Clazz(String clazzString) {
        this.clazzString = clazzString;
    }

    @Override
    public String toString() {
        return clazzString;
    }

    public static Clazz parse(String clazzString) {
        return parse(clazzString, null);
    }

    public static Clazz parse(String clazzString, String tournamentName) {
        int leftParenthesisIndex = clazzString.indexOf("(");
        if (leftParenthesisIndex > -1) {
            clazzString = clazzString.substring(0, leftParenthesisIndex).trim();
        }
        switch (clazzString) {
            case "H":
            case "Herr":
                return MEN;
            case "D":
            case "Dam":
                return WOMEN;
            case "M":
            case "Mixed":
                return MIXED;
            case "U23 D":
                return D23;
            case "U23 H":
                return H23;
            case "U18 F":
                return F18;
            case "U18 P":
                return P18;
            case "U18 M":
                return M18;
            case "U16 F":
                return F16;
            case "U16 P":
                return P16;
            case "U16 M":
                return M16;
            case "Mini F GrönBlå":
            case "Flickor GrönBlå":
                return MiniFGronBla;
            case "Mini F Röd":
            case "Flickor Röd":
                return MiniFRod;
            case "Mini F Svart":
            case "Flickor Svart":
                return MiniFSvart;
            case "Mini Ö GrönBlå":
            case "Mini Öppen GrönBlå":
                return MiniOGronBla;
            case "Mini Ö Röd":
            case "Mini Öppen Röd":
                return MiniORod;
            case "Mini Ö Svart":
            case "Mini Öppen Svart":
                return MiniOSvart;
            case "Junior D":
                return JuniorD;
            case "Junior H":
                return JuniorH;
            case "Junior M":
                return JuniorM;
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
            case "V50+ D":
                return V50D;
            case "V50+ H":
                return V50H;
            case "V55+ D":
                return V55D;
            case "V55+ H":
                return V55H;
        }
        return guessClazz(tournamentName);
    }

    private static Clazz guessClazz(String tournamentName) {
        if (tournamentName == null) {
            return UNKNOWN;
        }
        if (tournamentName.contains("Mixed")) {
            return MIXED;
        }
        return UNKNOWN;
    }

    public static List<Clazz> getYouthClazzes() {
        Clazz[] youthClazzes = {D23, H23, F18, P18, M18, F16, P16, M16, MiniFGronBla, MiniFRod, MiniFSvart, MiniOGronBla, MiniORod, MiniOSvart,
                JuniorD, JuniorH, JuniorM, UNKNOWN};
        return Arrays.asList(youthClazzes);
    }

    public static List<Clazz> getVeteranClazzes() {
        Clazz[] youthClazzes = {V35D, V35H, V40D, V40H, V45D, V45H, V50D, V50H, V55D, V55H, UNKNOWN};
        return Arrays.asList(youthClazzes);
    }
}
