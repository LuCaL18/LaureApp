package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.ClassificaTesi;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Adapter che funge da complementare a ListaTaskFragment per la
 * visualizzazione della lista dei task visibili all'utente
 *
 */

public class ClassificaTesiAdapter extends BaseAdapter {

    /* Contesto dello stato della classifica tesi */
    private final Context mContext;
    /* Lista delle tesi da visualizzare a schermo */
    private List<Tesi> mDataList;
    private final VisualizeThesisViewModel thesisViewModel;
    /* Lista delle tesi filtrate da visualizzare a schermo */
    private List<Tesi> filteredData;
    /* Mappa contenente la stringa relativa all'id dello studente e della sua lista di tesi preferite */
    private Map<String, List<Tesi>> classifica;

    public ClassificaTesiAdapter(Context context, VisualizeThesisViewModel model) {
        mContext = context;
        mDataList = new ArrayList<>();
        thesisViewModel = model;
        /* Istanziamo la lista delle tesi filtrate */
        filteredData = new ArrayList<>();
    }

    /**
     *
     * Metodo per il recupero della dimensione della lista di tesi
     *
     * @return
     */
    @Override
    public int getCount() {
        return mDataList.size();
    }

    /**
     *
     * Metodo per il recupero della posizione di una specifica tesi nel mDataList
     *
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    /**
     *
     * Metodo per il recupero del numero della posizione della tesi
     *
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     *
     * Metodo "getView" per la visualizzazione del layout relativo a classifica tesi, in cui
     * oltre alla visualizzazione della lista di tesi della classifica è possibile effettuare
     * alcune operazioni tramite degli imageButton, ovvero:
     *
     *   1. VISUALIZZAZIONE: l'utente ha la possibilità di visualizzare la tesi selezionata
     *                       con tutti i suoi dettagli
     *   2. ELIMINAZIONE   : l'utente ha la possibilità di rimuovere la tesi selezionata
     *                       dalla classifica
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /* Creazione della viewHolder responsabile della gestione della visualizzazione del layout */
        ViewHolder viewHolder;
        /* Recupero degli elementi del layout classifica tesi */
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.classifica_tesi, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView1 = convertView.findViewById(R.id.nometesi2);
            viewHolder.textView2 = convertView.findViewById(R.id.descrizione_tesi);
            viewHolder.imageButton1 = convertView.findViewById(R.id.visualizza_Tesi_classifica);
            viewHolder.imageButton2 = convertView.findViewById(R.id.deleteTesi);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Tesi tesi = mDataList.get(position);
        /* Recupero dei dati relativi al nome tesi e descrizione */
        if (tesi != null && tesi.getNomeTesi() != null && tesi.getDescrizione() != null) {
            viewHolder.textView1.setText(tesi.getNomeTesi());
            viewHolder.textView2.setText(tesi.getDescrizione());
        }
        /* imabeButton per permettere la visualizzazione a dettaglio della tesi selezionata */
        viewHolder.imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thesisViewModel.getThesis().setValue(tesi);
            }
        });

        viewHolder.imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Tesi tesi = mDataList.get(position);
                mDataList.remove(tesi);

                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    return;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String studenteId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference classificaRef = db.collection("tesi_classifiche").document(studenteId);
                Map<String, Object> updates = new HashMap<>();
                updates.put("tesi", getIdOfThesis(mDataList));

                classificaRef.update(updates);
            }
        });

        //make draggable view
        convertView.setLongClickable(true);

        return convertView;
    }

    /**
     *
     * Metodo per l'aggiornamento della classifica tesi
     *
     * @param newList
     */
    public void updateList(List<Tesi> newList) {
        mDataList.clear();
        mDataList.addAll(newList);
        notifyDataSetChanged();
    }

    /**
     *
     * Metodo per istanziare gli elementi del layout
     *
     */
    private static class ViewHolder {
        TextView textView1;
        TextView textView2;
        ImageButton imageButton1;
        ImageButton imageButton2;
    }

    public void insertItem(int position, Tesi tesi) {
        mDataList.add(position, tesi);
        notifyDataSetChanged();
    }

    public void removeItem(Tesi tesi) {
        mDataList.remove(tesi);
        notifyDataSetChanged();
    }

    public void setmDataList(List<Tesi> mDataList) {
        this.mDataList = mDataList;
        notifyDataSetChanged();
    }

    public List<Tesi> getmDataList() {
        return mDataList;
    }


    private List<String> getIdOfThesis(List<Tesi> tesiList) {

        List<String> listId = new ArrayList<>();

        for (Tesi tesi : tesiList) {
            listId.add(tesi.getId());
        }

        return listId;
    }
}

