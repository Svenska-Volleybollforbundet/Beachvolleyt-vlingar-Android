package com.ngusta.cupassist.domain;

import com.google.android.gms.maps.model.Marker;

public class CourtWithKeyTag {

    public String key;

    public Court court;

    public CourtWithKeyTag(String key, Court court) {
        this.key = key;
        this.court = court;
    }

    public static CourtWithKeyTag getTag(Marker marker) {
        CourtWithKeyTag tag = (CourtWithKeyTag) marker.getTag();
        assert tag != null;
        return tag;
    }
}
