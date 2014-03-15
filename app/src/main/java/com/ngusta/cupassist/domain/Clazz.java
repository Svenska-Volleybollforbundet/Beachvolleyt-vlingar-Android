package com.ngusta.cupassist.domain;

public enum Clazz {
    MEN, WOMEN, MIXED, U13F, U13P, U15F, U15P, U17F, U17P, U19F, U19P, U21F, U21P, V35D, V35H, V40D, V40H, V45D, V45H, V55D, V55H, UNKNOWN;

    public static Clazz parse(String clazzString) {
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
}
