package com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogs;

import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.MainViewModel;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.CoRelatorPermissions;
import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.CreaTesiFragment;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.VisualizeTesiFragment;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Dialog che si occupa dell'aggiunta dei corelatori associati a una tesi
 */
public class CoRelatoreDialoog {

    private final AlertDialog dialog;
    private final List<PersonaTesi> coRelators;
    private final TextView popup_nome;
    private final EditText popup_email;
    private final View relatorePopup;
    private final Button save;
    private final Button verifyEmailButton;
    private final Button actionEditEmail;
    private final CheckBox editSearchKeysPermission, documentsPermission, constraintsPermission, notesPermission;
    private LoggedInUser selectedCoRelators;
    private final LoggedInUser user;

    public CoRelatoreDialoog(AlertDialog dialog, View view, List<PersonaTesi> coRelators, VisualizeTesiFragment requireFragment) {
        this.dialog = dialog;
        this.coRelators = coRelators;
        relatorePopup = view;

        MainViewModel model = new ViewModelProvider(requireFragment.requireActivity()).get(MainViewModel.class);
        user = model.getUser().getValue();

        popup_nome = view.findViewById(R.id.nome);
        popup_email = view.findViewById(R.id.email);

        editSearchKeysPermission = view.findViewById(R.id.edit_search_keys_permission);
        documentsPermission = view.findViewById(R.id.edit_documents_permission);
        constraintsPermission = view.findViewById(R.id.edit_constraints_permission);
        notesPermission = view.findViewById(R.id.edit_notes_permission);
        verifyEmailButton = view.findViewById(R.id.verify_button_email);
        actionEditEmail = view.findViewById(R.id.edit_button_email);

        save = view.findViewById(R.id.saveButton);
        Button cancel = view.findViewById(R.id.cancelButton);

        impostaActionButton();

        save.setOnClickListener(viewSave -> {

            if (selectedCoRelators != null) {
                PersonaTesi coRelatore = new PersonaTesi(selectedCoRelators.getId(), selectedCoRelators.getDisplayName(), selectedCoRelators.getEmail(), checkPermessi());
                requireFragment.addCoRelator(coRelatore);
            }

            dialog.dismiss();
        });

        cancel.setOnClickListener(viewCancel -> dialog.dismiss());
    }

    public CoRelatoreDialoog(AlertDialog dialog, View view, List<PersonaTesi> coRelators, CreaTesiFragment requireFragment) {
        this.dialog = dialog;
        this.coRelators = coRelators;
        relatorePopup = view;

        MainViewModel model = new ViewModelProvider(requireFragment.requireActivity()).get(MainViewModel.class);
        user = model.getUser().getValue();

        popup_nome = view.findViewById(R.id.nome);
        popup_email = view.findViewById(R.id.email);

        editSearchKeysPermission = view.findViewById(R.id.edit_search_keys_permission);
        documentsPermission = view.findViewById(R.id.edit_documents_permission);
        constraintsPermission = view.findViewById(R.id.edit_constraints_permission);
        notesPermission = view.findViewById(R.id.edit_notes_permission);
        verifyEmailButton = view.findViewById(R.id.verify_button_email);
        actionEditEmail = view.findViewById(R.id.edit_button_email);

        save = view.findViewById(R.id.saveButton);
        Button cancel = view.findViewById(R.id.cancelButton);

        impostaActionButton();

        save.setOnClickListener(viewSave -> {

            if (selectedCoRelators != null) {
                PersonaTesi coRelatore = new PersonaTesi(selectedCoRelators.getId(), selectedCoRelators.getDisplayName(), selectedCoRelators.getEmail(), checkPermessi());
                requireFragment.addCoRelator(coRelatore);
            }

            dialog.dismiss();
        });

        cancel.setOnClickListener(viewCancel -> dialog.dismiss());
    }

    private void impostaActionButton() {

        impostaVerifica();

        verifyEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailRelator = popup_email.getText().toString();
                boolean isAlreadyPresent = false;

                if(coRelators!=null){
                    for (PersonaTesi relator : coRelators) {
                        if (relator.getEmail().equals(emailRelator)) {
                            isAlreadyPresent = true;
                            break;
                        }
                    }
                }

                if (isAlreadyPresent || user.getEmail().equals(emailRelator)) {
                    Toast.makeText(relatorePopup.getContext(), relatorePopup.getContext().getString(R.string.relator_already_inserted), Toast.LENGTH_SHORT).show();
                    return;
                }

                CollectionReference usersRef = FirebaseFirestore.getInstance().collection("users");
                usersRef.whereEqualTo("email", emailRelator).whereEqualTo("role", RoleUser.PROFESSOR)
                        .limit(1)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(queryDocumentSnapshots.isEmpty()) {
                                    Toast.makeText(relatorePopup.getContext(), relatorePopup.getContext().getString(R.string.professor_email_not_found), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    LoggedInUser coRelatoreObj = documentSnapshot.toObject(LoggedInUser.class);
                                    popup_nome.setText(coRelatoreObj.getDisplayName());
                                    popup_nome.setVisibility(View.VISIBLE);
                                    popup_email.setVisibility(View.GONE);

                                    CircleImageView imageView = relatorePopup.findViewById(R.id.image);
                                    selectedCoRelators = coRelatoreObj;
                                    Glide.with(relatorePopup.getContext())
                                            .load(selectedCoRelators.getPhotoUrl())
                                            .apply(new RequestOptions()
                                                    .placeholder(R.mipmap.ic_user_round)
                                                    .error(R.mipmap.ic_user_round)
                                                    .skipMemoryCache(true))
                                            .into(imageView);

                                    impostaModifica();
                                    actionEditEmail.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            popup_nome.setVisibility(View.GONE);
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
    }

    private void impostaModifica() {
        verifyEmailButton.setVisibility(View.GONE);
        actionEditEmail.setVisibility(View.VISIBLE);
    }

    private void impostaVerifica() {
        verifyEmailButton.setVisibility(View.VISIBLE);
        actionEditEmail.setVisibility(View.GONE);
    }


    private List<String> checkPermessi() {
        List<String> permessi = new ArrayList<>();

        if (editSearchKeysPermission.isChecked()) {
            permessi.add(CoRelatorPermissions.EDIT_SEARCH_KEYS.name());
        }

        if (documentsPermission.isChecked()) {
            permessi.add(CoRelatorPermissions.EDIT_DOCUMENTS.name());
        }

        if (constraintsPermission.isChecked()) {
            permessi.add(CoRelatorPermissions.EDIT_CONSTRAINTS.name());
        }

        if (notesPermission.isChecked()) {
            permessi.add(CoRelatorPermissions.EDIT_NOTES.name());
        }

        return permessi;
    }

    public void show() {
        this.dialog.show();
    }
}
