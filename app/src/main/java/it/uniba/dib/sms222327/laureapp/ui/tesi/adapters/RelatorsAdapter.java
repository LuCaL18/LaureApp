package it.uniba.dib.sms222327.laureapp.ui.tesi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms222327.laureapp.R;
import it.uniba.dib.sms222327.laureapp.data.PersonaTesi;
import it.uniba.dib.sms222327.laureapp.ui.tesi.CreaTesiFragment;
import it.uniba.dib.sms222327.laureapp.ui.tesi.VisualizeTesiFragment;
import it.uniba.dib.sms222327.laureapp.ui.tesi.viewHolder.RelatorViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter che si occupa della lista dei corelatori della tesi
 */
public class RelatorsAdapter extends RecyclerView.Adapter<RelatorViewHolder> {
    private List<PersonaTesi> relators;
    private boolean permissionDelete;
    private VisualizeTesiFragment tesiFragment;
    private CreaTesiFragment creaTesiFragment;

    public RelatorsAdapter() {
        relators = new ArrayList<>();
    }

    public RelatorsAdapter(List<PersonaTesi> relators, boolean permissionDelete, VisualizeTesiFragment tesiFragment) {
        this.relators = relators;
        this.permissionDelete = permissionDelete;
        this.tesiFragment = tesiFragment;
    }

    public RelatorsAdapter(List<PersonaTesi> relators, boolean permissionDelete, CreaTesiFragment tesiFragment) {
        this.relators = relators;
        this.permissionDelete = permissionDelete;
        this.creaTesiFragment = tesiFragment;
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
                Context context;
                int mod;
                if(tesiFragment==null){
                    context = creaTesiFragment.getContext();
                    mod = 1;
                }
                else{
                    context = tesiFragment.getContext();
                    mod = 0;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.title_dialog_delete_relator));
                builder.setMessage(context.getString(R.string.message_dialog_delete_relator, relator.getDisplayName()));
                if(mod==1){
                    builder.setPositiveButton(context.getString(R.string.yes_text), (dialog, which) -> creaTesiFragment.removeCoRelator(relator));
                }
                else {
                    builder.setPositiveButton(context.getString(R.string.yes_text), (dialog, which) -> tesiFragment.removeCoRelator(relator));
                }
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
