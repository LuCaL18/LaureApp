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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.TicketState;
import com.uniba.mobile.cddgl.laureapp.data.model.Task;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.data.model.Ticket;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.VisualizeTesiFragment;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogs.QRCodeDialogFragment;
import com.uniba.mobile.cddgl.laureapp.ui.ticket.TicketFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListaTaskFragment extends Fragment {

    public static final String LIST_TASK_TESI_KEY = "idThesis";
    public static final String LIST_TASK_PERMISSION_CREATE = "permission_create";

    private static final int ADD_TASK = R.id.add_task;

    private ListView listView;
    private ListaTaskAdapter adapter;
    private List<Task> dataList;
    private BottomNavigationView navBar;
    private boolean permissionCreate;
    private MenuProvider providerMenu;
    private String idThesis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                    dataList.add(task);
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
