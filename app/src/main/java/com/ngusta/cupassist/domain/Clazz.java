package com.ngusta.cupassist.domain;

import java.util.Arrays;
import java.util.List;

public enum Clazz {
    MEN("Herr"), WOMEN("Dam"), MIXED("Mixed"),
    D23("U23 D"), H23("H23 H"),
    F19("U19 F"), P19("U19 P"),
    F18("U18 F"), P18("U18 P"), M18("U18 M"),
    F17("U17 F"), P17("U17 P"), M17("U17 M"),
    F16("U16 F"), P16("U16 P"), M16("U16 M"),
    F15("U15 F"), O15("U15 Ö"), M15("U15 M"),
    MiniFLevel5("Mini F Level 5"), MiniFLevel4("Mini F Level 4"),
    MiniOLevel5("Mini Ö Level 5"), MiniOLevel4("Mini Ö Level 4"),
    JuniorD("Junior D"), JuniorH("Junior H"), JuniorM("Junior M"),
    V35D("V35+ D"), V35H("V35+ H"),
    V40D("V40+ D"), V40H("V40+ H"),
    V45D("V45+ D"), V45H("V45+ H"),
    V50D("V50+ D"), V50H("V50+ H"),
    V55D("V55+ D"), V55H("V55+ H"),
    V60D("V60+ D"), V60H("V60+ H"),
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
            case "U19 F":
            case "U19 F (03-)":
                return F19;
            case "U19 P":
            case "U19 P (03-)":
                return P19;
            case "U18 F":
                return F18;
            case "U18 P":
                return P18;
            case "U18 M":
            case "U18 M (04-)":
                return M18;
            case "U17 F":
            case "U17 F (05-)":
                return F17;
            case "U17 P":
            case "U17 P (05-)":
                return P17;
            case "U17 M":
                return M17;
            case "U16 F":
            case "U16 F (06-)":
                return F16;
            case "U16 P":
            case "U16 P (06-)":
                return P16;
            case "U16 M":
                return M16;
            case "U15 F":
            case "U15 F (07-)":
                return F15;
            case "U15 Ö":
            case "U15 Ö (07-)":
                return O15;
            case "U15 M":
            case "U15 M (07-)":
                return M15;
            case "Mini Ö Level 5":
            case "Mini Ö Level 5 (08-)":
            case "L5 Ö GB (08-)":
            case "L5 Ö RS (08-)":
                return MiniOLevel5;
            case "Mini Ö Level 4":
            case "Mini Ö Level 4 (08-)":
            case "L4 Ö GB (08-)":
            case "L4 Ö RS (08-)":
                return MiniOLevel4;
            case "Mini F Level 5":
            case "Mini F Level 5 (08-)":
            case "L5 F GB (08-)":
            case "L5 F RS (08-)":
                return MiniFLevel5;
            case "Mini F Level 4":
            case "Mini F Level 4 (08-)":
            case "L4 F GB (08-)":
            case "L4 F RS (08-)":
                return MiniFLevel4;
            case "Junior D":
            case "Junior D (02-)":
                return JuniorD;
            case "Junior H":
            case "Junior H (02-)":
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
            case "V60+ D":
            case "V60+ D (-60)":
                return V60D;
            case "V60+ H":
            case "V60+ H (-60)":
                return V60H;
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
        Clazz[] youthClazzes = {D23, H23, F19, P19, F18, P18, M18, F17, P17, M17, F16, P16, M16, F15, O15, M15, MiniOLevel5, MiniOLevel4, MiniFLevel5,
                MiniFLevel4,
                JuniorD, JuniorH, JuniorM, UNKNOWN};
        return Arrays.asList(youthClazzes);
    }

    public static List<Clazz> getVeteranClazzes() {
        Clazz[] youthClazzes = {V35D, V35H, V40D, V40H, V45D, V45H, V50D, V50H, V55D, V55H, V60D, V60H, UNKNOWN};
        return Arrays.asList(youthClazzes);
    }
}
