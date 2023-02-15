package com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogueMessages;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FilterDialogFragment extends DialogFragment {

    // Definizione dell'interfaccia per la comunicazione con l'activity chiamante
    public interface FilterDialogListener {
        void onFilterSelected(String filterOption);
    }

    private FilterDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            // Inizializza il listener dell'activity chiamante
            mListener = (FilterDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " deve implementare FilterDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Crea il builder della finestra di dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Imposta il titolo della finestra di dialogo
        builder.setTitle("Seleziona un filtro");

        // Crea un array di opzioni di filtro
        final String[] filterOptions = {"Opzione 1", "Opzione 2", "Opzione 3"};

        // Imposta la lista delle opzioni di filtro nella finestra di dialogo
        builder.setItems(filterOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Invia l'opzione di filtro selezionata all'activity chiamante
                mListener.onFilterSelected(filterOptions[which]);
            }
        });

        // Crea la finestra di dialogo
        AlertDialog dialog = builder.create();

        // Ritorna la finestra di dialogo
        return dialog;
    }



    public void show(FragmentManager manager, String tag) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(this, tag);
        transaction.commitAllowingStateLoss();
    }


}
