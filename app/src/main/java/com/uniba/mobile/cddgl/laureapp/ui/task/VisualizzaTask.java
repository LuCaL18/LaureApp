package com.uniba.mobile.cddgl.laureapp.ui.task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.uniba.mobile.cddgl.laureapp.R;

public class VisualizzaTask extends Fragment {

    private BottomNavigationView navBar;
    private View root;

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
        root = inflater.inflate(R.layout.visualizza_task, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            TextView title = root.findViewById(R.id.nometask2);
            title.setText(bundle.getString("nometask"));
            // Card descrizione
            MaterialCardView cardDescrizione = root.findViewById(R.id.cv_descrizione);
            LinearLayout cvDescrizioneLayout = root.findViewById(R.id.descrizione_task);
            TextView descrizioneTask = root.findViewById(R.id.tv_descrizione3);
            cardDescrizione.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cvDescrizioneLayout.getVisibility() == View.GONE) {
                        cvDescrizioneLayout.setVisibility(View.VISIBLE);
                    } else {
                        cvDescrizioneLayout.setVisibility(View.GONE);
                    }
                }
            });
            descrizioneTask.setText(bundle.getString("descrizione"));
            // Card stato
            MaterialCardView cardStato = root.findViewById(R.id.cv_stato);
            LinearLayout cvStatoLayout = root.findViewById(R.id.stato_task);
            TextView statoTask = root.findViewById(R.id.tv_stato3);
            cardStato.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cvStatoLayout.getVisibility() == View.GONE) {
                        cvStatoLayout.setVisibility(View.VISIBLE);
                    } else {
                        cvStatoLayout.setVisibility(View.GONE);
                    }
                }
            });
            statoTask.setText(bundle.getString("stato"));
            // Card scadenza
            MaterialCardView cardScadenza = root.findViewById(R.id.cv_scadenza);
            LinearLayout cvScadenzaLayout = root.findViewById(R.id.scadenza_task);
            TextView scadenzaTask = root.findViewById(R.id.tv_scadenza3);
            cardScadenza.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cvScadenzaLayout.getVisibility() == View.GONE) {
                        cvScadenzaLayout.setVisibility(View.VISIBLE);
                    } else {
                        cvScadenzaLayout.setVisibility(View.GONE);
                    }
                }
            });
            scadenzaTask.setText(bundle.getString("scadenza"));
        }

        return root;
    }

}