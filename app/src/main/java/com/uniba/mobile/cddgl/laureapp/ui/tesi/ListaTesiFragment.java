package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import android.os.Bundle;
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
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.MainViewModel;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.adapters.ListAdapterTesi;

import java.util.ArrayList;
import java.util.List;

public class ListaTesiFragment extends Fragment implements SearchView.OnQueryTextListener {

    private final int SEARCH_ITEM_MENU = R.id.search_tesi;
    private final int FILTER_ITEM_MENU = R.id.filter_tesi;
    private final int TAB_ALL = 0;
    private final int TAB_PERSONAL = 1;

    private ListAdapterTesi adapterAll;
    private ListAdapterTesi adapterPersonal;
    private List<Tesi> currentTesiList;
    private List<Tesi> allTesiList;
    private List<Tesi> personalTesiList;
    private VisualizeThesisViewModel visualizeThesisViewModel;
    private Query queryAll;
    private MenuProvider menuProvider;
    private SearchView searchView;
    private int currentTab;
    private LoggedInUser user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allTesiList = new ArrayList<>();
        personalTesiList = new ArrayList<>();

        if (savedInstanceState != null) {
            currentTab = Integer.parseInt(savedInstanceState.getString("current_tab"));
        } else {
            currentTab = 0;
        }

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        user = mainViewModel.getUser().getValue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_tesi, container, false);
        ListView listView = view.findViewById(R.id.listatesi);

        visualizeThesisViewModel = new ViewModelProvider(requireParentFragment()).get(VisualizeThesisViewModel.class);

        adapterAll = new ListAdapterTesi(getContext(), new ArrayList<>(), visualizeThesisViewModel);
        adapterPersonal = new ListAdapterTesi(getContext(), new ArrayList<>(), visualizeThesisViewModel);

        CollectionReference tesiRef = FirebaseFirestore.getInstance().collection("tesi");

        queryAll = tesiRef.whereEqualTo("isAssigned", false).orderBy("created_at", Query.Direction.DESCENDING);
        queryAll.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                allTesiList.clear();
                for (DocumentSnapshot doc : querySnapshot) {
                    Tesi tesi = doc.toObject(Tesi.class);
                    allTesiList.add(tesi);
                }

                adapterAll.setTesiList(allTesiList);

                if (currentTab == TAB_ALL) {
                    currentTesiList = allTesiList;

                    if(currentTesiList.isEmpty()) {
                        view.findViewById(R.id.text_no_tesi_available).setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    } else {
                        view.findViewById(R.id.text_no_tesi_available).setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        if (user.getRole().equals(RoleUser.PROFESSOR)) {
            Query queryProf = tesiRef.whereEqualTo("relatore.id", user.getId()).orderBy("created_at", Query.Direction.DESCENDING);

            Query queryCoRelatori = tesiRef.whereArrayContains("coRelatori", new PersonaTesi(user.getId(), user.getDisplayName(), user.getEmail(), null))
                    .orderBy("created_at", Query.Direction.DESCENDING);

            com.google.android.gms.tasks.Task<QuerySnapshot> query1Task = queryProf.get();
            com.google.android.gms.tasks.Task<QuerySnapshot> query2Task = queryCoRelatori.get();

            Tasks.whenAllSuccess(query1Task, query2Task)
                    .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                        @Override
                        public void onSuccess(List<Object> objects) {
                            personalTesiList.clear();
                            for (Object object : objects) {
                                QuerySnapshot querySnapshot = (QuerySnapshot) object;

                                for (DocumentSnapshot doc : querySnapshot) {
                                    Tesi tesi = doc.toObject(Tesi.class);
                                    personalTesiList.add(tesi);
                                }

                                adapterPersonal.setTesiList(personalTesiList);

                                if (currentTab == TAB_PERSONAL) {
                                    currentTesiList = personalTesiList;

                                    if(currentTesiList.isEmpty()) {
                                        view.findViewById(R.id.text_no_tesi_available).setVisibility(View.VISIBLE);
                                        listView.setVisibility(View.GONE);
                                    } else {
                                        view.findViewById(R.id.text_no_tesi_available).setVisibility(View.GONE);
                                        listView.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else if (user.getRole().equals(RoleUser.STUDENT)) {
            Query queryStudent = tesiRef.whereEqualTo("student.id", user.getId()).orderBy("created_at", Query.Direction.DESCENDING);

            queryStudent.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    personalTesiList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Tesi tesi = doc.toObject(Tesi.class);
                        personalTesiList.add(tesi);
                    }

                    adapterPersonal.setTesiList(personalTesiList);

                    if (currentTab == TAB_PERSONAL) {
                        currentTesiList = personalTesiList;

                        if(currentTesiList.isEmpty()) {
                            view.findViewById(R.id.text_no_tesi_available).setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                        } else {
                            view.findViewById(R.id.text_no_tesi_available).setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

        } else {
            personalTesiList = new ArrayList<>();
            adapterPersonal.setTesiList(personalTesiList);

            if (currentTab == TAB_PERSONAL) {
                currentTesiList = personalTesiList;

                if(currentTesiList.isEmpty()) {
                    view.findViewById(R.id.text_no_tesi_available).setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.text_no_tesi_available).setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }
            }
        }

        TabLayout listTesiTabLayout = view.findViewById(R.id.tab_layout);
        listTesiTabLayout.selectTab(listTesiTabLayout.getTabAt(currentTab));
        listTesiTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == TAB_ALL) {
                    adapterAll.setTesiList(allTesiList);
                    listView.setAdapter(adapterAll);
                    currentTesiList = adapterAll.getTesiList();
                    currentTab = TAB_ALL;
                } else {
                    adapterPersonal.setTesiList(personalTesiList);
                    listView.setAdapter(adapterPersonal);
                    currentTesiList = adapterPersonal.getTesiList();
                    currentTab = tab.getPosition();
                }

                if(currentTesiList.isEmpty()) {
                    view.findViewById(R.id.text_no_tesi_available).setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.text_no_tesi_available).setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        if (currentTab == TAB_ALL) {
            listView.setAdapter(adapterAll);
        } else {
            listView.setAdapter(adapterPersonal);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(this);
        visualizeThesisViewModel.getThesis().observe(getViewLifecycleOwner(), tesi -> {
            if (tesi == null) {
                return;
            }

            navController.navigate(R.id.action_navigation_lista_tesi_to_visualizeTesiFragment);
        });

        if (menuProvider == null) {
            ListaTesiFragment queryTextListener = this;
            menuProvider = new MenuProvider() {
                @Override
                public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                    menu.clear();
                    menuInflater.inflate(R.menu.app_bar_list_tesi, menu);
                    MenuItem searchItem = menu.findItem(R.id.search_tesi);
                    searchView = (SearchView) searchItem.getActionView();
                    searchView.setQueryHint(getString(R.string.search_hint));
                    searchView.setOnQueryTextListener(queryTextListener);
                }

                @Override
                public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case SEARCH_ITEM_MENU:
                            searchView.setQuery("", false);

                            if (currentTab == TAB_PERSONAL) {
                                adapterPersonal.setTesiList(currentTesiList);
                                return true;
                            }

                            adapterAll.setTesiList(currentTesiList);
                            return true;

                        case FILTER_ITEM_MENU:
                            return true;
                        default:
                            return false;
                    }
                }
            };

            requireActivity().addMenuProvider(menuProvider);
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        List<Tesi> filteredList = new ArrayList<>();
        for (Tesi thesis : currentTesiList) {
            if (thesis.getNomeTesi().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(thesis);
            }
        }

        if (currentTab == TAB_ALL) {
            adapterAll.setTesiList(filteredList);
            return true;
        }

        adapterPersonal.setTesiList(filteredList);
        return true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("current_tab", String.valueOf(currentTab));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().removeMenuProvider(menuProvider);
        queryAll = null;
    }
}



