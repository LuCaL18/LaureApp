package com.uniba.mobile.cddgl.laureapp.ui.tesi.viewHolder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.viewModels.VisualizeThesisViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.viewModels.TesiListViewModel;

public class ClassificaTesiViewHolder {

    private final View view;
    private final TextView nomeTesi;
    private final TextView descrizioneTextView;
    private final ImageButton deleteImageButton;

    public ClassificaTesiViewHolder(View itemView) {
        view = itemView;
        nomeTesi = itemView.findViewById(R.id.nometesi2);
        descrizioneTextView = itemView.findViewById(R.id.descrizione_tesi);
        deleteImageButton = itemView.findViewById(R.id.deleteTesi);
    }

    public void bind(Tesi tesi, VisualizeThesisViewModel thesisViewModel, TesiListViewModel tesiListViewModel) {
        if (tesi != null && tesi.getNomeTesi() != null && tesi.getDescrizione() != null) {
            nomeTesi.setText(tesi.getNomeTesi());
            descrizioneTextView.setText(tesi.getDescrizione());
        }

        view.setOnClickListener(v -> {
            view.setSelected(true);
            thesisViewModel.getThesis().setValue(tesi);
        });

        deleteImageButton.setOnClickListener(v -> tesiListViewModel.removeTesiFromTesiList(tesi));
    }
}
