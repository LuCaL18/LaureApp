package com.uniba.mobile.cddgl.laureapp.ui.tesi.viewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;

public class RelatorViewHolder extends RecyclerView.ViewHolder {
    private final TextView rName;

    public RelatorViewHolder(@NonNull View itemView) {
        super(itemView);
        rName = itemView.findViewById(R.id.tv_relator_display_name);
    }

    public void bind(PersonaTesi relator) {
        rName.setText(relator.getDisplayName());
    }
}
