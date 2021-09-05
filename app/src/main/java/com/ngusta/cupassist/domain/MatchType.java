package com.ngusta.cupassist.domain;

import android.support.annotation.Nullable;

import static java.lang.Integer.compare;

public class MatchType implements Comparable<MatchType> {
    private final String type;
    private final String clazz;

    public MatchType(String type, String clazz) {
        this.type = type.replaceFirst("-[0-9]", "");
        this.clazz = clazz;
    }

    @Override
    public int compareTo(MatchType o) {
        int clazzCompare = clazz.compareTo(o.clazz);
        if (clazzCompare != 0) {
            return clazzCompare;
        }
        return compare(getMatchTypeCompareNumber(), o.getMatchTypeCompareNumber());
    }

    private int getMatchTypeCompareNumber() {
        if (type.length() == 1) { //A, B, C, D...
            return ((int) type.toLowerCase().charAt(0)) - 96;
        }
        if (type.contains("Sextondel")) {
            return 20;
        }
        if (type.contains("Åttondel")) {
            return 25;
        }
        if (type.contains("Kvart") || type.contains("Quart")) {
            return 30;
        }
        if (type.contains("Semi")) {
            return 35;
        }
        if (type.contains("Final")) {
            return 40;
        }
        return -1;
    }

    @Override
    public String toString() {
        return clazz + "-" + type;
    }

    @Override
    public int hashCode() {
        return 1000*type.hashCode() + clazz.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        MatchType o = (MatchType) obj;
        return type.equals(o.type) && clazz.equals(o.clazz);
    }
}