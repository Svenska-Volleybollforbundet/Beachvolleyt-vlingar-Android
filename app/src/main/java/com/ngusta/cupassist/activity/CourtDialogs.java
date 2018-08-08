package com.ngusta.cupassist.activity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.domain.CourtWithKeyTag;
import com.ngusta.cupassist.service.CourtService;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import static com.ngusta.cupassist.domain.CourtWithKeyTag.getTag;

public class CourtDialogs {

    private static EditText descriptionBox;

    private static EditText linkBox;

    public static void showEditInfoDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.edit_help_title)
                .setMessage(R.string.edit_help)
                .setPositiveButton(R.string.ok, null)
                .create()
                .show();
    }

    public static void showConfirmMoveMarkerDialog(Context context, final CourtService courtService,
            final CourtWithKeyTag tag, final Marker marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.confirm_move_title)
                .setMessage(R.string.confirm_move_message)
                .setPositiveButton(R.string.move, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tag.court.setLat(marker.getPosition().latitude);
                        tag.court.setLng(marker.getPosition().longitude);
                        courtService.updateCourt(tag.key, tag.court);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        marker.setPosition(new LatLng(tag.court.getLat(), tag.court.getLng()));
                    }
                })
                .create()
                .show();
    }

    public static void showEditCourtInfoDialog(final Context context, final CourtService courtService, final Marker marker) {
        final CourtWithKeyTag tag = getTag(marker);

        final String[] booleanProperties = {"Har nät", "Har linjer", "Har antenner"};
        final boolean[] valuesOfBooleanProperties = {tag.court.getHasNet(), tag.court.getHasLines(), tag.court.getHasAntennas()};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.edit_marker_info_title)
                .setMultiChoiceItems(booleanProperties, valuesOfBooleanProperties, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        valuesOfBooleanProperties[which] = isChecked;
                    }
                })
                .setView(createEditBoxesLayout(context, tag))
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, null)
                .setNeutralButton(R.string.delete_court, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createConfirmDeleteMarkerDialog(context, courtService, tag.key);
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String linkText = linkBox.getText().toString().toLowerCase();
                if (!(linkText.startsWith("http://") || linkText.startsWith("https://"))) {
                    Toast.makeText(context, R.string.link_validation_message, Toast.LENGTH_LONG).show();
                    return;
                }
                tag.court.setHasNet(valuesOfBooleanProperties[0]);
                tag.court.setHasLines(valuesOfBooleanProperties[1]);
                tag.court.setHasAntennas(valuesOfBooleanProperties[2]);
                tag.court.setDescription(descriptionBox.getText().toString());
                tag.court.setLink(linkBox.getText().toString());
                courtService.updateCourt(tag.key, tag.court);
                marker.showInfoWindow();
                dialog.dismiss();
            }
        });
    }

    @NonNull
    private static LinearLayout createEditBoxesLayout(Context context, CourtWithKeyTag tag) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        descriptionBox = new EditText(context);
        descriptionBox.setHint(R.string.description_hint);
        descriptionBox.setText(tag.court.getDescription());
        descriptionBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
        layout.addView(descriptionBox);

        linkBox = new EditText(context);
        linkBox.setHint(R.string.link_hint);
        linkBox.setText(tag.court.getLink());
        linkBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        layout.addView(linkBox);

        return layout;
    }

    private static void createConfirmDeleteMarkerDialog(Context context, final CourtService courtService, final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.delete_court, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        courtService.removeCourt(key);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }
}