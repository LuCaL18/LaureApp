package com.uniba.mobile.cddgl.laureapp.ui.tesi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.data.model.TesiClassifica;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.VisualizeThesisViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassificaTesiAdapter extends BaseAdapter {

    private final Context mContext;
    private List<Tesi> mDataList;
    private VisualizeThesisViewModel thesisViewModel;

    public ClassificaTesiAdapter(Context context, VisualizeThesisViewModel model) {
        mContext = context;
        mDataList = new ArrayList<>();
        thesisViewModel = model;
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
            viewHolder.textView2 = convertView.findViewById(R.id.descrizione_tesi);
            viewHolder.imageButton1 = convertView.findViewById(R.id.visualizza_Tesi_classifica);
            viewHolder.imageButton2 = convertView.findViewById(R.id.deleteTesi);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Tesi tesi = mDataList.get(position);

        if (tesi != null && tesi.getNomeTesi() != null && tesi.getDescrizione() != null) {
            viewHolder.textView1.setText(tesi.getNomeTesi());
            viewHolder.textView2.setText(tesi.getDescrizione());
        }

        viewHolder.imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thesisViewModel.getThesis().setValue(tesi);
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String studenteId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        viewHolder.imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference classificaRef = db.collection("tesi_classifiche").document(studenteId);
                Tesi tesi = mDataList.get(position);
                mDataList.remove(tesi);

                Map<String, Object> updates = new HashMap<>();
                updates.put("tesi", getIdOfThesis(mDataList));

                classificaRef.update(updates);
            }
        });

        //make draggable view
        convertView.setLongClickable(true);

        return convertView;
    }

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

