package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import android.content.ClipData;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.MainActivity;
import com.uniba.mobile.cddgl.laureapp.MainViewModel;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.data.model.TesiClassifica;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.adapters.ClassificaTesiAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassificaTesiFragment extends Fragment {
    private ListView listView;
    private ClassificaTesiAdapter adapter;
    private List<Tesi> tesi;
    private BottomNavigationView navBar;
    private LoggedInUser user;
    private VisualizeThesisViewModel thesisViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classifica_tesi, container, false);

        thesisViewModel = new ViewModelProvider(requireParentFragment()).get(VisualizeThesisViewModel.class);

        listView = view.findViewById(R.id.classifica_tesi);
        navBar = getActivity().findViewById(R.id.nav_view);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String studenteId = currentUser.getUid();

        adapter = new ClassificaTesiAdapter(getContext(), thesisViewModel);
        listView.setAdapter(adapter);

        CollectionReference mCollection = FirebaseFirestore.getInstance().collection("tesi_classifiche");
        mCollection.whereEqualTo("studentId", studenteId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                tesi = new ArrayList<>();

                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    TesiClassifica classificaTesi = doc.toObject(TesiClassifica.class);

                    if (classificaTesi.getTesi().isEmpty()) {

                        tesi = new ArrayList<>();
                        adapter.setmDataList(tesi);

                    } else {

                        List<String> tesiId = classificaTesi.getTesi();

                        FirebaseFirestore.getInstance().collection("tesi").whereIn("id", tesiId)
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        List<Tesi> thesisList = new ArrayList<>();
                                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                            // Convert each document snapshot to a Thesis object
                                            Tesi thesis = document.toObject(Tesi.class);
                                            thesisList.add(thesis);
                                        }

                                        tesi = thesisList;
                                        adapter.setmDataList(tesi);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Classifica tesi", e.getMessage());
                                    }
                                });
                    }
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(data, shadowBuilder, null, 0);
                } else {
                    view.startDrag(data, shadowBuilder, null, 0);
                }

                return true;
            }
        });

        listView.setOnDragListener(new View.OnDragListener() {

            int previousPos = -1;

            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        listView.setNestedScrollingEnabled(false);
                        previousPos = listView.pointToPosition((int) event.getX(), (int) event.getY());
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        // highlight list item
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        int currentPos = listView.pointToPosition((int) event.getX(), (int) event.getY());
                        if (currentPos != AdapterView.INVALID_POSITION) {
                            if (currentPos != previousPos) {
                                // update the list item position
                                Tesi currentItem = (Tesi) adapter.getItem(currentPos);

                                adapter.removeItem(currentItem);
                                adapter.insertItem(previousPos, currentItem);

                                previousPos = currentPos;
                            }
                        }
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        // unhighlight list item
                        return true;
                    case DragEvent.ACTION_DROP:
                        tesi = adapter.getmDataList();
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        listView.setNestedScrollingEnabled(true);
                        return true;
                }
                return false;
            }
        });

        String[] opzioniOrdinamento = new String[]{"MENU", "Tesi A-Z", "Tesi Z-A", "Relatore A-Z", "Relatore Z-A", "Condividi"};
        ArrayAdapter<String> adapterOrdinamento = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, opzioniOrdinamento);
        adapterOrdinamento.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinnerOrdinamento = view.findViewById(R.id.spinner_classificatesi);
        spinnerOrdinamento.setAdapter(adapterOrdinamento);
        spinnerOrdinamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (tesi.isEmpty()) return;
                List<Tesi> listaTesiOrdinata = new ArrayList<>(tesi);
                String opzioneSelezionata = parent.getItemAtPosition(position).toString();
                switch (opzioneSelezionata) {
                    case "Tesi A-Z":
                        Collections.sort(listaTesiOrdinata, new Comparator<Tesi>() {
                            @Override
                            public int compare(Tesi t1, Tesi t2) {
                                return t1.getNomeTesi().compareTo(t2.getNomeTesi());
                            }
                        });
                        break;
                    case "Tesi Z-A":
                        Collections.sort(listaTesiOrdinata, new Comparator<Tesi>() {
                            @Override
                            public int compare(Tesi t1, Tesi t2) {
                                return t2.getNomeTesi().compareTo(t1.getNomeTesi());
                            }
                        });
                        break;
                    case "Relatore A-Z":
                        Collections.sort(listaTesiOrdinata, new Comparator<Tesi>() {
                            @Override
                            public int compare(Tesi t1, Tesi t2) {
                                return t1.getRelatore().getDisplayName().compareTo(t2.getRelatore().getDisplayName());
                            }
                        });
                        break;
                    case "Relatore Z-A":
                        Collections.sort(listaTesiOrdinata, new Comparator<Tesi>() {
                            @Override
                            public int compare(Tesi t1, Tesi t2) {
                                return t2.getRelatore().getDisplayName().compareTo(t1.getRelatore().getDisplayName());
                            }
                        });
                        break;
                    case "Condividi":
                        StringBuilder sb = new StringBuilder();
                        sb.append("Ecco la mia classifica di tesi preferite: " + "\n");
                        for (Tesi tesi : listaTesiOrdinata) {
                            sb.append("Nome tesi: " + tesi.getNomeTesi() + "\n");
                            sb.append("Professore: " + tesi.getRelatore().getDisplayName() + "\n");
                        }
                        String message = sb.toString();
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, message);
                        intent.setType("text/plain");
                        startActivity(Intent.createChooser(intent, "Condividi con:"));
                        break;
                }
                adapter.setmDataList(listaTesiOrdinata);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // punto 2: quando una tesi viene cliccata, aprire un'altra activity che visualizza maggiori informazioni sulla tesi selezionata
            Tesi tesi = mDataList.get(position);
            Intent intent = new Intent(getActivity(), DettagliTesiActivity.class);
            intent.putExtra("tesi", tesi);
            startActivity(intent);
            }
        });
        */
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mainViewModel.getUser().observe(getViewLifecycleOwner(), loggedInUser -> user = loggedInUser);

        NavController navController = NavHostFragment.findNavController(this);
        thesisViewModel.getThesis().observe(getViewLifecycleOwner(), tesi -> {
            if (tesi == null) {
                return;
            }

            navController.navigate(R.id.action_nav_classifica_tesi_to_visualizeTesiFragment);
        });
    }

    private void shareClassifica() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Classifica Tesi");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Ecco la mia classifica di tesi: " + tesi);
        startActivity(Intent.createChooser(shareIntent, "Condividi la classifica tramite"));
    }

    private List<String> getIdOfThesis(List<Tesi> tesiList) {

        List<String> listId = new ArrayList<>();

        for (Tesi tesi : tesiList) {
            listId.add(tesi.getId());
        }

        return listId;
    }

    @Override
    public void onResume() {
        super.onResume();
        navBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navBar.setVisibility(View.VISIBLE);

        NavigationView navigationView = requireActivity().findViewById(R.id.nav_view_menu);
        navigationView.getMenu().findItem(MainActivity.CLASSIFICA_TESI).setChecked(false);

        Map<String, Object> updates = new HashMap<>();
        updates.put("tesi", getIdOfThesis(tesi));

        FirebaseFirestore.getInstance().collection("tesi_classifiche").document(user.getId()).update(updates);
    }
}


