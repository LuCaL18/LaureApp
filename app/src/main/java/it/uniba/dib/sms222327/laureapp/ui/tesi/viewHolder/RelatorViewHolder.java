package it.uniba.dib.sms222327.laureapp.ui.tesi.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms222327.laureapp.R;
import it.uniba.dib.sms222327.laureapp.data.PersonaTesi;
/**
 * ViewHolder che si occupa della visualizzazione di un corelatore della lista corelatori di una tesi
 */
public class RelatorViewHolder extends RecyclerView.ViewHolder {
    private final TextView rName;
    private final ImageView deleteRelator;

    public RelatorViewHolder(@NonNull View itemView) {
        super(itemView);
        rName = itemView.findViewById(R.id.tv_relator_display_name);
        deleteRelator = itemView.findViewById(R.id.delete_image_view);
    }

    public void bind(PersonaTesi relator) {
        rName.setText(relator.getDisplayName());
    }

    public ImageView getDeleteRelator() {
        return deleteRelator;
    }
}
