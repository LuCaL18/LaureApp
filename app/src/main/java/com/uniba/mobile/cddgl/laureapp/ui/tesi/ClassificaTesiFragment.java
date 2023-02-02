package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassificaTesiFragment extends Fragment {
    private ListView listView;
    private ClassificaTesiAdapter adapter;
    private Map<String,List<Tesi>> classifica;
    private List<Tesi> dataList;
    private CollectionReference mCollection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classifica_tesi, container, false);
        listView = view.findViewById(R.id.classifica_tesi);
        dataList = new ArrayList<>();
        classifica = new HashMap<>();
        mCollection = FirebaseFirestore.getInstance().collection("tesi").document("nBaFQbLRbSWkyKIjlTZQGPGMQ072").collection("tesi_classifica");
        adapter = new ClassificaTesiAdapter(getActivity(), mCollection);
        Log.d("ClassificaTesiFragment", "onCreateView() method called");
        listView.setAdapter(adapter);
        mCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                classifica.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    Tesi tesi = doc.toObject(Tesi.class);
                    dataList.add(tesi);
                }
                classifica.put("classificaTesi",dataList);
                Log.d("ClassificaTesiFragment", "onCreateView() method called");
                adapter.notifyDataSetChanged();
            }
        });
        return view;
    }
}
