package com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogs;

import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.VisualizeTesiFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Dialog che si occupa della modifica o impostazione delle parole chiave associate a una tesi
 */
public class SearchKeyDialog {

    private final AlertDialog dialog;
    private ArrayAdapter<String> arrayAdapter2;
    private final CollectionReference filtriRef = FirebaseFirestore.getInstance().collection("filtri");

    public SearchKeyDialog(AlertDialog dialog, View searchKeyPopup, VisualizeTesiFragment requiredFragment, String ambito, List<String> keyWords) {
        this.dialog = dialog;

        Spinner ambitoSpinner = searchKeyPopup.findViewById(R.id.spinner);
        List<String> ambiti = Arrays.asList(requiredFragment.getResources().getStringArray(R.array.ambiti));

        EditText eNuovakey = searchKeyPopup.findViewById(R.id.nuova_chiave);
        Button addKey = searchKeyPopup.findViewById(R.id.aggiungi_keyword);
        addKey.setEnabled(false);

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()) {
                    addKey.setEnabled(false);

                    return;
                }

                addKey.setEnabled(true);
            }
        };

        eNuovakey.addTextChangedListener(afterTextChangedListener);

        ListView keywordListView = searchKeyPopup.findViewById(R.id.keywordsList);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(searchKeyPopup.getContext(),
                R.array.ambiti,
                android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        ambitoSpinner.setAdapter(adapter);
        ambitoSpinner.setSelection(ambiti.indexOf(ambito));

        List<String> keywordList = new ArrayList<>();
        addKey.setOnClickListener(viewAddKey -> {
            String nuovakey = String.valueOf(eNuovakey.getText());

            if (!keywordList.contains(nuovakey)) {

                keywordList.add(nuovakey);
                arrayAdapter2.notifyDataSetChanged();

                keywordListView.setItemChecked(keywordList.size() - 1, true);
                keyWords.add(nuovakey);
            } else {
                Toast.makeText(searchKeyPopup.getContext(), R.string.existing_keyword, Toast.LENGTH_SHORT).show();
            }

            Map<String, Object> T_chiave = new HashMap<>();
            T_chiave.put("lista", keywordList);

            filtriRef.document("parole_chiave")
                    .update(T_chiave)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(searchKeyPopup.getContext(), R.string.keyword_entered, Toast.LENGTH_SHORT).show();
                        eNuovakey.setText("");
                    });
        });


        filtriRef.document("parole_chiave").get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot != null && documentSnapshot.get("lista") != null) {
                keywordList.addAll((Collection<String>) documentSnapshot.get("lista"));
                arrayAdapter2 = new ArrayAdapter<>(searchKeyPopup.getContext(),
                        android.R.layout.simple_list_item_multiple_choice, keywordList);

                keywordListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                keywordListView.setAdapter(arrayAdapter2);

                keywordListView.setOnItemClickListener((adapterView, view, position, id) -> {

                    if(keyWords.contains(keywordList.get(position))) {
                        keyWords.remove(keywordList.get(position));
                        return;
                    }

                    keyWords.add(keywordList.get(position));
                });


                for (String word : keyWords) {
                    keywordListView.setItemChecked(keywordList.indexOf(word), true);
                }
            }
        });

        Button save = searchKeyPopup.findViewById(R.id.saveButton);
        Button cancel = searchKeyPopup.findViewById(R.id.cancelButton);

        save.setOnClickListener(viewSave -> {
            requiredFragment.updateSearchKey(ambitoSpinner.getSelectedItem().toString(), keyWords);
            dialog.dismiss();
        });

        cancel.setOnClickListener(viewCancel -> dialog.dismiss());
    }

    public void show() {
        dialog.show();
    }
}
