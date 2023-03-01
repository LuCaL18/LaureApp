package com.uniba.mobile.cddgl.laureapp.ui.task;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.Task;

import java.util.ArrayList;
import java.util.List;

public class ListaTaskFragment extends Fragment {

    private ListView listView;
    private ListaTaskAdapter adapter;
    private List<Task> dataList;
    private CollectionReference mCollection;
    private CollectionReference mCollection2;
    private BottomNavigationView navBar;
    private LoggedInUser userLogged = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_task, container, false);
        listView = view.findViewById(R.id.lista_task);
        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);
        dataList = new ArrayList<>();
        mCollection = FirebaseFirestore.getInstance().collection("task");
        adapter = new ListaTaskAdapter(getActivity(), mCollection);
        Log.d("ListaTesiFragment", "onCreateView() method called");
        listView.setAdapter(adapter);
        mCollection2 = FirebaseFirestore.getInstance().collection("users");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        mCollection2.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        break;
                    }
                }
            }
        });
        mCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("FirebaseListAdapter", "Listen failed.", e);
                    return;
                }
                dataList.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    Task task = doc.toObject(Task.class);
                    String idRelatore = doc.getString("relatore");
                    String idStudent = doc.getString("studenteId");
                    if (userLogged.getRole().equals(RoleUser.PROFESSOR) && idRelatore.equals(userLogged.getId())) {
                        dataList.add(task);
                    } else if (userLogged.getRole().equals(RoleUser.STUDENT) && idStudent.equals(userLogged.getId())) {
                        dataList.add(task);
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });
        return view;
    }
}
