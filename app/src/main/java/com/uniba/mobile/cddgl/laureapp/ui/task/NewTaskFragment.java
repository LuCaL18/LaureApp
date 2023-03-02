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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.ui.component.DatePickerFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Fragment che si occupa della gestione della creazione di un nuovo task
 * da aggiungere ad una tesi già assegnata ad uno studente
 *
 */

public class NewTaskFragment extends Fragment {

    /* Istanza per avviare il collegamento con firebase */
    private FirebaseFirestore db;
    /* EditText da visualizzare a schermo */
    private EditText nometaskEditText,descrizioneEditText,scadenzaEditText;
    /* Button per completare l'operazione di creazione task */
    private Button addtaskButton;
    /* CollectionReference per il recupero di tutte le tesi istanziate su firebase */
    private CollectionReference tesiReference;
    /* Lista di tesi di backup da utilizzare durante le operazioni */
    public List<Tesi> tesiBackup;
    /* Lista di users di backup da utilizzare durante le operazioni */
    public List<PersonaTesi> personaBackup;

    public NewTaskFragment() {
        //
    }

    public static NewTaskFragment newInstance() {
        return new NewTaskFragment();
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     *
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
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* Creazione della view responsabile della gestione della visualizzazione del layout */
        View view = inflater.inflate(R.layout.fragment_new_task,container,false);
        /* Istanza del database */
        db = FirebaseFirestore.getInstance();
        /* Rimozione della navBar dal layout */
        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);
        /* Chiamata alle varie componenti del layout tramite findViewById */
        nometaskEditText = view.findViewById(R.id.nometask);
        descrizioneEditText = view.findViewById(R.id.descrizione);
        /* Spiiner relativi allo stato, tesi e studenteù */
        Spinner statoSpinner = view.findViewById(R.id.stato);
        Spinner tesiSpinner = view.findViewById(R.id.tesi_spinner);
        Spinner studenteSpinner = view.findViewById(R.id.studente_spinner);
        scadenzaEditText = view.findViewById(R.id.scadenza);
        addtaskButton = view.findViewById(R.id.addtask_button);
        tesiReference = FirebaseFirestore.getInstance().collection("tesi");
        /* Adapter per creazione dello spinner relativo allo stato del task */
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.stato_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statoSpinner.setAdapter(adapter);
        /* Recupera l'ID del relatore attualmente loggato */
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String relatoreId = currentUser.getUid();
        /* Seleziona le tesi in cui il campo relatore o correlatore corrisponde all'ID del relatore loggato */
        List<String> tesiList = new ArrayList<>();
        List<Tesi> tesiList2 = new ArrayList<>();
        /* Definizione istanza delle liste di backup */
        tesiBackup = new ArrayList<>();
        personaBackup = new ArrayList<>();
        /* Utilizzo della tesiReeference per il recupero delle istanze degli users presenti nel database */
        tesiReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    Tesi tesi = doc.toObject(Tesi.class);
                    /* List che contiene tutte le informazioni relativi ai relatori associati a quella specifica tesi recuperata dal firebase */
                    List<PersonaTesi> coRelatore = tesi.getCoRelatori();
                    if (coRelatore.isEmpty()) {
                        for (PersonaTesi p : coRelatore) {
                            /* Se il coRelatore coincide con l'utente loggato, procedere al salvataggio della tesi nelle liste */
                            if (p.getId().equals(relatoreId)) {
                                tesiList.add(tesi.getNomeTesi());
                                tesiList2.add(tesi);
                                tesiBackup.add(tesi);
                            }
                        }
                    }
                    /* Se il relatore coincide con l'utente loggato, procedere al salvataggio della tesi nelle liste */
                    if (tesi.getRelatore().getId().equals(relatoreId)) {
                        tesiList.add(tesi.getNomeTesi());
                        tesiList2.add(tesi);
                        tesiBackup.add(tesi);
                    }
                }
                Log.d("ListaTesiFragment", "onCreateView() method called");
                /* Creazione dell'adapter per la lista delle tesi di cui l'utente
                   loggato è relatore o coRelatore in cui associare il task */
                ArrayAdapter<String> tesiAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, tesiList);
                tesiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tesiSpinner.setAdapter(tesiAdapter);
                /* Quando viene selezionata una tesi dal primo spinner, recuperare l'ID della tesi selezionata */
                tesiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        /* Recupero della tesi scelta */
                        String tesiNome = (String) parent.getItemAtPosition(position);
                        /* Lista degli studenti da popolare */
                        List<String> studentiList = new ArrayList<>();
                        List<PersonaTesi> studentiList2 = new ArrayList<>();
                        personaBackup = studentiList2;
                        for (Tesi tesi : tesiList2) {
                            if (tesi.getNomeTesi().equals(tesiNome)) {
                                // String tesiId = tesi.getId();
                                /* Seleziona gli studenti associati alla tesi selezionata */
                                studentiList2.add(tesi.getStudent());
                                for (PersonaTesi studente : studentiList2) {
                                    studentiList.add(studente.getDisplayName());
                                }
                                /* Popola il secondo spinner con i dati degli studenti selezionati */
                                ArrayAdapter<String> studentiAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, studentiList);
                                studentiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                studenteSpinner.setAdapter(studentiAdapter);
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
        });
        /* Visualizzazione del calendario per assegnare la data limite entro cui completare il task */
        scadenzaEditText.setOnClickListener(v -> {
            /* Chiamata al datePicker gestore della visualizzazione e dell'utilizzo del calendario */
            DialogFragment datePicker = new DatePickerFragment(R.layout.fragment_new_task);
            datePicker.show(getParentFragmentManager(), "date picker");
            addtaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /* Recupero dei dati inseriti e inserimento all'interno del firebase nella raccolta task */
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String relatoreId = currentUser.getUid();
                    String nometask = nometaskEditText.getText().toString();
                    String descrizione = descrizioneEditText.getText().toString();
                    String scadenza = scadenzaEditText.getText().toString();
                    String stato = statoSpinner.getSelectedItem().toString();
                    String tesi = tesiSpinner.getSelectedItem().toString();
                    String studente = studenteSpinner.getSelectedItem().toString();
                    String tesiId = null;
                    String studenteId = null;
                    for (Tesi t : tesiBackup) {
                        /* Verifico se la tesi salvata nella lista di backup è equivalente all'id della tesi selezionata */
                        if (t.getNomeTesi().equals(tesi)) {
                            tesiId = t.getId();
                        }
                    }
                    for (PersonaTesi p : personaBackup) {
                        /* Verifico se la persona salvata nella lista di backup è equivalente all'id dello studente */
                        if (p.getDisplayName().equals(studente)) {
                            studenteId = p.getId();
                        }
                    }
                    Map<String, Object> listaTask = new HashMap<>();
                    listaTask.put("nomeTask", nometask);
                    listaTask.put("stato", stato);
                    listaTask.put("descrizione", descrizione);
                    listaTask.put("scadenza", scadenza);
                    listaTask.put("relatore", relatoreId);
                    listaTask.put("tesiId",tesiId);
                    listaTask.put("studenteId",studenteId);
                    /* Salvataggio del nuovo task nel database */
                    db.collection("task")
                            .add(listaTask)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getContext(), "successfull", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        });
        /* Ritorno la view da mostrare a schermo */
        return view;
    }

}