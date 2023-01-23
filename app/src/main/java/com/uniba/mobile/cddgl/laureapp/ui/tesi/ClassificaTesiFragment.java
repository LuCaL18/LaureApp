package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;

import java.util.ArrayList;

public class ClassificaTesiFragment extends Fragment {

    private ListView listView2;
    private ClassificaTesiAdapter listAdapter2;
    private ArrayList<Tesi> objectList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classifica_tesi, container, false);
        listView2 = view.findViewById(R.id.classifica_tesi);
        listAdapter2 = new ClassificaTesiAdapter(getActivity(), objectList);
        listView2.setAdapter(listAdapter2);
        return view;
    }

    public void addObjectToList(Tesi tesi) {
        objectList.add(tesi);
        listAdapter2.notifyDataSetChanged();
    }
}
