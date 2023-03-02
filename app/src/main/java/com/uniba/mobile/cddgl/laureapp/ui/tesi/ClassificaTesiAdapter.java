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
import com.uniba.mobile.cddgl.laureapp.data.model.TesiClassifica;
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
    /* Lista delle tesi filtrate da visualizzare a schermo */
    private List<Tesi> filteredData;
    /* Mappa contenente la stringa relativa all'id dello studente e della sua lista di tesi preferite */
    private Map<String, List<Tesi>> classifica;

    public ClassificaTesiAdapter(Context context, CollectionReference ref) {
        /* Istanziamo mContext, mDatalist e classifica */
        mContext = context;
        mDataList = new ArrayList<>();
        classifica = new HashMap<>();
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("FirebaseListAdapter", "Listen failed.", e);
                    return;
                }
                mDataList.clear();
                /* Recupero della classifica tesi */
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    TesiClassifica tesiClassifica = doc.toObject(TesiClassifica.class);
                    mDataList = tesiClassifica.getTesi();
                }
                classifica.put("classificaTesi", mDataList);
                /* Notifico le modifiche effettuate */
                notifyDataSetChanged();
            }
        });
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
            viewHolder.textView2 = convertView.findViewById(R.id.nomerelatore2);
            viewHolder.imageButton1 = convertView.findViewById(R.id.visualizzaTesi2);
            viewHolder.imageButton2 = convertView.findViewById(R.id.deleteTesi);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Tesi tesi = mDataList.get(position);
        /* Recupero dei dati relativi al nome tesi e relatore */
        if (tesi != null && tesi.getNomeTesi() != null && tesi.getRelatore() != null) {
            viewHolder.textView1.setText(tesi.getNomeTesi());
            viewHolder.textView2.setText(tesi.getRelatore().getDisplayName());
        }
        /* imabeButton per permettere la visualizzazione a dettaglio della tesi selezionata */
        viewHolder.imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // qui puoi inserire il codice per la visualizzazione della tesi
            }
        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        /* Recupera l'ID del relatore attualmente loggato */
        String studenteId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        /* Recupero dell'istanza presente all'interno del database associato allo studente loggato */
        DocumentReference classificaRef = db.collection("tesi_classifiche").document(studenteId);
        /* imageButton per permettere all'utente di rimuovere una tesi dalla classifica */
        viewHolder.imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Recupero dell'istanza della tesi selezionata da rimuovere */
                Tesi tesi = mDataList.get(position);
                /* Chiamata al metodo remove per eliminare la tesi */
                mDataList.remove(tesi);
                /* Aggiornamento della classifica tesi senza la tesi selezionata da rimuovere */
                classificaRef.set(new TesiClassifica(mDataList, studenteId));
            }
        });
        /* Ritorno della view da mostrare a schermo */
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

    /**
     *
     * Metodo per l'aggiunta di una nuova classifica tesi
     *
     * @param tesi
     */
    public void addTesi(List<Tesi> tesi) {
        mDataList = tesi;
        notifyDataSetChanged();
    }

}

