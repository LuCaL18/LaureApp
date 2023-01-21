package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;

public class ListaTesiFragment extends Fragment {

    ListView listview;
    FirebaseListAdapter adapter;

    public ListaTesiFragment() {
        //
    }

    public static ListaTesiFragment newInstance() {
        return new ListaTesiFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_tesi,container,false);
        Query query = FirebaseDatabase.getInstance().getReference().child("tesi");
        listview = (ListView) findViewById(R.id.listatesi);
        FirebaseListOptions<Tesi> options = new FirebaseListOptions.Builder<Tesi>()
                .setLayout(R.layout.lista_tesi)
                .setLifecycleOwner(start.this)
                .setQuery(query, Tesi.class)
                .build();
        adapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Object model, int position) {
                TextView nomeTesi = v.findViewById(R.id.nometesi2);
                TextView relatoreTesi = v.findViewById(R.id.nomerelatore2);
                ImageButton visualizzaTesi;
                ImageButton addTesi;
                Tesi tesi = (Tesi) model;
                nomeTesi.getText(tesi.getNomeTesi().toString());
                relatoreTesi.getText(tesi.getRelatore().toString());
            }
        };
        listview.setAdapter(adapter);
        return view;
    }

    public void onStart () {
        super.onStart();
        adapter.startListening();
    }

    public void onStop () {
        super.onStop();
        adapter.stopListening();
    }

}