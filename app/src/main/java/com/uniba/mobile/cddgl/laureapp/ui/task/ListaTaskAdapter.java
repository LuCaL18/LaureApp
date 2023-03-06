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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.TaskState;
import com.uniba.mobile.cddgl.laureapp.data.model.Task;

import java.util.ArrayList;
import java.util.List;

public class ListaTaskAdapter extends BaseAdapter {

    private final Context mContext;
    private List<Task> mDataList;

    public ListaTaskAdapter(Context context, List<Task> list) {
        mContext = context;
        mDataList = list;
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
        viewHolder.textView1.setText(task.getNomeTask());
        viewHolder.textView2.setText(task.getScadenza());
        TaskState taskState = task.getStato();
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
        viewHolder.imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task task = mDataList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("nometask", task.getNomeTask());
                bundle.putString("stato", task.getStato().toString());
                bundle.putString("descrizione", task.getDescrizione());
                bundle.putString("scadenza", task.getScadenza());
                Navigation.findNavController(v).navigate(R.id.nav_visualizza_task, bundle);
            }
        });
        return convertView;
    }

    public List<Task> getmDataList() {
        return mDataList;
    }

    public void setmDataList(List<Task> mDataList) {
        this.mDataList = mDataList;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView textView1;
        TextView textView2;
        ImageButton imageButton1;
        ProgressBar progressBar;
    }
}