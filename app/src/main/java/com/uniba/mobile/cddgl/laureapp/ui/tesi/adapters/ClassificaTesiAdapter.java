package com.uniba.mobile.cddgl.laureapp.ui.tesi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.VisualizeThesisViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.viewModels.TesiListViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter che funge da complementare a ListaTaskFragment per la
 * visualizzazione della lista dei task visibili all'utente
 */

public class ClassificaTesiAdapter extends BaseAdapter {

    /* Contesto dello stato della classifica tesi */
    private final Context mContext;
    /* Lista delle tesi da visualizzare a schermo */
    private List<Tesi> mDataList;
    private final VisualizeThesisViewModel thesisViewModel;

    private final TesiListViewModel tesiListViewModel;

    public ClassificaTesiAdapter(Context context, VisualizeThesisViewModel model, TesiListViewModel tesiListViewModel) {
        mContext = context;
        mDataList = new ArrayList<>();
        thesisViewModel = model;
        this.tesiListViewModel = tesiListViewModel;
    }

    /**
     * Metodo per il recupero della dimensione della lista di tesi
     *
     * @return
     */
    @Override
    public int getCount() {
        return mDataList.size();
    }

    /**
     * Metodo per il recupero della posizione di una specifica tesi nel mDataList
     *
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    /**
     * Metodo per il recupero del numero della posizione della tesi
     *
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Metodo "getView" per la visualizzazione del layout relativo a classifica tesi, in cui
     * oltre alla visualizzazione della lista di tesi della classifica è possibile effettuare
     * alcune operazioni tramite degli imageButton, ovvero:
     * <p>
     * 1. VISUALIZZAZIONE: l'utente ha la possibilità di visualizzare la tesi selezionata
     * con tutti i suoi dettagli
     * 2. ELIMINAZIONE   : l'utente ha la possibilità di rimuovere la tesi selezionata
     * dalla classifica
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /* Creazione della viewHolder responsabile della gestione della visualizzazione del layout */
        ViewHolder viewHolder;
        /* Recupero degli elementi del layout classifica tesi */
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.classifica_tesi, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView1 = convertView.findViewById(R.id.nometesi2);
            viewHolder.textView2 = convertView.findViewById(R.id.descrizione_tesi);
            viewHolder.imageButton1 = convertView.findViewById(R.id.visualizza_Tesi_classifica);
            viewHolder.imageButton2 = convertView.findViewById(R.id.deleteTesi);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Tesi tesi = mDataList.get(position);
        /* Recupero dei dati relativi al nome tesi e descrizione */
        if (tesi != null && tesi.getNomeTesi() != null && tesi.getDescrizione() != null) {
            viewHolder.textView1.setText(tesi.getNomeTesi());
            viewHolder.textView2.setText(tesi.getDescrizione());
        }
        /* imabeButton per permettere la visualizzazione a dettaglio della tesi selezionata */
        viewHolder.imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thesisViewModel.getThesis().setValue(tesi);
            }
        });

        viewHolder.imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Tesi tesi = mDataList.get(position);
                mDataList.remove(tesi);
                tesiListViewModel.removeTesiFromTesiList(tesi);
            }
        });

        //make draggable view
        convertView.setLongClickable(true);

        return convertView;
    }


    /**
     * Metodo per istanziare gli elementi del layout
     */
    private static class ViewHolder {
        TextView textView1;
        TextView textView2;
        ImageButton imageButton1;
        ImageButton imageButton2;
    }

    public void addTheses(List<Tesi> tesi) {
        mDataList.clear();
        mDataList.addAll(tesi);
        notifyDataSetChanged();
    }

    public void insertItem(int position, Tesi tesi) {
        mDataList.add(position, tesi);
        notifyDataSetChanged();
    }

    public void removeItem(Tesi tesi) {
        mDataList.remove(tesi);
        notifyDataSetChanged();
    }

    /**
     * Metodo per l'aggiornamento della classifica tesi
     *
     * @param mDataList
     */
    public void setmDataList(List<Tesi> mDataList) {
        this.mDataList = mDataList;
        notifyDataSetChanged();
    }

    public List<Tesi> getmDataList() {
        return mDataList;
    }
}

