package com.uniba.mobile.cddgl.laureapp.ui.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.StatoTask;
import com.uniba.mobile.cddgl.laureapp.data.model.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListaTaskAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Task> mDataList;
    private CollectionReference mCollectionRef;

    public ListaTaskAdapter(Context context, CollectionReference ref) {
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
                    Task task = doc.toObject(Task.class);
                    mDataList.add(task);
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
        com.uniba.mobile.cddgl.laureapp.ui.task.ListaTaskAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.lista_task, parent, false);
            viewHolder = new com.uniba.mobile.cddgl.laureapp.ui.task.ListaTaskAdapter.ViewHolder();
            viewHolder.textView1 = convertView.findViewById(R.id.nomeTask);
            viewHolder.textView2 = convertView.findViewById(R.id.scadenzaTask);
            viewHolder.imageButton1 = convertView.findViewById(R.id.visualizza_task1);
            viewHolder.progressBar = convertView.findViewById(R.id.progressBar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (com.uniba.mobile.cddgl.laureapp.ui.task.ListaTaskAdapter.ViewHolder) convertView.getTag();
        }
        Task task = mDataList.get(position);
        viewHolder.textView1.setText(task.getNometask());
        viewHolder.textView2.setText(task.getScadenza());
        StatoTask taskState = task.getStato();
        switch (taskState) {
            case NEW:
                viewHolder.progressBar.setProgress(10);
                break;
            case RUNNING:
                viewHolder.progressBar.setProgress(50);
                break;
            case END:
                viewHolder.progressBar.setProgress(100);
                break;
        };
        viewHolder.imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task task = mDataList.get(position);
                Intent intent = new Intent(mContext, Task.class);
                intent.putExtra("nometask", task.getNometask());
                intent.putExtra("stato", task.getStato());
                intent.putExtra("descrizione", task.getDescrizione());
                intent.putExtra("scadenza", task.getScadenza());
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView textView1;
        TextView textView2;
        ImageButton imageButton1;
        ProgressBar progressBar;
    }
}