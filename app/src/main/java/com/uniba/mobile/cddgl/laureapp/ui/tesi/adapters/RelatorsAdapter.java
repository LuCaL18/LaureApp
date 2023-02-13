package com.uniba.mobile.cddgl.laureapp.ui.tesi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.viewHolder.RelatorViewHolder;

import java.util.ArrayList;
import java.util.List;

public class RelatorsAdapter extends RecyclerView.Adapter<RelatorViewHolder> {
    private List<PersonaTesi> relators;

    public RelatorsAdapter() {
        relators = new ArrayList<>();
    }

    public RelatorsAdapter(List<PersonaTesi> relators) {
        this.relators = relators;
    }

    public void setSpeakers(List<PersonaTesi> relators) {
        this.relators = relators;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RelatorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_relator, parent, false);
        return new RelatorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatorViewHolder holder, int position) {
        PersonaTesi relator = relators.get(position);
        holder.bind(relator);
    }

    @Override
    public int getItemCount() {
        return relators.size();
    }
}
