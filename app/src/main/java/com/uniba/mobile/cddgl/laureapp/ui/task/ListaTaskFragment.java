package com.uniba.mobile.cddgl.laureapp.ui.task;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Task;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.data.model.Ticket;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.VisualizeTesiFragment;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogs.QRCodeDialogFragment;
import com.uniba.mobile.cddgl.laureapp.ui.ticket.TicketFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Fragment che si occupa della gestione della visualizzazione
 * di una lista di task visibli dall'utente
 *
 */

public class ListaTaskFragment extends Fragment {

    public static final String LIST_TASK_TESI_KEY = "idThesis";
    public static final String LIST_TASK_PERMISSION_CREATE = "permission_create";

    private static final int ADD_TASK = R.id.add_task;

    private ListView listView;
    /* Adapter per la gestione di ListaTaskAdapter */
    private ListaTaskAdapter adapter;
    /* Lista dei task da visualizzare a schermo */
    private List<Task> dataList;
    private CollectionReference mCollection;
    private BottomNavigationView navBar;
    private boolean permissionCreate;
    private MenuProvider providerMenu;
    private String idThesis;

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

        Query query;

        if(getArguments() != null) {
            idThesis = getArguments().getString(LIST_TASK_TESI_KEY);
            permissionCreate = Boolean.parseBoolean(getArguments().getString(LIST_TASK_PERMISSION_CREATE));
            query = FirebaseFirestore.getInstance().collection("task").whereEqualTo("idTesi", idThesis);
        } else {
            query = FirebaseFirestore.getInstance().collection("task");
        }

        listView = view.findViewById(R.id.lista_task);

        dataList = new ArrayList<>();
        adapter = new ListaTaskAdapter(getActivity(), dataList);
        Log.d("ListaTesiFragment", "onCreateView() method called");

        listView.setAdapter(adapter);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
                 adapter.setmDataList(dataList);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = NavHostFragment.findNavController(this);

        if (permissionCreate) {
            providerMenu = new MenuProvider() {
                @Override
                public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                    menu.clear();
                    menuInflater.inflate(R.menu.app_bar_list_task, menu);
                }

                @Override
                public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                    if (menuItem.getItemId() == ADD_TASK) {
                        Bundle bundleTask = new Bundle();
                        bundleTask.putString(LIST_TASK_TESI_KEY, idThesis);

                        navController.navigate(R.id.action_nav_lista_task_to_nav_new_task);
                        return true;
                    }

                    return false;
                }
            };

            requireActivity().addMenuProvider(providerMenu);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navBar.setVisibility(View.VISIBLE);
        requireActivity().removeMenuProvider(providerMenu);
    }
}
