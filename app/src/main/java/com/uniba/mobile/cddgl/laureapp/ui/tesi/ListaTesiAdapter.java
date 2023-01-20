package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;

import java.util.ArrayList;

public class ListaTesiAdapter extends ArrayAdapter<Tesi> {

    public ListaTesiAdapter (Context Context, ArrayList<Tesi> tesiArrayList) {
        super(context, R.layout.lista_tesi,tesiArrayList);
    }

    @NonNull
    @Override
    public view getView (int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Tesi tesi = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lista_tesi,parent,false);
        }
    }

}
