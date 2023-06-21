package com.uniba.mobile.cddgl.laureapp.ui.profile.dialogs;

import static com.uniba.mobile.cddgl.laureapp.ui.login.LoginViewModel.MIN_LENGTH_PASSWORD;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uniba.mobile.cddgl.laureapp.R;

/**
 * Dialog per la richiesta di modifica password
 */
public class PasswordChangeDialog extends DialogFragment {

    private Context context;
    private EditText editTextOldPassword;
    private EditText editTextNewPassword;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_password, null);

        editTextOldPassword = view.findViewById(R.id.editTextOldPassword);
        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);

        // Imposta il titolo, i pulsanti e altre personalizzazioni del dialog
        builder.setView(view)
                .setPositiveButton(getString(R.string.save), null)
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog != null) {
            alertDialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                // Ottieni l'utente corrente
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                // Verifica se l'utente è loggato e non è nullo
                if (currentUser != null) {
                    // Ottieni la vecchia password inserita dall'utente
                    String oldPassword = editTextOldPassword.getText().toString();

                    // Ottieni la nuova password inserita dall'utente
                    String newPassword = editTextNewPassword.getText().toString();

                    if (newPassword.isEmpty() || newPassword.trim().length() <= MIN_LENGTH_PASSWORD) {
                        Toast.makeText(context, getString(R.string.required_new_passord_length), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Crea le credenziali per l'autenticazione
                    AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), oldPassword);

                    // Reautentica l'utente con le credenziali correnti
                    currentUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Reautenticazione riuscita, esegui la modifica della password
                                        currentUser.updatePassword(newPassword)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // Modifica della password riuscita
                                                            Toast.makeText(context, getString(R.string.password_changed_successfully), Toast.LENGTH_SHORT).show();
                                                            dismiss();
                                                        } else {
                                                            // Modifica della password fallita
                                                            Toast.makeText(context, getString(R.string.password_changed_failed), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        // Reautenticazione fallita
                                        Toast.makeText(context, getString(R.string.wrong_old_password_entered), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
        }
    }

}

