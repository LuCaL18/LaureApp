package com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogs;

import android.app.AlertDialog;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.MainViewModel;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.VisualizeTesiFragment;

import java.util.ArrayList;
import java.util.List;

public class CoRelatoreDialoog {

    private final AlertDialog dialog;
    private final List<PersonaTesi> coRelators;
    private final TextView popup_nome;
    private final EditText popup_email;
    private final View relatorePopup;
    private final Button save;
    private Button verifica, modifica;
    private final CheckBox permesso1, permesso2, permesso3, permesso4;
    private LoggedInUser selectedCoRelators;
    private LoggedInUser user;

    public CoRelatoreDialoog(AlertDialog dialog, View view, List<PersonaTesi> coRelators, VisualizeTesiFragment requireFragment) {
        this.dialog = dialog;
        this.coRelators = coRelators;
        relatorePopup = view;

        MainViewModel model = new ViewModelProvider(requireFragment.requireActivity()).get(MainViewModel.class);
        user = model.getUser().getValue();

        popup_nome = view.findViewById(R.id.nome);
        popup_email = view.findViewById(R.id.email);

        permesso1 = view.findViewById(R.id.permesso1);
        permesso2 = view.findViewById(R.id.permesso2);
        permesso3 = view.findViewById(R.id.permesso3);
        permesso4 = view.findViewById(R.id.permesso4);

        save = view.findViewById(R.id.saveButton);
        Button cancel = view.findViewById(R.id.cancelButton);

        impostaVerifica();

        save.setOnClickListener(viewSave -> {

            if (selectedCoRelators != null) {
                PersonaTesi coRelatore = new PersonaTesi(selectedCoRelators.getId(), selectedCoRelators.getDisplayName(), selectedCoRelators.getEmail(), checkPermessi());
                requireFragment.addCoRelator(coRelatore);
            }

            dialog.dismiss();
        });

        cancel.setOnClickListener(viewCancel -> dialog.dismiss());
    }

    private void impostaVerifica() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        verifica = new Button(relatorePopup.getContext());
        verifica.setLayoutParams(params);
        verifica.setTextSize(20);
        verifica.setTextColor(Color.WHITE);
        verifica.setBackgroundColor(save.getShadowColor());
        verifica.setText(R.string.verify_email);

        LinearLayout tastiLayout = relatorePopup.findViewById(R.id.tasti);

        verifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailRelator = popup_email.getText().toString();
                boolean isAlreadyPresent = false;

                for (PersonaTesi relator : coRelators) {
                    if (relator.getEmail().equals(emailRelator)) {
                        isAlreadyPresent = true;
                        break;
                    }
                }

                if (isAlreadyPresent || user.getEmail().equals(emailRelator)) {
                    Toast.makeText(relatorePopup.getContext(), relatorePopup.getContext().getString(R.string.relator_already_inserted), Toast.LENGTH_SHORT).show();
                    return;
                }

                CollectionReference usersRef = FirebaseFirestore.getInstance().collection("users");
                usersRef.whereEqualTo("email", emailRelator)
                        .limit(1)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    LoggedInUser coRelatoreObj = documentSnapshot.toObject(LoggedInUser.class);
                                    popup_nome.setText(coRelatoreObj.getDisplayName());
                                    popup_nome.setVisibility(View.VISIBLE);
                                    popup_email.setVisibility(View.INVISIBLE);

                                    selectedCoRelators = coRelatoreObj;
                                    impostaModifica();
                                    modifica.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            popup_nome.setVisibility(View.INVISIBLE);
                                            popup_email.setVisibility(View.VISIBLE);
                                            save.setVisibility(View.INVISIBLE);
                                            impostaVerifica();
                                        }
                                    });
                                    save.setVisibility(View.VISIBLE);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(relatorePopup.getContext(), relatorePopup.getContext().getString(R.string.email_not_found), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        tastiLayout.removeView(modifica);
        tastiLayout.addView(verifica);
    }

    private void impostaModifica() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        modifica = new Button(relatorePopup.getContext());
        modifica.setLayoutParams(params);
        modifica.setTextSize(20);
        modifica.setBackgroundColor(save.getShadowColor());
        modifica.setTextColor(Color.WHITE);
        modifica.setText(relatorePopup.getContext().getString(R.string.change_email));

        LinearLayout tastiLayout = relatorePopup.findViewById(R.id.tasti);
        tastiLayout.removeView(verifica);
        tastiLayout.addView(modifica);
    }

    private List<String> checkPermessi() {
        List<String> permessi = new ArrayList<>();

        if (permesso1.isChecked()) {
            permessi.add(permesso1.getText().toString());
        }

        if (permesso2.isChecked()) {
            permessi.add(permesso2.getText().toString());
        }

        if (permesso3.isChecked()) {
            permessi.add(permesso3.getText().toString());
        }

        if (permesso4.isChecked()) {
            permessi.add(permesso4.getText().toString());
        }

        return permessi;
    }

    public void show() {
        this.dialog.show();
    }
}
