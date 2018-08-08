package com.ngusta.cupassist.adapters;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.activity.CourtActivity;
import com.ngusta.cupassist.domain.Court;
import com.ngusta.cupassist.domain.CourtWithKeyTag;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static com.ngusta.cupassist.domain.CourtWithKeyTag.getTag;

public class CourtMarkerAdapter implements GoogleMap.InfoWindowAdapter {

    private final CourtActivity courtActivity;

    private View popup;

    private final LayoutInflater layoutInflater;

    public CourtMarkerAdapter(LayoutInflater layoutInflater, CourtActivity courtActivity) {
        this.layoutInflater = layoutInflater;
        this.courtActivity = courtActivity;
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
        CourtWithKeyTag tag = getTag(marker);
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
            popup.findViewById(R.id.link).setVisibility(View.VISIBLE);
            setTextForId(R.id.link, court.getLink());
        } else {
            popup.findViewById(R.id.link).setVisibility(View.GONE);
        }

        ImageView actionIndicatorImg = popup.findViewById(R.id.popup_click_indicator);
        if (courtActivity.isEditEnabled()) {
            actionIndicatorImg.setVisibility(View.VISIBLE);
            actionIndicatorImg.setImageResource(android.R.drawable.ic_menu_edit);
        } else if (court.hasValidLink()) {
            actionIndicatorImg.setVisibility(View.VISIBLE);
            actionIndicatorImg.setImageResource(R.drawable.external_link_enabled);
        } else {
            actionIndicatorImg.setVisibility(View.INVISIBLE);
        }
        return popup;
    }

    private void setTextForId(int viewId, String text) {
        TextView tv = popup.findViewById(viewId);
        tv.setText(text);
    }
}
