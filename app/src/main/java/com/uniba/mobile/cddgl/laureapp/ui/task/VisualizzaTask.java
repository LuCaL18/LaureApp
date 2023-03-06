package com.uniba.mobile.cddgl.laureapp.ui.task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.uniba.mobile.cddgl.laureapp.R;

public class VisualizzaTask extends Fragment {

    private BottomNavigationView navBar;
    private TextView nometaskTextView,descrizioneTextView,statoTextView,scadenzaTextView;

    public VisualizzaTask() {
        //
    }

    public static VisualizzaTask newInstance() {
        return new VisualizzaTask();
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.visualizza_task, container, false);

        nometaskTextView = view.findViewById(R.id.nome_task);
        descrizioneTextView = view.findViewById(R.id.descrizione);
        statoTextView = view.findViewById(R.id.stato);
        scadenzaTextView = view.findViewById(R.id.scadenza);

        Bundle bundle = getArguments();
        if (bundle != null) {
            nometaskTextView.setText(bundle.getString("nometask"));
            descrizioneTextView.setText(bundle.getString("descrizione"));
            statoTextView.setText(bundle.getString("stato"));
            scadenzaTextView.setText(bundle.getString("scadenza"));
        }

        return view;
    }


}
