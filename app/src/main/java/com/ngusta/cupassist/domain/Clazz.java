package com.ngusta.cupassist.domain;

import java.util.Arrays;
import java.util.List;

public enum Clazz {
    MEN("H"), WOMEN("D"), MIXED("M"),
    D98("D98"), H98("H98"),
    F00("U18 H"), P00("U18 P"), M00("U18 M"),
    F02("U16 H"), P02("U16 P"), M02("U16 M"),
    F03("Tremanna F"), O03("Tremanna O"), O07("Fyrmanna O"),
    V35D("V35+ D"), V35H("V35+ H"),
    V40D("V40+ D"), V40H("V40+ H"),
    V45D("V45+ D"), V45H("V45+ H"),
    V55D("V55+ D"), V55H("V55+ H"),
    UNKNOWN("Unknown");

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
            case "Herr":
                return MEN;
            case "D":
            case "Dam":
                return WOMEN;
            case "M":
            case "Mixed":
                return MIXED;
            case "D98":
                return D98;
            case "H98":
                return H98;
            case "F00":
                return F00;
            case "P00":
                return P00;
            case "M00":
                return M00;
            case "F02":
                return F02;
            case "P02":
                return P02;
            case "M02":
                return M02;
            case "F03":
                return F03;
            case "O03":
                return O03;
            case "O07":
                return O07;
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
        Clazz[] youthClazzes = {D98, H98, F00, P00, M00, F02, P02, M02, F03, O03, O07, UNKNOWN};
        return Arrays.asList(youthClazzes);
    }

    public static List<Clazz> getVeteranClazzes() {
        Clazz[] youthClazzes = {V35D, V35H, V40D, V40H, V45D, V45H, V55D, V55H, UNKNOWN};
        return Arrays.asList(youthClazzes);
    }
}
