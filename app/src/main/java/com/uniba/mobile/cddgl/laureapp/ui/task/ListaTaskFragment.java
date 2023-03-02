package com.uniba.mobile.cddgl.laureapp.ui.task;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import com.uniba.mobile.cddgl.laureapp.data.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Fragment che si occupa della gestione della visualizzazione
 * di una lista di task visibli dall'utente
 *
 */

public class ListaTaskFragment extends Fragment {

    /* ListView per la visualizzazione della lista di task */
    private ListView listView;
    /* Adapter per la gestione di ListaTaskAdapter */
    private ListaTaskAdapter adapter;
    /* Lista dei task da visualizzare a schermo */
    private List<Task> dataList;
    /* CollectionReference per il recupero di tutti i task istanziati su firebase */
    private CollectionReference mCollection;
    /* CollectionReference per il recupero di tutti gli user istanziati su firebase */
    private CollectionReference mCollection2;
    /* navBar da rimuovere nella schermata */
    private BottomNavigationView navBar;
    /* Oggetto di tipo LoggedInUser per memorizzare l'users attualmente loggato */
    private LoggedInUser userLogged = null;

    /**
     *
     * Metodo "onCreateView" per il recupero di tutti i task da visualizzare a schermo
     * filtrati per lo specifico user in base al suo ruolo (PROFESSOR o STUDENT)
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* Creazione della view responsabile della gestione della visualizzazione del layout */
        View view = inflater.inflate(R.layout.fragment_lista_task, container, false);
        listView = view.findViewById(R.id.lista_task);
        /* Rimozione della navBar dallo schermo */
        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);
        /* Inizializzo il datalist, mCollection, mCollection2 e adapter */
        dataList = new ArrayList<>();
        mCollection = FirebaseFirestore.getInstance().collection("task");
        adapter = new ListaTaskAdapter(getActivity(), mCollection);
        Log.d("ListaTesiFragment", "onCreateView() method called");
        /* Assegno l'adapter alla listView */
        listView.setAdapter(adapter);
        mCollection2 = FirebaseFirestore.getInstance().collection("users");
        /* Recupera l'ID del relatore attualmente loggato */
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        /* Utilizzo mCollection2 per il recupero di tutti gli users registrati nel database */
        mCollection2.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("FirebaseListAdapter", "Listen failed.", e);
                    return;
                }
                /* Recupero dei dati relativi all'user loggato tramite l'id recuperato in precedenza */
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    LoggedInUser user = doc.toObject(LoggedInUser.class);
                    if (user.getId().equals(userId)) {
                        userLogged = user;
                        break;
                    }
                }
            }
        });
        /* Utilizzo di mCollection per il recupero di tutti i task istanziati sul database */
        mCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("FirebaseListAdapter", "Listen failed.", e);
                    return;
                }
                dataList.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    Task task = doc.toObject(Task.class);
                    /* Recupero degli id dello studente e del relatore */
                    String idRelatore = doc.getString("relatore");
                    String idStudent = doc.getString("studenteId");
                    /* Se l'utente loggato è un professore e l'id del relatore coincide con l'utente loggato, aggiungere il task alla lista */
                    if (userLogged.getRole().equals(RoleUser.PROFESSOR) && idRelatore.equals(userLogged.getId())) {
                        dataList.add(task);
                    } /* Se l'utente loggato è uno studente e l'id del relatore coincide con l'utente loggato, aggiungere il task alla lista */
                    else if (userLogged.getRole().equals(RoleUser.STUDENT) && idStudent.equals(userLogged.getId())) {
                        dataList.add(task);
                    }
                }
                /* Notifico l'adapter delle modifiche effettuate */
                adapter.notifyDataSetChanged();
            }
        });
        /* Restituisco la view da mostrare a schermo */
        return view;
    }
}
