package com.uniba.mobile.cddgl.laureapp.ui.tesi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.viewModels.VisualizeThesisViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.viewHolder.ClassificaTesiViewHolder;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.viewModels.TesiListViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter che funge da complementare a ListaTaskFragment per la
 * visualizzazione della lista dei task visibili all'utente
 */

public class ClassificaTesiAdapter extends BaseAdapter {

    private final Context mContext;
    private final VisualizeThesisViewModel thesisViewModel;
    private final TesiListViewModel tesiListViewModel;
    private final List<Tesi> mDataList;

    public ClassificaTesiAdapter(Context context, VisualizeThesisViewModel model, TesiListViewModel tesiListViewModel) {
        mContext = context;
        thesisViewModel = model;
        this.tesiListViewModel = tesiListViewModel;
        mDataList = new ArrayList<>();
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
        ClassificaTesiViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.classifica_tesi, parent, false);
            viewHolder = new ClassificaTesiViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ClassificaTesiViewHolder) convertView.getTag();
        }

        viewHolder.bind(mDataList.get(position), thesisViewModel, tesiListViewModel);

        //make draggable view
        convertView.setLongClickable(true);

        return convertView;
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

    public void setmDataList(List<Tesi> mDataList) {
        this.mDataList.clear();
        this.mDataList.addAll(mDataList);
        notifyDataSetChanged();
    }

    public List<Tesi> getmDataList() {
        return mDataList;
    }
}

