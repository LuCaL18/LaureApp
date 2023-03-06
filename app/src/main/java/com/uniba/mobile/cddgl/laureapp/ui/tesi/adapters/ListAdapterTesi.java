package com.uniba.mobile.cddgl.laureapp.ui.tesi.adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.data.model.TesiClassifica;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.VisualizeThesisViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Adapter che funge da complementare a ListaTaskFragment per la
 * visualizzazione della lista dei task visibili all'utente
 *
 */

public class ListAdapterTesi extends BaseAdapter {

    /* Contesto dello stato della lista di tesi */
    private final Context mContext;
    private List<Tesi> tesiList;
    private VisualizeThesisViewModel thesisViewModel;

    public ListAdapterTesi(Context context, List<Tesi> tesiList, VisualizeThesisViewModel model) {
        mContext = context;
        this.tesiList = tesiList;
        thesisViewModel = model;
    }

    /**
     * Metodo per il recupero della dimensione della lista di tesi
     *
     * @return
     */
    @Override
    public int getCount() {
        return tesiList.size();
    }

    /**
     * Metodo per il recupero della posizione di una specifica tesi nel mDataList
     *
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return tesiList.get(position);
    }

    /**
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
     * Metodo "getView" per la visualizzazione della lista di tutte le tesi disponibili in base
     * al ruolo dello studente loggato sarà possibile effettuare le seguenti operazioni
     * <p>
     * 1. Scorrimento della lista di tutte le tesi
     * 1.1 Se l'utente loggato è un professore, potrà vedere la lista di tutte le tesi
     * di cui è relatore o corelatore
     * 1.2 Se l'utente loggato è uno studente, potrà vedere la lista di tutte le tesi
     * non ancora assegnate ad uno studente
     * 2. Condivisione dei dati di una tesi
     * 3. Visualizzazione dei dati di una tesi
     * 4. Aggiunta di una tesi all'interno di una propria classifica personale
     * (valida solamente per lo studente)
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
        /* Recupero degli elementi del layout per la visualizzazione della lista tesi */
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.lista_tesi, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView1 = convertView.findViewById(R.id.nometesi);
            viewHolder.textView2 = convertView.findViewById(R.id.nomerelatore);
            viewHolder.imageButton1 = convertView.findViewById(R.id.visualizzaTesi);
            viewHolder.imageButton2 = convertView.findViewById(R.id.share_tesi);
            viewHolder.imageButton3 = convertView.findViewById(R.id.addTesi);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Tesi tesi = tesiList.get(position);
        /* Recupero e visualizzazione dei dati relativi al nome della tesi e al relatore */
        viewHolder.textView1.setText(tesi.getNomeTesi());
        viewHolder.textView2.setText(tesi.getRelatore().getDisplayName());
        /* imageButton per la visualizzazione a dettaglio della tesi */
        viewHolder.imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thesisViewModel.getThesis().setValue(tesi);
            }
        });
        /* imageButton per la condivisione della tesi */
        viewHolder.imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });
        /* Verifico se l'utente loggato è uno STUDENTE o PROFESSORE per visualizzare o meno il bottone addTesi
        if (userLogged.getRole() == RoleUser.PROFESSOR) {
            viewHolder.imageButton3.setVisibility(View.GONE);
        } else if (userLogged.getRole() == RoleUser.STUDENT) {
            viewHolder.imageButton3.setVisibility(View.VISIBLE);
        }
        Se l'utente loggato è uno studente, potrà visualizzare l'imageButton relativo all'aggiunta
        *  di una tesi all'interno di una propria classifica personalizzata */
        viewHolder.imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Recupero dell'id dello studente attualmente loggato */
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String studenteId = currentUser.getUid();
                /* DocumentReference relativo all'istanza della classifica presente o meno all'interno di tesi classifiche (database) */
                DocumentReference classificaTesiDoc = FirebaseFirestore.getInstance().collection("tesi_classifiche").document(studenteId);
                Tesi tesiSelezionata = tesiList.get(position);
                classificaTesiDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        TesiClassifica tesiClassificate;
                        /* Verifica se l'istanza di tesiClassifica è già stata creata in precedenza */
                        if (documentSnapshot.exists()) {
                            /* Controllo extra se tesi già presente o meno da fare*/
                            tesiClassificate = documentSnapshot.toObject(TesiClassifica.class);
                            tesiClassificate.addTesi(tesiSelezionata.getId());
                        } else {
                            List<String> classifica = new ArrayList<>();
                            classifica.add(tesiSelezionata.getId());
                            tesiClassificate = new TesiClassifica(studenteId, classifica);
                        }

                        classificaTesiDoc.set(tesiClassificate).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Classifica tesi aggiornata con successo");
                            }
                        });
                    }
                });
            }
        });
        /* Ritorno della view da visualizzare a schermo */
        return convertView;
    }

    public List<Tesi> getTesiList() {
        return tesiList;
    }

    public void setTesiList(List<Tesi> tesiList) {
        this.tesiList = tesiList;
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
        ImageButton imageButton3;
    }
}


