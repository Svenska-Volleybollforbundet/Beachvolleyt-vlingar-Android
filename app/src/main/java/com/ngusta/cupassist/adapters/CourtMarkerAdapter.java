package com.ngusta.cupassist.adapters;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.domain.Court;
import com.ngusta.cupassist.domain.CourtWithKeyTag;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
        CourtWithKeyTag tag = (CourtWithKeyTag) marker.getTag();
        if (tag == null) {
            return null;
        }
        Court court = tag.court;
        setTextForId(R.id.title, court.getTitle() + " - " + court.getNumCourts() + (court.getNumCourts() == 1 ? " bana" : " banor"));
        setTextForId(R.id.snippet, court.getDescription());

        ImageView iv = popup.findViewById(R.id.hasNet);
        iv.setImageResource(court.getHasNet() ? R.drawable.beach_net_green : R.drawable.beach_net_red);

        iv = popup.findViewById(R.id.hasLines);
        iv.setImageResource(court.getHasLines() ? R.drawable.volleyball_lines_green : R.drawable.volleyball_lines_red);

        iv = popup.findViewById(R.id.hasAntennas);
        iv.setImageResource(court.getHasAntennas() ? R.drawable.antenna_green : R.drawable.antenna_red);

        if (court.hasLink()) {
            setTextForId(R.id.link, court.getLink());
        } else {
            popup.findViewById(R.id.link).setVisibility(View.GONE);
        }
        return popup;
    }

    private void setTextForId(int viewId, String text) {
        TextView tv = popup.findViewById(viewId);
        tv.setText(text);
    }
}
