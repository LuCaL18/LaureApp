package com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogs;

import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.CreaTesiFragment;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.VisualizeTesiFragment;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class ConstraintsDialog {

    private final AlertDialog dialog;
    private int timeWeeks;
    private final EditText textExamN;
    private final ChipGroup chipsContainer;
    private final List<String> exams;

    public ConstraintsDialog(AlertDialog dialog, View vincoliPopup, VisualizeTesiFragment requiredFragment, Tesi thesis) {
        this.dialog = dialog;

        timeWeeks = thesis.getTempistiche();
        exams = thesis.getEsami() != null ? thesis.getEsami() : new ArrayList<>();

        textExamN = vincoliPopup.findViewById(R.id.text_input_edit_text);
        chipsContainer = vincoliPopup.findViewById(R.id.chips_exam_container);

        for(String exam : exams) {
            createChip(exam);
        }

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

        textExamN.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    createChip(null);
                    return true;
                }
                return false;
            }
        });

        textExamN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().endsWith("\n")) {
                    createChip(null);
                }
            }
        });

        timeline.addOnChangeListener((slider, value, fromUser) -> {
            timelineTextView.setText(vincoliPopup.getContext().getString(R.string.timeline_value_weeks, String.valueOf((int) value)));
            timeWeeks = (int) value;
        });

        save.setOnClickListener(viewSave -> {

            requiredFragment.updateConstraints(timeWeeks, Float.parseFloat(voto.getText().toString()), exams, eSkill.getText().toString());
            dialog.dismiss();
        });

        cancel.setOnClickListener(viewCancel -> dialog.dismiss());
    }

    public ConstraintsDialog(AlertDialog dialog, View vincoliPopup, CreaTesiFragment requiredFragment) {
        this.dialog = dialog;

        exams = new ArrayList<>();

        textExamN = vincoliPopup.findViewById(R.id.text_input_edit_text);
        chipsContainer = vincoliPopup.findViewById(R.id.chips_exam_container);

        EditText eSkill = vincoliPopup.findViewById(R.id.CeS_edit);
        eSkill.setHint("Skill richieste");

        TextView timelineTextView = vincoliPopup.findViewById(R.id.temp_value);
        //timelineTextView.setText(requiredFragment.getString(R.string.timeline_value_weeks, String.valueOf(thesis.getTempistiche())));

        TextView voto = vincoliPopup.findViewById(R.id.voto_edit_constraint);
        //voto.setText(String.valueOf(thesis.getMediaVoto()));

        Slider media = vincoliPopup.findViewById(R.id.media_edit_slider_bar);
        //media.setValue(thesis.getMediaVoto());

        Slider timeline = vincoliPopup.findViewById(R.id.tempistiche_slider_bar);
        //timeline.setValue(timeWeeks);

        Button save = vincoliPopup.findViewById(R.id.saveButton);
        Button cancel = vincoliPopup.findViewById(R.id.cancelButton);

        //media.addOnChangeListener((slider, value, fromUser) -> voto.setText(String.valueOf(value)));

        textExamN.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    createChip(null);
                    return true;
                }
                return false;
            }
        });

        textExamN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().endsWith("\n")) {
                    createChip(null);
                }
            }
        });

        timeline.addOnChangeListener((slider, value, fromUser) -> {
            timelineTextView.setText(vincoliPopup.getContext().getString(R.string.timeline_value_weeks, String.valueOf((int) value)));
            timeWeeks = (int) value;
        });

        media.addOnChangeListener((slider, value, fromUser) -> voto.setText(String.valueOf(value)));

        save.setOnClickListener(viewSave -> {

            requiredFragment.updateConstraints(timeWeeks, Float.parseFloat(voto.getText().toString()), exams, eSkill.getText().toString());
            dialog.dismiss();
        });

        cancel.setOnClickListener(viewCancel -> dialog.dismiss());
    }

    public void show() {
        dialog.show();
    }

    private void createChip(@Nullable  String exam) {
        String text = exam;

        if(exam == null) {
            text = textExamN.getText().toString().trim();
        }

        if (!text.isEmpty()) {
            Chip chip = new Chip(dialog.getContext());
            chip.setText(text);
            chip.setChipBackgroundColorResource(R.color.primary_green);
            chip.setCloseIconTintResource(R.color.white);
            chip.setCloseIconVisible(true);
            chip.setTextColor(ContextCompat.getColor(dialog.getContext(), android.R.color.white));
            chipsContainer.addView(chip, new ChipGroup.LayoutParams(
                    ChipGroup.LayoutParams.WRAP_CONTENT,
                    ChipGroup.LayoutParams.WRAP_CONTENT));

            if(exam == null) {
                exams.add(text);
                textExamN.setText("");
            }

            String finalText = text;
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chipsContainer.removeView(chip);
                    exams.remove(finalText);
                }
            });
        }
    }
}
