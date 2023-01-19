package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.R;

public class ListaTesiFragment extends Fragment {

    private FirebaseFirestore db;
    private TextView nomeTesiView;
    private TextView relatoreView;
    private ImageButton visualizzaButton;
    private ImageButton addButton;

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
        View view2 = inflater.inflate(R.layout.fragment_lista_tesi,container,false);
        View view = inflater.inflate(R.layout.lista_tesi,container,false);
        db = FirebaseFirestore.getInstance();
        nomeTesiView = view.findViewById(R.id.nometesi2);
        relatoreView = view.findViewById(R.id.nomerelatore2);
        visualizzaButton = view.findViewById(R.id.visualizzaTesi);
        addButton = view.findViewById(R.id.addTesi);

        addButton.setOnClickListener(v -> {

        });
        visualizzaButton.setOnClickListener(v -> {

        });
        return view2;
    }

}