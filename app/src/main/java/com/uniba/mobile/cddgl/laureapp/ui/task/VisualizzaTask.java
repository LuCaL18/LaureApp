package com.uniba.mobile.cddgl.laureapp.ui.task;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;

/**
 *
 * Fragment che si occupa della gestione della visualizzazione di un task
 * esistente selezionato dalla lista dei task visibili dall'utente
 *
 */

public class VisualizzaTask extends Fragment {

    /* CollectionReference per il recupero di tutti gli users istanziati su firebase */
    private CollectionReference mCollection = FirebaseFirestore.getInstance().collection("users");
    /* Oggetto di tipo LoggedInUser per memorizzare l'users attualmente loggato */
    private LoggedInUser userLogged2;
    /* Stringa in cui memorizzare l'id del task da visualizzare */
    private String taskId;

    private BottomNavigationView navBar;

    public static VisualizzaTask newInstance() {
        return new VisualizzaTask();
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     *
     * Metodo 'onCreateView' in cui avviene la gestione di tutte le operazioni: dalla visualizzazione
     * a schermo delle varie componenti del layout e all'eventuale modifica dell'attributo STATO di
     * un task se l'utente loggato è un PROFESSOR
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Creazione della view responsabile della gestione della visualizzazione del layout */
        /* View per la gestione della visualizzazione della schermata del singolo task */
        View root = inflater.inflate(R.layout.visualizza_task, container, false);
        /* Recupero il task passato da ListaTaskAdapter */
        Bundle bundle = getArguments();
        if (bundle != null) {
            /* Visualizzo il titolo del task */
            TextView title = root.findViewById(R.id.nometask2);
            title.setText(bundle.getString("nometask"));
            /* Card descrizione */
            MaterialCardView cardDescrizione = root.findViewById(R.id.cv_descrizione);
            LinearLayout cvDescrizioneLayout = root.findViewById(R.id.descrizione_task);
            TextView descrizioneTask = root.findViewById(R.id.tv_descrizione3);
            cardDescrizione.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cvDescrizioneLayout.getVisibility() == View.GONE) {
                        cvDescrizioneLayout.setVisibility(View.VISIBLE);
                    } else {
                        cvDescrizioneLayout.setVisibility(View.GONE);
                    }
                }
            });
            descrizioneTask.setText(bundle.getString("descrizione"));
            /* Recupero user attualmente loggato */
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            ImageButton editStatoTask = root.findViewById(R.id.edit_task_state);
            if (currentUser != null) {
                String userId = currentUser.getUid();
                mCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("FirebaseListAdapter", "Listen failed.", e);
                            return;
                        }
                        /* Recupero dei dati relativi all'utente loggato e da riportare su "userLogged2" istanziato in precedenza */
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            LoggedInUser user = doc.toObject(LoggedInUser.class);
                            if (user.getId().equals(userId)) {
                                userLogged2 = user;
                                break;
                            }
                        }
                        /* Verifico se l'utente loggato è uno STUDENTE o PROFESSORE per visualizzare o meno il bottone editStatoTask */
                        if (userLogged2 != null && userLogged2.getRole() == RoleUser.STUDENT) {
                            editStatoTask.setVisibility(View.GONE);
                        } else if (userLogged2 != null && userLogged2.getRole() == RoleUser.PROFESSOR) {
                            editStatoTask.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
            /* Card stato */
            MaterialCardView cardStato = root.findViewById(R.id.cv_stato);
            LinearLayout cvStatoLayout = root.findViewById(R.id.stato_task);
            TextView statoTask = root.findViewById(R.id.tv_stato3);
            cardStato.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cvStatoLayout.getVisibility() == View.GONE) {
                        cvStatoLayout.setVisibility(View.VISIBLE);
                    } else {
                        cvStatoLayout.setVisibility(View.GONE);
                    }
                }
            });
            statoTask.setText(bundle.getString("stato"));
            editStatoTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /* Crea il dialog personalizzato */
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_modifica_stato, null);
                    builder.setView(dialogView);
                    AlertDialog dialog = builder.create();
                    /* Popola la ListView con le opzioni dello stato */
                    ListView listViewStato = dialogView.findViewById(R.id.listview_stato);
                    String[] stati = {"NEW", "STARTED", "COMPLETED", "CLOSED"};
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, stati);
                    listViewStato.setAdapter(adapter);
                    /* Aggiorna l'istanza task su Firebase quando l'utente seleziona una delle opzioni */
                    listViewStato.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String nuovoStato = stati[position];
                            statoTask.setText(nuovoStato);
                            /* Aggiorna l'istanza task su Firebase con il nuovo stato */
                            Bundle bundle = getArguments();
                            if (bundle != null) {
                                String nomeTask = bundle.getString("nomeTask");
                                /* Recupero del task visualizzato presente su firebase */
                                FirebaseFirestore.getInstance().collection("task")
                                        .whereEqualTo("nomeTask", bundle.getString("nometask")).get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                if (!queryDocumentSnapshots.isEmpty()) {
                                                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                                    String taskId = documentSnapshot.getId();
                                                    /* Aggiornamento del task visualizzato con la modifica dell'attributo stato */
                                                    FirebaseFirestore.getInstance().collection("task").document(taskId)
                                                            .update("stato", nuovoStato)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(getContext(), "Stato aggiornato con successo", Toast.LENGTH_SHORT).show();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(getContext(), "Errore nell'aggiornamento dello stato", Toast.LENGTH_SHORT).show();
                                                                    Log.d("VisualizzaTask", e.toString());
                                                                }
                                                            });
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), "Errore nel recupero del task", Toast.LENGTH_SHORT).show();
                                                Log.d("VisualizzaTask", e.toString());
                                            }
                                        });
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
            /* Card scadenza */
            MaterialCardView cardScadenza = root.findViewById(R.id.cv_scadenza);
            LinearLayout cvScadenzaLayout = root.findViewById(R.id.scadenza_task);
            TextView scadenzaTask = root.findViewById(R.id.tv_scadenza3);
            cardScadenza.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cvScadenzaLayout.getVisibility() == View.GONE) {
                        cvScadenzaLayout.setVisibility(View.VISIBLE);
                    } else {
                        cvScadenzaLayout.setVisibility(View.GONE);
                    }
                }
            });
            scadenzaTask.setText(bundle.getString("scadenza"));
        }
        /* Ritorno della view da visualizzare a schermo */
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        /* Rimozione della navBar dal layout */
        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navBar.setVisibility(View.VISIBLE);
        navBar = null;
    }
}