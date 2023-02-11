package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.uniba.mobile.cddgl.laureapp.data.model.ClassificaTesi;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassificaTesiFragment extends Fragment {
    private ListView listView;
    private ClassificaTesiAdapter adapter;
    private Map<String,ClassificaTesi> classifica;
    private ClassificaTesi dataList;
    private CollectionReference mCollection;
    private BottomNavigationView navBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classifica_tesi, container, false);
        listView = view.findViewById(R.id.classifica_tesi);
        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);
        classifica = new HashMap<>();
        dataList = null;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String studenteId = currentUser.getUid();
        mCollection = FirebaseFirestore.getInstance().collection("tesi_classifiche");
        adapter = new ClassificaTesiAdapter(getActivity(), mCollection);
        Log.d("ClassificaTesiFragment", "onCreateView() method called");
        listView.setAdapter(adapter);
        mCollection.whereEqualTo("studenteId", studenteId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                classifica.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    ClassificaTesi classificaTesi = doc.toObject(ClassificaTesi.class);
                    dataList = classificaTesi;
                }
                classifica.put("classificaTesi",dataList);
                Log.d("ClassificaTesiFragment", "onCreateView() method called");
                adapter.notifyDataSetChanged();
            }
        });
        String[] opzioniOrdinamento = new String[]{"MENU","Tesi A-Z","Tesi Z-A","Relatore A-Z","Relatore Z-A","Condividi"};
        ArrayAdapter<String> adapterOrdinamento = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, opzioniOrdinamento);
        adapterOrdinamento.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinnerOrdinamento = view.findViewById(R.id.spinner_classificatesi);
        spinnerOrdinamento.setAdapter(adapterOrdinamento);
        spinnerOrdinamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (dataList == null) return;
                List<Tesi> listaTesiOrdinata = new ArrayList<>(dataList.getTesi());
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
                                return t1.getRelatore().compareTo(t2.getRelatore());
                            }
                        });
                        break;
                    case "Relatore Z-A":
                        Collections.sort(listaTesiOrdinata, new Comparator<Tesi>() {
                            @Override
                            public int compare(Tesi t1, Tesi t2) {
                                return t2.getRelatore().compareTo(t1.getRelatore());
                            }
                        });
                        break;
                    case "Condividi":
                        StringBuilder sb = new StringBuilder();
                        sb.append("Ecco la mia classifica di tesi preferite: " + "\n");
                        for (Tesi tesi : listaTesiOrdinata) {
                            sb.append("Nome tesi: " + tesi.getNomeTesi() + "\n");
                            sb.append("Professore: " + tesi.getRelatore() + "\n");
                        }
                        String message = sb.toString();
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, message);
                        intent.setType("text/plain");
                        startActivity(Intent.createChooser(intent, "Condividi con:"));
                        break;
                }
                adapter.addTesi(listaTesiOrdinata);
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

    private void shareClassifica() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Classifica Tesi");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Ecco la mia classifica di tesi: " + dataList.getTesi());
        startActivity(Intent.createChooser(shareIntent, "Condividi la classifica tramite"));
    }

}


