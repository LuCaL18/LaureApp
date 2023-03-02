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

public class ListAdapterTesi extends BaseAdapter {

    private final Context mContext;
    private final List<Tesi> mDataList;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String studenteId = currentUser.getUid();
    private LoggedInUser userLogged;
    private CollectionReference mCollectionRef;
    private CollectionReference mCollection = FirebaseFirestore.getInstance().collection("users");
    private DocumentReference classificaTesiDoc = FirebaseFirestore.getInstance().collection("tesi_classifiche").document(studenteId);

    public ListAdapterTesi(Context context, CollectionReference ref) {
        mContext = context;
        mDataList = new ArrayList<>();
        mCollectionRef = ref;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        mCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("FirebaseListAdapter", "Listen failed.", e);
                    return;
                }
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    LoggedInUser user = doc.toObject(LoggedInUser.class);
                    if (user.getId().equals(userId)) {
                        userLogged = user;
                        notifyDataSetChanged();
                        break;
                    }
                }
            }
        });
        mCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("FirebaseListAdapter", "Listen failed.", e);
                    return;
                }
                mDataList.clear();
                if (userLogged.getRole().equals(RoleUser.PROFESSOR)) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Tesi tesi = doc.toObject(Tesi.class);
                        List<PersonaTesi> coRelatore = tesi.getCoRelatori();
                        if (coRelatore.isEmpty()) {
                            for (PersonaTesi p : coRelatore) {
                                if (p.getId().equals(userLogged.getId())) {
                                    mDataList.add(tesi);
                                }
                            }
                        }
                        if (tesi.getRelatore().getId().equals(userLogged.getId())) {
                            mDataList.add(tesi);
                        }
                    }
                }
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

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
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
        viewHolder.textView1.setText(tesi.getNomeTesi());
        viewHolder.textView2.setText(tesi.getRelatore().getDisplayName());
        viewHolder.imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });
        viewHolder.imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });
        // Verifico se l'utente loggato è uno STUDENTE o PROFESSORE per visualizzare o meno il bottone addTesi
        if (userLogged.getRole() == RoleUser.PROFESSOR) {
            viewHolder.imageButton3.setVisibility(View.GONE);
        } else if (userLogged.getRole() == RoleUser.STUDENT) {
            viewHolder.imageButton3.setVisibility(View.VISIBLE);
        }
        viewHolder.imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String studenteId = currentUser.getUid();
                DocumentReference classificaTesiDoc = FirebaseFirestore.getInstance().collection("tesi_classifiche").document(studenteId);
                Tesi tesiSelezionata = mDataList.get(position);
                classificaTesiDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        TesiClassifica tesiClassificate;
                        if (documentSnapshot.exists()) {
                            tesiClassificate = documentSnapshot.toObject(TesiClassifica.class);
                            if (!(tesiClassificate.getTesi().contains(tesiSelezionata))) {
                                tesiClassificate.addTesi(tesiSelezionata);
                            }
                        } else {
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
        return convertView;
    }

    private static class ViewHolder {
        TextView textView1;
        TextView textView2;
        ImageButton imageButton1;
        ImageButton imageButton2;
        ImageButton imageButton3;
    }
}




