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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
        View view = inflater.inflate(R.layout.fragment_lista_tesi,container,false);
        ListView listView = view.findViewById(R.id.listatesi);
        return view;
    }

}