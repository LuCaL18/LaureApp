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

public class ClassificaTesiAdapter extends BaseAdapter {

    private final Context mContext;
    private List<Tesi> mDataList;
    private List<Tesi> filteredData;
    private Map<String, List<Tesi>> classifica;

    public ClassificaTesiAdapter(Context context, CollectionReference ref) {
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
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    ClassificaTesi classificaTesi = doc.toObject(ClassificaTesi.class);
                    mDataList = classificaTesi.getTesi();
                }
                classifica.put("classificaTesi", mDataList);
                notifyDataSetChanged();
            }
        });
        filteredData = new ArrayList<>();
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
        if (tesi != null && tesi.getNomeTesi() != null && tesi.getRelatore() != null) {
            viewHolder.textView1.setText(tesi.getNomeTesi());
            viewHolder.textView2.setText(tesi.getRelatore().getDisplayName());
        }
        viewHolder.imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // qui puoi inserire il codice per la visualizzazione della tesi
            }
        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String studenteId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference classificaRef = db.collection("tesi_classifiche").document(studenteId);
        viewHolder.imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tesi tesi = mDataList.get(position);
                mDataList.remove(tesi);
                classificaRef.set(new ClassificaTesi(mDataList, studenteId));
            }
        });

        return convertView;
    }

    public void updateList(List<Tesi> newList) {
        mDataList.clear();
        mDataList.addAll(newList);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView textView1;
        TextView textView2;
        ImageButton imageButton1;
        ImageButton imageButton2;
    }

    public void addTesi(List<Tesi> tesi) {
        mDataList = tesi;
        notifyDataSetChanged();
    }

}

