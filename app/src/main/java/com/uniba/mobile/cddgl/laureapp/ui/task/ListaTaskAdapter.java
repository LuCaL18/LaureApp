package com.uniba.mobile.cddgl.laureapp.ui.task;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.TaskState;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.Task;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Adapter che funge da complementare a ListaTaskFragment per la
 * visualizzazione della lista dei task visibili all'utente
 *
 */

public class ListaTaskAdapter extends BaseAdapter {

    /* Contesto dello stato corrente della lista dei task */
    private final Context mContext;
    /* Lista dei task da visualizzare a schermo */
    private final List<Task> mDataList;
    /* CollectionReference */
    private CollectionReference mCollectionRef;
    /* CollectionReference per il recupero di tutti gli user istanziati su firebase */
    private CollectionReference mCollection = FirebaseFirestore.getInstance().collection("users");
    /* CollectionReference per il recupero di tutte le tesi istanziate su firebase */
    private CollectionReference mCollection2 = FirebaseFirestore.getInstance().collection("tesi");
    /* Oggetto di tipo LoggedInUser per memorizzare l'users attualmente loggato */
    private LoggedInUser userLogged = null;

    public ListaTaskAdapter(Context context, CollectionReference ref) {
        /* Istanziamo mContext, mDataList e mCollectionReference*/
        mContext = context;
        mDataList = new ArrayList<>();
        mCollectionRef = ref;
        /* Recupero dell'ID dell'utente attualmente loggato da utilizzare per il recupero dei suoi dati */
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        /* Utilizzo della mCollection per il recupero delle istanze degli users presenti nel database */
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
                        /* Notifico della modifica effettuata */
                        notifyDataSetChanged();
                        break;
                    }
                }
            }
        });
        /* Recupero dei dati da visualizzare in base al ruolo dell'user loggato */
        mCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("FirebaseListAdapter", "Listen failed.", e);
                    return;
                }
                mDataList.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    Task task = doc.toObject(Task.class);
                    /* Recupero degli id relativi a studente e relatore per effettuare i controlli */
                    String idRelatore = doc.getString("relatore");
                    String idStudent = doc.getString("studenteId");
                    /* Se l'utente loggato è un professore, aggiungere tutti i task delle tesi
                    *  in cui l'user è relatore o corelatore */
                    if (userLogged.getRole().equals(RoleUser.PROFESSOR) && idRelatore.equals(userLogged.getId())) {
                        mDataList.add(task);
                    } /* Se l'utente loggato è uno studente, aggiungere tutti i task della tesi
                      *  in cui l'user è studente associato a tale tesi */
                    else if (userLogged.getRole().equals(RoleUser.STUDENT) && idStudent.equals(userLogged.getId())) {
                        mDataList.add(task);
                    }
                }
                /* Notifica delle modifiche effettuate */
                notifyDataSetChanged();
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
     * Metodo "getView" per la visualizzazione a schermo degli elementi del layout per la
     * visualizzazione della lista di tutti i task
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        com.uniba.mobile.cddgl.laureapp.ui.task.ListaTaskAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            /* Recupero degli elementi del layout per la visualizzazione della lista task */
            convertView = LayoutInflater.from(mContext).inflate(R.layout.lista_task, parent, false);
            viewHolder = new com.uniba.mobile.cddgl.laureapp.ui.task.ListaTaskAdapter.ViewHolder();
            viewHolder.textView1 = convertView.findViewById(R.id.nomeTask);
            viewHolder.textView2 = convertView.findViewById(R.id.scadenzaTask);
            viewHolder.textView3 = convertView.findViewById(R.id.tesiTask);
            viewHolder.textView4 = convertView.findViewById(R.id.studenteTask);
            viewHolder.imageButton1 = convertView.findViewById(R.id.visualizza_task1);
            viewHolder.progressBar = convertView.findViewById(R.id.progressBar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (com.uniba.mobile.cddgl.laureapp.ui.task.ListaTaskAdapter.ViewHolder) convertView.getTag();
        }
        Task task = mDataList.get(position);
        /* Recupero dei dati relativi a nomeTask e scadenza di un task */
        viewHolder.textView1.setText(task.getNomeTask());
        viewHolder.textView2.setText(task.getScadenza());
        TaskState taskState = task.getStato();
        /* Switch utilizzato per differenziare lo stato di progresso della barra in base allo stato del task */
        switch (taskState) {
            case NEW:
                viewHolder.progressBar.setProgress(10);
                break;
            case STARTED:
                viewHolder.progressBar.setProgress(50);
                break;
            case COMPLETED:
                viewHolder.progressBar.setProgress(100);
                break;
            case CLOSED:
                viewHolder.progressBar.setProgress(0);
                break;
        }
        /* Se l'utente loggato è un PROFESSORE, visualizzare nel layout anche le informazioni relative allo specifico
        *  studente e tesi associate a quel task, in modo tale da ottimizzare l'utilizzo della visualizzazione della lista */
        if (userLogged.getRole() == RoleUser.PROFESSOR) {
            viewHolder.textView3.setVisibility(View.VISIBLE);
            viewHolder.textView4.setVisibility(View.VISIBLE);
            /* Utilizzo della mCollection2 per il recupero delle tesi istanziate all'interno del database */
            mCollection2.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.e("FirebaseListAdapter", "Listen failed.", e);
                        return;
                    }
                    /* Recupero della tesi corretta relativa al task associato */
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Tesi tesi = doc.toObject(Tesi.class);
                        /* Se l'id della tesi recuperata è equivalente all'id del task attuale, visualizzare anche le informazioni
                        *  su nome tesi e dello studente associato a tale tesi */
                        if (tesi.getId().equals(task.getTesiId())) {
                            viewHolder.textView3.setText(tesi.getNomeTesi());
                            viewHolder.textView4.setText(tesi.getStudent().getDisplayName());
                        }
                    }
                }
            });
        } /* Se l'utente è uno STUDENTE, ignorare la visualizzazione dei dati relativi alla tesi e studente in quanto
           * inutili e ridondanti poichè farebbero riferimento alla stessa tesi e studente */
        else if (userLogged.getRole() == RoleUser.STUDENT) {
            viewHolder.textView3.setVisibility(View.GONE);
            viewHolder.textView4.setVisibility(View.GONE);
        }
        /* imageButton per la visualizzazione del singolo task nei dettagli */
        viewHolder.imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task task = mDataList.get(position);
                /* Creazione di un bundle per salvare l'istanza del task visualizzata che va
                *  passata a VisualizzaTask */
                Bundle bundle = new Bundle();
                bundle.putString("nometask", task.getNomeTask());
                bundle.putString("stato", task.getStato().toString());
                bundle.putString("descrizione", task.getDescrizione());
                bundle.putString("scadenza", task.getScadenza());
                /* Chiamata al navigation gestore di VisualizzaTask */
                Navigation.findNavController(v).navigate(R.id.nav_visualizza_task, bundle);
            }
        });
        /* Ritorno la view da visualizzare a schermo */
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
        TextView textView3;
        TextView textView4;
        ImageButton imageButton1;
        ProgressBar progressBar;
    }
}