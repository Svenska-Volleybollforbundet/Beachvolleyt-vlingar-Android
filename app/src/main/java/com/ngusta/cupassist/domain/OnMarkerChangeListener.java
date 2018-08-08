package com.ngusta.cupassist.domain;

import java.util.Map;

public interface OnMarkerChangeListener {

    void addMarkers(Map<String, Court> courts);

    void addMarker(String key, Court court);

    void removeMarker(String key);

    void updateMarker(String key, Court court);
}
