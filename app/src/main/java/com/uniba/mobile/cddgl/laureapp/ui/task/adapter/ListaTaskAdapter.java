package com.uniba.mobile.cddgl.laureapp.ui.task.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.Task;
import com.uniba.mobile.cddgl.laureapp.ui.task.viewHolder.TaskListViewHolder;

import java.util.List;

/**
 * Adapter che funge da complementare a ListaTaskFragment per la
 * visualizzazione della lista dei task visibili all'utente
 */

public class ListaTaskAdapter extends BaseAdapter {

    private static final String CLASS_ID = "ListaTaskAdapter";

    /* Contesto dello stato corrente della lista dei task */
    private final Context mContext;
    private List<Task> mDataList;

    private final LoggedInUser userLogged;

    public ListaTaskAdapter(Context context, List<Task> list, LoggedInUser user) {
        mContext = context;
        mDataList = list;
        userLogged = user;
    }

    /**
     * Metodo per il recupero della dimensione della lista di tesi
     *
     * @return
     */
    @Override
    public int getCount() {
        return mDataList.size();
    }

    /**
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
        TaskListViewHolder viewHolder;
        try {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.lista_task, parent, false);
                viewHolder = new TaskListViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (TaskListViewHolder) convertView.getTag();
            }

            Task task = mDataList.get(position);
            viewHolder.bind(task, userLogged);

        } catch (Exception e) {
            Log.e(CLASS_ID, "Error in getView: ", e);
        }

        /* Ritorno la view da visualizzare a schermo */
        return convertView;
    }

    public List<Task> getmDataList() {
        return mDataList;
    }

    public void setmDataList(List<Task> mDataList) {
        this.mDataList = mDataList;
        notifyDataSetChanged();
    }
}