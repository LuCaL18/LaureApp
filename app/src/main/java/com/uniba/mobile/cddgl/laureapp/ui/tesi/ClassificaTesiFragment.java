package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;

import java.util.ArrayList;

public class ClassificaTesiFragment extends Fragment {

    private ListView listView;
    private ClassificaTesiAdapter adapter;
    private ArrayList<Tesi> dataList;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classifica_tesi, container, false);
        listView = view.findViewById(R.id.classifica_tesi);
        dataList = new ArrayList<>();
        adapter = new ClassificaTesiAdapter(getActivity(),mDatabase);
        listView.setAdapter(adapter);
        mDatabase = FirebaseDatabase.getInstance().getReference("tesi_classifica");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Tesi tesi = postSnapshot.getValue(Tesi.class);
                    dataList.add(tesi);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    public void addObjectToList(Tesi tesi) {
        dataList.add(tesi);
        adapter.notifyDataSetChanged();
    }
}
