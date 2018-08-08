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
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.ngusta.cupassist.domain.CourtWithKeyTag.getTag;

public class CourtDialogs {

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

        final EditText titleBox = new EditText(context);
        final EditText numberOfCourtsBox = new EditText(context);
        final EditText descriptionBox = new EditText(context);
        final EditText linkBox = new EditText(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.edit_marker_info_title)
                .setMultiChoiceItems(booleanProperties, valuesOfBooleanProperties, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        valuesOfBooleanProperties[which] = isChecked;
                    }
                })
                .setView(createEditBoxesLayout(context, tag, titleBox, numberOfCourtsBox, descriptionBox, linkBox))
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
                String numCourts = numberOfCourtsBox.getText().toString();
                if (!linkText.isEmpty() && !(linkText.startsWith("http://") || linkText.startsWith("https://"))) {
                    Toast.makeText(context, R.string.link_validation_message, Toast.LENGTH_LONG).show();
                    return;
                }
                if (!numCourts.isEmpty() && !numCourts.matches("[0-9]|[1-9][0-9]")) {
                    Toast.makeText(context, R.string.num_courts_validation_message, Toast.LENGTH_LONG).show();
                    return;
                }
                tag.court.setHasNet(valuesOfBooleanProperties[0]);
                tag.court.setHasLines(valuesOfBooleanProperties[1]);
                tag.court.setHasAntennas(valuesOfBooleanProperties[2]);
                tag.court.setTitle(titleBox.getText().toString());
                tag.court.setNumCourts(numCourts.isEmpty() ? 0 : Integer.parseInt(numCourts));
                tag.court.setDescription(descriptionBox.getText().toString());
                tag.court.setLink(linkText.isEmpty() ? null : linkText);
                courtService.updateCourt(tag.key, tag.court);
                marker.showInfoWindow();
                dialog.dismiss();
            }
        });
    }

    @NonNull
    private static LinearLayout createEditBoxesLayout(Context context, CourtWithKeyTag tag, EditText titleBox, EditText numberOfCourtsBox,
            EditText descriptionBox, EditText linkBox) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        addEditTextRow(context, layout, R.string.court_title_label, R.string.court_title_hint, tag.court.getTitle(), InputType.TYPE_CLASS_TEXT,
                titleBox);
        addEditTextRow(context, layout, R.string.court_number_label, R.string.court_number_hint, String.valueOf(tag.court.getNumCourts()),
                InputType.TYPE_CLASS_NUMBER, numberOfCourtsBox);
        addEditTextRow(context, layout, R.string.description_label, R.string.description_hint, tag.court.getDescription(),
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE, descriptionBox);
        addEditTextRow(context, layout, R.string.link_label, R.string.link_hint, tag.court.getLink(),
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI, linkBox);

        return layout;
    }

    private static void addEditTextRow(Context context, LinearLayout layout, int label, int hint, String text, int inputType, EditText box) {
        LinearLayout horLayout = new LinearLayout(context);
        horLayout.setOrientation(LinearLayout.HORIZONTAL);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = context.getResources().getDimensionPixelSize(R.dimen.dialog_margin);

        TextView labelView = new TextView(context);
        labelView.setText(label);
        labelView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        labelView.setLayoutParams(params);
        horLayout.addView(labelView);

        box.setHint(hint);
        box.setText(text);
        box.setInputType(inputType);
        box.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        horLayout.addView(box);
        layout.addView(horLayout);
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

    public static void showConfirmCreateMarkerDialog(Context context, final CourtService courtService, final double latitude,
            final double longitude) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.confirm_create_court_title)
                .setMessage(R.string.confirm_create_message)
                .setPositiveButton(R.string.create_court, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        courtService.addCourt(latitude, longitude);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }
}