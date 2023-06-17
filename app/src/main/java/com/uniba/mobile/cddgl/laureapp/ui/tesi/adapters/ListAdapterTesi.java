package com.uniba.mobile.cddgl.laureapp.ui.tesi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.interfaces.FavouriteItemCallback;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.viewModels.VisualizeThesisViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.viewHolder.TesiListViewHolder;

import java.util.List;

/**
 * Adapter che funge da complementare a ListaTaskFragment per la
 * visualizzazione della lista dei task visibili all'utente
 */

public class ListAdapterTesi extends BaseAdapter {

    /* Contesto dello stato della lista di tesi */
    private final LoggedInUser user;
    private List<Tesi> tesiList;
    private final VisualizeThesisViewModel thesisViewModel;
    private final LayoutInflater inflater;
    private List<String> classficaTesi;
    private final FavouriteItemCallback favouriteItemCallback;

    public ListAdapterTesi(Context context, List<Tesi> tesiList, VisualizeThesisViewModel model, LoggedInUser user, FavouriteItemCallback callback) {
        this.tesiList = tesiList;
        thesisViewModel = model;
        this.user = user;
        favouriteItemCallback = callback;
        inflater = LayoutInflater.from(context);
    }

    /**
     * Metodo per il recupero della dimensione della lista di tesi
     *
     * @return
     */
    @Override
    public int getCount() {
        return tesiList.size();
    }

    /**
     * Metodo per il recupero della posizione di una specifica tesi nel mDataList
     *
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return tesiList.get(position);
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
     * Metodo "getView" per la visualizzazione della lista di tutte le tesi disponibili in base
     * al ruolo dello studente loggato sarà possibile effettuare le seguenti operazioni
     * <p>
     * 1. Scorrimento della lista di tutte le tesi
     * 1.1 Se l'utente loggato è un professore, potrà vedere la lista di tutte le tesi
     * di cui è relatore o corelatore
     * 1.2 Se l'utente loggato è uno studente, potrà vedere la lista di tutte le tesi
     * non ancora assegnate ad uno studente
     * 2. Condivisione dei dati di una tesi
     * 3. Visualizzazione dei dati di una tesi
     * 4. Aggiunta di una tesi all'interno di una propria classifica personale
     * (valida solamente per lo studente)
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TesiListViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.lista_tesi, parent, false);
            viewHolder = new TesiListViewHolder(convertView, favouriteItemCallback);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TesiListViewHolder) convertView.getTag();
        }

        Tesi tesi = (Tesi) getItem(position);
        if(classficaTesi != null && classficaTesi.contains(tesi.getId())) {
            viewHolder.bindData(tesi, thesisViewModel, user, true);
        } else {
            viewHolder.bindData(tesi, thesisViewModel, user, false);
        }


        return convertView;
    }

    public List<Tesi> getTesiList() {
        return tesiList;
    }

    public void setTesiList(List<Tesi> tesiList) {
        this.tesiList = tesiList;
        notifyDataSetChanged();
    }

    public List<String> getClassficaTesi() {
        return classficaTesi;
    }

    public void setClassficaTesi(List<String> classficaTesi) {
        this.classficaTesi = classficaTesi;
        notifyDataSetChanged();
    }
}


