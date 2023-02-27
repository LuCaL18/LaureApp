package com.uniba.mobile.cddgl.laureapp.ui.tesi;

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
import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;

import java.util.ArrayList;
import java.util.List;

public class ListaTesiFragment extends Fragment {

    private ListView listView;
    private ListAdapterTesi adapter;
    private List<Tesi> dataList;
    private CollectionReference mCollection;
    private CollectionReference mCollection2;
    private BottomNavigationView navBar;
    private LoggedInUser userLogged = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_tesi, container, false);
        listView = view.findViewById(R.id.listatesi);
        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);
        dataList = new ArrayList<>();
        mCollection = FirebaseFirestore.getInstance().collection("tesi");
        adapter = new ListAdapterTesi(getActivity(), mCollection);
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
                if (userLogged.getRole().equals(RoleUser.PROFESSOR)) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Tesi tesi = doc.toObject(Tesi.class);
                        List<PersonaTesi> coRelatore = tesi.getCoRelatori();
                        if (coRelatore.isEmpty()) {
                            for (PersonaTesi p : coRelatore) {
                                if (p.getId().equals(userLogged.getId())) {
                                    dataList.add(tesi);
                                }
                            }
                        }
                        if (tesi.getRelatore().getId().equals(userLogged.getId())) {
                            dataList.add(tesi);
                        }
                    }
                }
                if (userLogged.getRole().equals(RoleUser.STUDENT)) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Tesi tesi = doc.toObject(Tesi.class);
                        if (tesi.getStudent() == null) {
                            dataList.add(tesi);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        return view;
    }
}