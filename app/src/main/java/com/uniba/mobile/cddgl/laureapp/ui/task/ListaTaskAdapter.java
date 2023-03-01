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

public class ListaTaskAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Task> mDataList;
    private CollectionReference mCollectionRef;
    private CollectionReference mCollection = FirebaseFirestore.getInstance().collection("users");
    private CollectionReference mCollection2 = FirebaseFirestore.getInstance().collection("tesi");
    private LoggedInUser userLogged = null;

    public ListaTaskAdapter(Context context, CollectionReference ref) {
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
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    Task task = doc.toObject(Task.class);
                    String idRelatore = doc.getString("relatore");
                    String idStudent = doc.getString("studenteId");
                    if (userLogged.getRole().equals(RoleUser.PROFESSOR) && idRelatore.equals(userLogged.getId())) {
                        mDataList.add(task);
                    } else if (userLogged.getRole().equals(RoleUser.STUDENT) && idStudent.equals(userLogged.getId())) {
                        mDataList.add(task);
                    }
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
            viewHolder.textView3 = convertView.findViewById(R.id.tesiTask);
            viewHolder.textView4 = convertView.findViewById(R.id.studenteTask);
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
        if (userLogged.getRole() == RoleUser.PROFESSOR) {
            viewHolder.textView3.setVisibility(View.VISIBLE);
            viewHolder.textView4.setVisibility(View.VISIBLE);
            mCollection2.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.e("FirebaseListAdapter", "Listen failed.", e);
                        return;
                    }
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Tesi tesi = doc.toObject(Tesi.class);
                        if (tesi.getId().equals(task.getTesiId())) {
                            viewHolder.textView3.setText(tesi.getNomeTesi());
                            viewHolder.textView4.setText(tesi.getStudent().getDisplayName());
                        }
                    }
                }
            });
        } else if (userLogged.getRole() == RoleUser.STUDENT) {
            viewHolder.textView3.setVisibility(View.GONE);
            viewHolder.textView4.setVisibility(View.GONE);
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

    private static class ViewHolder {
        TextView textView1;
        TextView textView2;
        TextView textView3;
        TextView textView4;
        ImageButton imageButton1;
        ProgressBar progressBar;
    }
}