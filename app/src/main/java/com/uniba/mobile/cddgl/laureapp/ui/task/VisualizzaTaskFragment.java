package com.uniba.mobile.cddgl.laureapp.ui.task;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Task;

import java.util.ArrayList;
import java.util.List;

public class VisualizzaTaskFragment extends Fragment {
    private ListView listView;
    private VisualizzaTaskAdapter adapter;
    private List<Task> dataList;
    private CollectionReference mCollection;
    private BottomNavigationView navBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visualizza_task, container, false);
        listView = view.findViewById(R.id.visualizza_task);
        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);
        dataList = new ArrayList<>();
        mCollection = FirebaseFirestore.getInstance().collection("task");
        adapter = new VisualizzaTaskAdapter(getActivity(), mCollection);
        Log.d("ListaTesiFragment", "onCreateView() method called");
        listView.setAdapter(adapter);
        mCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                dataList.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    Task task = doc.toObject(Task.class);
                    dataList.add(task);
                }
                Log.d("VisualizzaTaskFragment", "onCreateView() method called");
                adapter.notifyDataSetChanged();
            }
        });
        return view;
    }
}
