package com.uniba.mobile.cddgl.laureapp.ui.tesi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.VisualizeTesiFragment;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.viewHolder.RelatorViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter che si occupa della lista dei corelatori della tesi
 */
public class RelatorsAdapter extends RecyclerView.Adapter<RelatorViewHolder> {
    private List<PersonaTesi> relators;
    private boolean permissionDelete;
    private VisualizeTesiFragment tesiFragment;

    public RelatorsAdapter() {
        relators = new ArrayList<>();
    }

    public RelatorsAdapter(List<PersonaTesi> relators, boolean permissionDelete, VisualizeTesiFragment tesiFragment) {
        this.relators = relators;
        this.permissionDelete = permissionDelete;
        this.tesiFragment = tesiFragment;
    }

    public void setRelators(List<PersonaTesi> relators) {
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

        ImageView deleteButton = holder.getDeleteRelator();

        if(permissionDelete) {
            deleteButton.setClickable(true);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(view -> {

                Context context = tesiFragment.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.title_dialog_delete_relator));
                builder.setMessage(context.getString(R.string.message_dialog_delete_relator, relator.getDisplayName()));
                builder.setPositiveButton(context.getString(R.string.yes_text), (dialog, which) -> tesiFragment.removeCoRelator(relator));
                builder.setNegativeButton(context.getString(R.string.no_text), null);
                builder.create().show();
            });
        }

    }

    @Override
    public int getItemCount() {
        return relators.size();
    }
}
