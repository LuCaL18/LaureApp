package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.R;

public class TesiPreferiteFragment extends Fragment {

    private FirebaseFirestore db;
    private TextView nomeTesiView;
    private TextView relatoreView;
    private ImageButton visualizzaButton;
    private ImageButton deleteButton;

    public TesiPreferiteFragment() {
        //
    }

    public static TesiPreferiteFragment newInstance() {
        return new TesiPreferiteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view2 = inflater.inflate(R.layout.fragment_tesi_preferite,container,false);
        View view = inflater.inflate(R.layout.lista_tesipreferite,container,false);
        db = FirebaseFirestore.getInstance();
        nomeTesiView = view.findViewById(R.id.nometesi);
        relatoreView = view.findViewById(R.id.nomerelatore);
        visualizzaButton = view.findViewById(R.id.visualizza);
        deleteButton = view.findViewById(R.id.delete);

        visualizzaButton.setOnClickListener(v -> {

        });
        deleteButton.setOnClickListener(v -> {

        });
        return view2;
    }

}