package com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogs;

import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.slider.Slider;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.VisualizeTesiFragment;

import java.util.ArrayList;

public class ConstraintsDialog {

    private final AlertDialog dialog;
    private int timeWeeks;

    public ConstraintsDialog(AlertDialog dialog, View vincoliPopup, VisualizeTesiFragment requiredFragment, Tesi thesis) {
        this.dialog = dialog;

        timeWeeks = thesis.getTempistiche();

        EditText eSkill = vincoliPopup.findViewById(R.id.CeS_edit);
        eSkill.setText(thesis.getSkill());

        TextView timelineTextView = vincoliPopup.findViewById(R.id.temp_value);
        timelineTextView.setText(requiredFragment.getString(R.string.timeline_value_weeks, String.valueOf(thesis.getTempistiche())));

        TextView voto = vincoliPopup.findViewById(R.id.voto_edit_constraint);
        voto.setText(String.valueOf(thesis.getMediaVoto()));

        Slider media = vincoliPopup.findViewById(R.id.media_edit_slider_bar);
        media.setValue(thesis.getMediaVoto());

        Slider timeline = vincoliPopup.findViewById(R.id.tempistiche_slider_bar);
        timeline.setValue(timeWeeks);

        Button save = vincoliPopup.findViewById(R.id.saveButton);
        Button cancel = vincoliPopup.findViewById(R.id.cancelButton);

        media.addOnChangeListener((slider, value, fromUser) -> voto.setText(String.valueOf(value)));

        timeline.addOnChangeListener((slider, value, fromUser) -> {
            timelineTextView.setText(vincoliPopup.getContext().getString(R.string.timeline_value_weeks, String.valueOf((int) value)));
            timeWeeks = (int) value;
        });

        save.setOnClickListener(viewSave -> {

            requiredFragment.updateConstraints(timeWeeks, Float.parseFloat(voto.getText().toString()), new ArrayList<>(), eSkill.getText().toString());
            dialog.dismiss();
        });

        cancel.setOnClickListener(viewCancel -> dialog.dismiss());
    }

    public void show() {
        dialog.show();
    }
}
