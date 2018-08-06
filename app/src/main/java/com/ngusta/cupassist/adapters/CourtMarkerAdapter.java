package com.ngusta.cupassist.adapters;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.domain.Court;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class CourtMarkerAdapter implements GoogleMap.InfoWindowAdapter {

    private View popup;

    private final LayoutInflater layoutInflater;

    public CourtMarkerAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        if (popup == null) {
            popup = layoutInflater.inflate(R.layout.court_marker_popup, null);
        }
        Court court = (Court) marker.getTag();
        if (court == null) {
            return null;
        }
        TextView tv = popup.findViewById(R.id.title);
        tv.setText(court.getTitle());

        tv = popup.findViewById(R.id.snippet);
        tv.setText(court.getDescription());

        tv = popup.findViewById(R.id.link);
        if (court.hasLink()) {
            tv.setVisibility(View.VISIBLE);
            tv.setText(court.getLink());
        } else {
            tv.setVisibility(View.GONE);
        }
        return popup;
    }
}
