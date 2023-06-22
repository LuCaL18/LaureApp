package it.uniba.dib.sms222327.laureapp.ui.tesi.viewHolder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import it.uniba.dib.sms222327.laureapp.R;
import it.uniba.dib.sms222327.laureapp.data.model.Tesi;
import it.uniba.dib.sms222327.laureapp.ui.tesi.viewModels.VisualizeThesisViewModel;
import it.uniba.dib.sms222327.laureapp.ui.tesi.viewModels.TesiListViewModel;

/**
 * ViewHolder che si occupa della visualizzazione di una tesi nella classifica
 */
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
