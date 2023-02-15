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
import com.uniba.mobile.cddgl.laureapp.data.model.ClassificaTesi;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.VisualizeThesisViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListAdapterTesi extends BaseAdapter {

    private final Context mContext;
    private List<Tesi> tesiList;
    private VisualizeThesisViewModel thesisViewModel;

    public ListAdapterTesi(Context context, List<Tesi> tesiList, VisualizeThesisViewModel model) {
        mContext = context;
        this.tesiList = tesiList;
        thesisViewModel = model;
    }

    @Override
    public int getCount() {
        return tesiList.size();
    }

    @Override
    public Object getItem(int position) {
        return tesiList.get(position);
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
        Tesi tesi = tesiList.get(position);
        viewHolder.textView1.setText(tesi.getNome_tesi());
        viewHolder.textView2.setText(tesi.getProfessor().getDisplayName());
        viewHolder.imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thesisViewModel.getThesis().setValue(tesi);
            }
        });
        viewHolder.imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });
        viewHolder.imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String studenteId = currentUser.getUid();
                DocumentReference classificaTesiDoc = FirebaseFirestore.getInstance().collection("tesi_classifiche").document(studenteId);
                Tesi tesiSelezionata = tesiList.get(position);
                classificaTesiDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ClassificaTesi tesiClassificate;
                        if (documentSnapshot.exists()) {
                            tesiClassificate = documentSnapshot.toObject(ClassificaTesi.class);
                            tesiClassificate.addTesi(tesiSelezionata);
                        } else {
                            List<Tesi> classifica = new ArrayList<>();
                            classifica.add(tesiSelezionata);
                            tesiClassificate = new ClassificaTesi(classifica, studenteId);
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

    public List<Tesi> getTesiList() {
        return tesiList;
    }

    public void setTesiList(List<Tesi> tesiList) {
        this.tesiList = tesiList;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView textView1;
        TextView textView2;
        ImageButton imageButton1;
        ImageButton imageButton2;
        ImageButton imageButton3;
    }
}


