package it.uniba.dib.sms222327.laureapp.ui.task;

import static it.uniba.dib.sms222327.laureapp.ui.task.NewTaskFragment.TESI_LIST_NEW_TASK;
import static it.uniba.dib.sms222327.laureapp.ui.task.NewTaskFragment.TESI_NEW_TASK;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms222327.laureapp.MainActivity;
import it.uniba.dib.sms222327.laureapp.MainViewModel;
import it.uniba.dib.sms222327.laureapp.R;
import it.uniba.dib.sms222327.laureapp.data.RoleUser;
import it.uniba.dib.sms222327.laureapp.data.model.LoggedInUser;
import it.uniba.dib.sms222327.laureapp.data.model.Task;
import it.uniba.dib.sms222327.laureapp.data.model.Tesi;
import it.uniba.dib.sms222327.laureapp.ui.task.adapter.ListaTaskAdapter;

/**
 * Fragment che si occupa della gestione della visualizzazione
 * di una lista di task visibli dall'utente
 */

public class ListaTaskFragment extends Fragment {

    private static final String CLASS_ID = "ListaTaskFragment";

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
    private Tesi thesis;

    private LoggedInUser user;

    private List<Tesi> listTesi = new ArrayList<>();
    private Menu menuNewTask;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        user = mainViewModel.getUser().getValue();
        dataList = new ArrayList<>();
    }

    /**
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

        adapter = new ListaTaskAdapter(getActivity(), dataList, user);

        if(listTesi.isEmpty() && thesis == null) {

            Query query;
            if (getArguments() != null) {
                thesis = (Tesi) getArguments().getSerializable(LIST_TASK_TESI_KEY);
                permissionCreate = Boolean.parseBoolean(getArguments().getString(LIST_TASK_PERMISSION_CREATE));
                query = FirebaseFirestore.getInstance().collection("task").whereEqualTo("tesiId", thesis.getId());
            } else if (user.getRole().equals(RoleUser.PROFESSOR)) {
                loadThesisRelator();
                query = FirebaseFirestore.getInstance().collection("task").whereArrayContains("relators", user.getId());
            } else if (user.getRole().equals(RoleUser.STUDENT)) {
                query = FirebaseFirestore.getInstance().collection("task").whereEqualTo("studenteId", user.getId());
            } else {
                query = null;
            }

            if (query != null) {
                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (queryDocumentSnapshots != null) {
                            dataList.clear();
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                Task task = doc.toObject(Task.class);
                                dataList.add(task);
                            }

                            setEmptyMessage(view);
                            adapter.setmDataList(dataList);
                        }

                    }
                });
            }
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = NavHostFragment.findNavController(this);

        listView = view.findViewById(R.id.lista_task);
        listView.setAdapter(adapter);
        providerMenu = new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.app_bar_list_task, menu);
                menuNewTask = menu;

                if (permissionCreate) {
                    menu.findItem(ADD_TASK).setVisible(true);
                } else {
                    menu.findItem(ADD_TASK).setVisible(false);
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == ADD_TASK) {
                    Bundle bundleTask = new Bundle();
                    if (thesis != null) {
                        bundleTask.putSerializable(TESI_NEW_TASK, thesis);
                    } else {
                        bundleTask.putSerializable(TESI_LIST_NEW_TASK, (Serializable) listTesi);
                    }

                    navController.navigate(R.id.action_nav_lista_task_to_nav_new_task, bundleTask);
                    return true;

                }

                return false;
            }

            @Override
            public void onPrepareMenu(@NonNull Menu menu) {
                if(menu.findItem(ADD_TASK) == null) {
                    return;
                }
                menu.findItem(ADD_TASK).setVisible(permissionCreate);
            }
        };

        requireActivity().addMenuProvider(providerMenu);
    }

    private void setEmptyMessage(View view) {
        if (adapter.getmDataList().isEmpty()) {
            listView.setVisibility(View.GONE);
            view.findViewById(R.id.text_no_task_available).setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            view.findViewById(R.id.text_no_task_available).setVisibility(View.GONE);
        }
    }

    private void loadThesisRelator() {

        CollectionReference tesiReference = FirebaseFirestore.getInstance().collection("tesi");
        Query queryProf = tesiReference.whereEqualTo("relatore.id", user.getId()).whereNotEqualTo("student", null);
        com.google.android.gms.tasks.Task<QuerySnapshot> query1Task = queryProf.get();


        Tasks.whenAllSuccess(query1Task)
                .addOnSuccessListener(objects -> {
                    for (Object object : objects) {
                        QuerySnapshot querySnapshot = (QuerySnapshot) object;

                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            Tesi tesi = doc.toObject(Tesi.class);
                            if (tesi == null) {
                                continue;
                            }

                            listTesi.add(tesi);
                        }

                        permissionCreate = !listTesi.isEmpty();
                        providerMenu.onPrepareMenu(menuNewTask);
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), getString(R.string.an_error_occured), Toast.LENGTH_SHORT).show();
                    Log.e(CLASS_ID, "Unable fetch thesis for professor: " + e.getMessage());
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        navBar = requireActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navBar.setVisibility(View.VISIBLE);
        requireActivity().removeMenuProvider(providerMenu);

        NavigationView navigationView = requireActivity().findViewById(R.id.nav_view_menu);
        navigationView.getMenu().findItem(MainActivity.LISTA_TASK).setChecked(false);
    }
}
