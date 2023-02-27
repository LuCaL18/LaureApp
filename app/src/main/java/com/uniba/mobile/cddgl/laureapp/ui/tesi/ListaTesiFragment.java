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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.adapters.ListAdapterTesi;

import java.util.ArrayList;
import java.util.List;

public class ListaTesiFragment extends Fragment implements SearchView.OnQueryTextListener {
    private ListAdapterTesi adapter;
    private List<Tesi> tesiList;
    private VisualizeThesisViewModel visualizeThesisViewModel;
    private Query query;
    private MenuProvider menuProvider;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_tesi, container, false);
        ListView listView = view.findViewById(R.id.listatesi);

        visualizeThesisViewModel = new ViewModelProvider(requireParentFragment()).get(VisualizeThesisViewModel.class);

        tesiList = new ArrayList<>();

        adapter = new ListAdapterTesi(getContext(), new ArrayList<>(), visualizeThesisViewModel);
        listView.setAdapter(adapter);

        query = FirebaseFirestore.getInstance().collection("tesi").orderBy("created_at", Query.Direction.DESCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                tesiList.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    Tesi tesi = doc.toObject(Tesi.class);
                    tesiList.add(tesi);
                }

                adapter.setTesiList(tesiList);
            }
        });
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

                    if (menuItem.getItemId() == R.id.search_tesi) {
                        searchView.setQuery("", false);
                        adapter.setTesiList(tesiList);
                        return true;
                    }

                    return false;
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
        for (Tesi thesis : tesiList) {
            if (thesis.getNomeTesi().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(thesis);
            }
        }
        adapter.setTesiList(filteredList);
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().removeMenuProvider(menuProvider);
        query = null;
    }
}



