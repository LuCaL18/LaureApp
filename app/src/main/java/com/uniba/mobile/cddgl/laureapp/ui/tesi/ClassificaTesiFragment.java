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

import java.util.HashMap;
import java.util.Map;

public class ClassificaTesiFragment extends Fragment {
    private ListView listView;
    private ClassificaTesiAdapter adapter;
    private Map<String,ClassificaTesi> classifica;
    private ClassificaTesi dataList;
    private CollectionReference mCollection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classifica_tesi, container, false);
        listView = view.findViewById(R.id.classifica_tesi);
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
        return view;
    }
}


