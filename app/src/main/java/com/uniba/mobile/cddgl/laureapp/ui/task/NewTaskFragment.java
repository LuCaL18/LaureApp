package com.uniba.mobile.cddgl.laureapp.ui.task;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.MainViewModel;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;
import com.uniba.mobile.cddgl.laureapp.data.TaskState;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.Task;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.ui.component.DatePickerFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fragment che si occupa della gestione della creazione di un nuovo task
 * da aggiungere ad una tesi già assegnata ad uno studente
 */

public class NewTaskFragment extends Fragment {

    private static final String CLASS_ID = "NewTaskFragment";

    public static final String TESI_NEW_TASK = "tesi_new_task";

    public static final String TESI_LIST_NEW_TASK = "tesi_list_new_task";

    /* Istanza per avviare il collegamento con firebase */
    private FirebaseFirestore db;
    /* EditText da visualizzare a schermo */
    private EditText nometaskEditText, descrizioneEditText, scadenzaEditText;
    /* Lista di tesi di backup da utilizzare durante le operazioni */
    public List<Tesi> tesiList;
    /* Lista di users di backup da utilizzare durante le operazioni */
    public PersonaTesi studenteTask;

    private BottomNavigationView navBar;

    private Spinner tesiSpinner;
    private TextView studenteTextView;
    private boolean creationFromTesi = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        this.tesiList = new ArrayList<>();

        Bundle bundle = getArguments();
        if (bundle != null) {
            Tesi tesi = (Tesi) bundle.getSerializable(TESI_NEW_TASK);

            if (tesi != null) {
                studenteTask = tesi.getStudent();
                tesiList.add(tesi);
                creationFromTesi = true;
            }

            if (bundle.getSerializable(TESI_LIST_NEW_TASK) != null) {
                tesiList = (List<Tesi>) bundle.getSerializable(TESI_LIST_NEW_TASK);
                creationFromTesi = false;
            }

        }
    }

    /**
     * Metodo 'onCreateView' in cui avviene la gestione di tutte le operazioni: dalla visualizzazione
     * a schermo delle varie componenti del layout, all'inserimento ed elaborazione dei dati, infine al
     * salvataggio dei dati all'interno del database
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* Creazione della view responsabile della gestione della visualizzazione del layout */
        View view = inflater.inflate(R.layout.fragment_new_task, container, false);

        try {
            /* Istanza del database */
            db = FirebaseFirestore.getInstance();

            /* Chiamata alle varie componenti del layout tramite findViewById */
            nometaskEditText = view.findViewById(R.id.nometask);

            descrizioneEditText = view.findViewById(R.id.descrizione);

            /* Spiiner relativi allo stato, tesi e studenteù */
            Spinner statoSpinner = view.findViewById(R.id.stato);
            tesiSpinner = view.findViewById(R.id.tesi_spinner);
            studenteTextView = view.findViewById(R.id.studente_spinner);
            scadenzaEditText = view.findViewById(R.id.scadenza);

            /* Adapter per creazione dello spinner relativo allo stato del task */
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.stato_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            statoSpinner.setAdapter(adapter);

            if (creationFromTesi) {
                studenteTextView.setText(studenteTask.getDisplayName());

                ArrayAdapter<String> tesiAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Collections.singletonList(tesiList.get(0).getNomeTesi()));
                tesiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tesiSpinner.setAdapter(tesiAdapter);

                tesiSpinner.setSelection(0);
                tesiSpinner.setEnabled(false);
            } else {
                loadThesisUser();
            }

            /* Visualizzazione del calendario per assegnare la data limite entro cui completare il task */
            scadenzaEditText.setOnClickListener(v -> {
                /* Chiamata al datePicker gestore della visualizzazione e dell'utilizzo del calendario */
                DialogFragment datePicker = new DatePickerFragment(R.layout.fragment_new_task);
                datePicker.show(getParentFragmentManager(), "date picker");
            });


            /* Button per completare l'operazione di creazione task */
            Button addtaskButton = view.findViewById(R.id.addtask_button);

            addtaskButton.setOnClickListener(view1 -> {
                String nometask = nometaskEditText.getText().toString();
                String descrizione = descrizioneEditText.getText().toString();
                String scadenza = scadenzaEditText.getText().toString();
                String stato = statoSpinner.getSelectedItem().toString();
                String tesi = tesiSpinner.getSelectedItem().toString();
                String tesiId = null;
                List<String> relators = new ArrayList<>();

                for (Tesi t : tesiList) {
                    /* Verifico se la tesi salvata nella lista di backup è equivalente all'id della tesi selezionata */
                    if (t.getNomeTesi().equals(tesi)) {
                        tesiId = t.getId();

                        relators.add(t.getRelatore().getId());
                        relators.addAll(t.getCoRelatori().stream().map(PersonaTesi::getId).collect(Collectors.toList()));
                    }
                }

                Task task = new Task(studenteTask.getId(), relators, descrizione, nometask, scadenza, TaskState.valueOf(stato), tesiId);

                /* Salvataggio del nuovo task nel database */
                db.collection("task")
                        .add(task)
                        .addOnSuccessListener(documentReference -> Toast.makeText(getContext(), getString(R.string.successfull), Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(getContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show());
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), getString(R.string.an_error_occured), Toast.LENGTH_SHORT).show();
            Log.e(CLASS_ID, "Error during onCreateView --> ", e);
        }

        /* Ritorno la view da mostrare a schermo */
        return view;
    }

    private void loadThesisUser() {
        /* Seleziona le tesi in cui il campo relatore o correlatore corrisponde all'ID del relatore loggato */
        List<String> tesiNameList = new ArrayList<>();

        for (Tesi tesi : tesiList) {
            tesiNameList.add(tesi.getNomeTesi());
        }

        /* Creazione dell'adapter per la lista delle tesi di cui l'utente loggato è relatore o coRelatore in cui associare il task */
        ArrayAdapter<String> tesiAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, tesiNameList);
        tesiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tesiSpinner.setAdapter(tesiAdapter);

        /* Quando viene selezionata una tesi dal primo spinner, recuperare l'ID della tesi selezionata */
        tesiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* Recupero della tesi scelta */
                String tesiNome = (String) parent.getItemAtPosition(position);

                for (Tesi tesi : tesiList) {
                    if (tesi.getNomeTesi().equals(tesiNome)) {
                        if (tesi.getStudent() != null) {
                            studenteTask = tesi.getStudent();
                        }

                        studenteTextView.setText(studenteTask.getDisplayName());
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        /* Rimozione della navBar dal layout */
        navBar = requireActivity().findViewById(R.id.nav_view);
        if (navBar != null) {
            navBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (navBar != null) {
            navBar.setVisibility(View.VISIBLE);
            navBar = null;
        }
    }
}