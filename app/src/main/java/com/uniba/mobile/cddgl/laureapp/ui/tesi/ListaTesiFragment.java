package com.uniba.mobile.cddgl.laureapp.ui.tesi;

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
import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Fragment che si occupa della gestione della visualizzazione
 * di una lista di task visibli dall'utente
 *
 */

public class ListaTesiFragment extends Fragment {

    /* ListView per la visualizzazione della lista di tesi*/
    private ListView listView;
    /* Adapter per la gestione di ListAdapterTesi */
    private ListAdapterTesi adapter;
    /* Lista delle tesi da visualizzare a schermo */
    private List<Tesi> dataList;
    /* CollectionReference per il recupero di tutte le tesi istanziate su firebase */
    private CollectionReference mCollection;
    /* CollectionReference per il recupero di tutti gli user istanziati su firebase */
    private CollectionReference mCollection2;
    /* navBar da rimuovere nella schermata*/
    private BottomNavigationView navBar;
    /* Oggetto di tipo LoggedInUser per memorizzare l'users attualmente loggato */
    private LoggedInUser userLogged = null;

    /**
     *
     * Metodo "onCreateView" per la visualizzazione della lista di tutte le tesi
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* Creazione della view responsabile della gestione della visualizzazione del layout */
        View view = inflater.inflate(R.layout.fragment_lista_tesi, container, false);
        listView = view.findViewById(R.id.listatesi);
        /* Rimozione della navBar dallo schermo */
        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);
        /* Istanziamo dataList, mCollection, mCollection2 e adatper */
        dataList = new ArrayList<>();
        mCollection = FirebaseFirestore.getInstance().collection("tesi");
        adapter = new ListAdapterTesi(getActivity(), mCollection);
        Log.d("ListaTesiFragment", "onCreateView() method called");
        listView.setAdapter(adapter);
        mCollection2 = FirebaseFirestore.getInstance().collection("users");
        /* Recupera l'ID del relatore attualmente loggato */
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        /* Utilizzo di mCollection2 per il recupero di tutti gli users registrati nel database */
        mCollection2.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("FirebaseListAdapter", "Listen failed.", e);
                    return;
                }
                /* Recupero di tutti gli attributi dell'user attualmente loggato */
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    LoggedInUser user = doc.toObject(LoggedInUser.class);
                    if (user.getId().equals(userId)) {
                        userLogged = user;
                        break;
                    }
                }
            }
        });
        /* Utilizzo di mCollection per il recupero di tutte le tesi istanziate sul database */
        mCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("FirebaseListAdapter", "Listen failed.", e);
                    return;
                }
                dataList.clear();
                /* Se l'utente loggato è un professore, aggiungere tutte le tesi
                 *  in cui l'user è relatore o corelatore */
                if (userLogged.getRole().equals(RoleUser.PROFESSOR)) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Tesi tesi = doc.toObject(Tesi.class);
                        /* Verifico se l'utente loggato è un coRelatore della tesi */
                        List<PersonaTesi> coRelatore = tesi.getCoRelatori();
                        if (coRelatore.isEmpty()) {
                            for (PersonaTesi p : coRelatore) {
                                /* Se l'id dell'user recuperato dal database è equivalente all'id dell'utente
                                *  loggato, allora inserisco la tesi all'interno del datalist */
                                if (p.getId().equals(userLogged.getId())) {
                                    dataList.add(tesi);
                                }
                            }
                        }
                        /* Verifico se l'utente loggato è un relatore della tesi */
                        if (tesi.getRelatore().getId().equals(userLogged.getId())) {
                            dataList.add(tesi);
                        }
                    }
                }
                /* Se l'utente loggato è un studente, aggiungere tutte le tesi
                 *  non ancora assegnate ad uno studente */
                if (userLogged.getRole().equals(RoleUser.STUDENT)) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Tesi tesi = doc.toObject(Tesi.class);
                        /* Se la tesi non è stata assegnata a nessuno studente, aggiungo tale
                        *  tesi all'interno del datalist */
                        if (tesi.getStudent() == null) {
                            dataList.add(tesi);
                        }
                    }
                }
                /* Notifico l'adapter delle modifiche effettuate*/
                adapter.notifyDataSetChanged();
            }
        });
        /* Restituisco la view da mostrare a schermo */
        return view;
    }
}