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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.ClassificaTesi;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;

import java.util.ArrayList;
import java.util.List;

public class ListAdapterTesi extends BaseAdapter {

    private final Context mContext;
    private final List<Tesi> mDataList;
    private CollectionReference mCollectionRef;

    public ListAdapterTesi(Context context, CollectionReference ref) {
        mContext = context;
        mDataList = new ArrayList<>();
        mCollectionRef = ref;
        mCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("FirebaseListAdapter", "Listen failed.", e);
                    return;
                }
                mDataList.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    Tesi tesi = doc.toObject(Tesi.class);
                    mDataList.add(tesi);
                }
                notifyDataSetChanged();
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
            viewHolder.imageButton2 = convertView.findViewById(R.id.addTesi);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Tesi tesi = mDataList.get(position);
        viewHolder.textView1.setText(tesi.getNomeTesi());
        viewHolder.textView2.setText(tesi.getRelatore());
        viewHolder.imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // handle click event for visualizzaTesi button
            }
        });
        viewHolder.imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // recupera l'istanza della tesi selezionata
                Tesi tesiSelezionata = mDataList.get(position);
                // recupero l'istanza dell'utente loggato
                LoggedInUser user;
                String studentId = user.getId();
                // crea un oggetto tesi_classifiche
                ClassificaTesi tesiClassifiche = new ClassificaTesi(tesiSelezionata,studentId);
                // aggiungi l'oggetto tesi_classifiche alla raccolta tesi_classifiche
                mCollectionRef.add(tesiClassifiche);
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        TextView textView1;
        TextView textView2;
        ImageButton imageButton1;
        ImageButton imageButton2;
    }
}


