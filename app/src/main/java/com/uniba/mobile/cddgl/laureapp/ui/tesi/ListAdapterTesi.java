package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import static android.content.ContentValues.TAG;

 import android.content.Context;
 import android.util.Log;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.BaseAdapter;
 import android.widget.ImageButton;
 import android.widget.TextView;

 import androidx.annotation.Nullable;

 import com.google.android.gms.tasks.OnSuccessListener;
 import com.google.firebase.auth.FirebaseAuth;
 import com.google.firebase.auth.FirebaseUser;
 import com.google.firebase.firestore.CollectionReference;
 import com.google.firebase.firestore.DocumentReference;
 import com.google.firebase.firestore.DocumentSnapshot;
 import com.google.firebase.firestore.EventListener;
 import com.google.firebase.firestore.FirebaseFirestore;
 import com.google.firebase.firestore.FirebaseFirestoreException;
 import com.google.firebase.firestore.QuerySnapshot;
 import com.uniba.mobile.cddgl.laureapp.R;
 import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;
 import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
 import com.uniba.mobile.cddgl.laureapp.data.model.TesiClassifica;
 import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
 import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;

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
    /* Lista delle tesi da visualizzare a schermo */
    private final List<Tesi> mDataList;
    /* Recupera l'ID del relatore attualmente loggato */
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String studenteId = currentUser.getUid();
    /* Oggetto di tipo LoggedInUser per memorizzare l'users attualmente loggato */
    private LoggedInUser userLogged;
    /* CollectionReference */
    private CollectionReference mCollectionRef;
    /* CollectionReference per il recupero di tutti gli user istanziati su firebase */
    private CollectionReference mCollection = FirebaseFirestore.getInstance().collection("users");
    /* CollectionReference per il recupero di tutte le tesi istanziate su firebase */
    private DocumentReference classificaTesiDoc = FirebaseFirestore.getInstance().collection("tesi_classifiche").document(studenteId);

    /**
     *
     * Metodo listAdapterTesi
     *
     * @param context
     * @param ref
     */
    public ListAdapterTesi(Context context, CollectionReference ref) {
        /* Istanziamo mContext, mDataList e mCollectionRef */
        mContext = context;
        mDataList = new ArrayList<>();
        mCollectionRef = ref;
        /* Recupera l'ID del relatore attualmente loggato */
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        /* Utilizzo della mCollection per il recupero di tutti gli utenti istanziati nel database */
        mCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("FirebaseListAdapter", "Listen failed.", e);
                    return;
                }
                /* Recupero di tutti gli attributi dell'user attualmente loggato */
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    LoggedInUser user = doc.toObject(LoggedInUser.class);
                    if (user.getId().equals(userId)) {
                        userLogged = user;
                        /* Notifica della modifica effettuata */
                        notifyDataSetChanged();
                        break;
                    }
                }
            }
        });
        /* Recupero delle tesi dal database in base al ruolo dell'user loggato */
        mCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("FirebaseListAdapter", "Listen failed.", e);
                    return;
                }
                mDataList.clear();
                /* Se l'utente loggato è un professore, aggiungere tutte le tesi
                 *  in cui l'user è relatore o corelatore */
                if (userLogged.getRole().equals(RoleUser.PROFESSOR)) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Tesi tesi = doc.toObject(Tesi.class);
                        /* Verifico se l'utente loggato è un coRelatore della tesi */
                        List<PersonaTesi> coRelatore = tesi.getCoRelatori();
                        if (coRelatore.isEmpty()) {
                            for (PersonaTesi p : coRelatore) {
                                /* Se l'id dell'user recuperato è equivalente a quello dell'utente loggato,
                                 * aggiungere la tesi al mDataList*/
                                if (p.getId().equals(userLogged.getId())) {
                                    mDataList.add(tesi);
                                }
                            }
                        }
                        /* Verifico se l'utente loggato è un relatore della tesi */
                        if (tesi.getRelatore().getId().equals(userLogged.getId())) {
                            mDataList.add(tesi);
                        }
                    }
                }
                /* Se l'utente loggato è un studente, aggiungere tutte le tesi
                *  non ancora assegnate ad uno studente */
                if (userLogged.getRole().equals(RoleUser.STUDENT)) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Tesi tesi = doc.toObject(Tesi.class);
                        if (tesi.getStudent() == null) {
                            mDataList.add(tesi);
                        }
                    }
                }
            }
        });
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
     * Metodo "getView" per la visualizzazione della lista di tutte le tesi disponibili in base
     * al ruolo dello studente loggato sarà possibile effettuare le seguenti operazioni
     *
     *    1. Scorrimento della lista di tutte le tesi
     *       1.1 Se l'utente loggato è un professore, potrà vedere la lista di tutte le tesi
     *           di cui è relatore o corelatore
     *       1.2 Se l'utente loggato è uno studente, potrà vedere la lista di tutte le tesi
     *           non ancora assegnate ad uno studente
     *    2. Condivisione dei dati di una tesi
     *    3. Visualizzazione dei dati di una tesi
     *    4. Aggiunta di una tesi all'interno di una propria classifica personale
     *       (valida solamente per lo studente)
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
        Tesi tesi = mDataList.get(position);
        /* Recupero e visualizzazione dei dati relativi al nome della tesi e al relatore */
        viewHolder.textView1.setText(tesi.getNomeTesi());
        viewHolder.textView2.setText(tesi.getRelatore().getDisplayName());
        /* imageButton per la visualizzazione a dettaglio della tesi */
        viewHolder.imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });
        /* imageButton per la condivisione della tesi */
        viewHolder.imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });
        /* Verifico se l'utente loggato è uno STUDENTE o PROFESSORE per visualizzare o meno il bottone addTesi */
        if (userLogged.getRole() == RoleUser.PROFESSOR) {
            viewHolder.imageButton3.setVisibility(View.GONE);
        } else if (userLogged.getRole() == RoleUser.STUDENT) {
            viewHolder.imageButton3.setVisibility(View.VISIBLE);
        }
        /* Se l'utente loggato è uno studente, potrà visualizzare l'imageButton relativo all'aggiunta
        *  di una tesi all'interno di una propria classifica personalizzata */
        viewHolder.imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Recupero dell'id dello studente attualmente loggato */
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String studenteId = currentUser.getUid();
                /* DocumentReference relativo all'istanza della classifica presente o meno all'interno di tesi classifiche (database) */
                DocumentReference classificaTesiDoc = FirebaseFirestore.getInstance().collection("tesi_classifiche").document(studenteId);
                Tesi tesiSelezionata = mDataList.get(position);
                classificaTesiDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        TesiClassifica tesiClassificate;
                        /* Verifica se l'istanza di tesiClassifica è già stata creata in precedenza */
                        if (documentSnapshot.exists()) {
                            tesiClassificate = documentSnapshot.toObject(TesiClassifica.class);
                            /* Verifica se la tesi selezionata non è stata
                             * precedentemente aggiunta all'interno della classifica */
                            if (!(tesiClassificate.getTesi().contains(tesiSelezionata))) {
                                tesiClassificate.addTesi(tesiSelezionata);
                            }
                        } /* Creazione di una nuova istanza all'interno di tesi classifiche */
                        else {
                            List<Tesi> classifica = new ArrayList<>();
                            classifica.add(tesiSelezionata);
                            tesiClassificate = new TesiClassifica(classifica, studenteId);
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




