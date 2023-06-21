package com.uniba.mobile.cddgl.laureapp.ui.ticket.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Ticket;
import com.uniba.mobile.cddgl.laureapp.ui.ticket.interfaces.TicketItemClickCallback;
import com.uniba.mobile.cddgl.laureapp.ui.ticket.viewHolder.TicketViewHolder;
/**
 *  La classe TicketAdapter estende FirestoreRecyclerAdapter,
 *  che è una classe di supporto fornita da FirestoreUI per la gestione di un elenco di dati Firestore in un RecyclerView.
 */
public class TicketAdapter extends FirestoreRecyclerAdapter<Ticket, TicketViewHolder> {

    private final TicketItemClickCallback callback;

    public TicketAdapter(@NonNull FirestoreRecyclerOptions<Ticket> options, TicketItemClickCallback callback) {
        super(options);
        this.callback = callback;
    }

    @Override
    public TicketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ticket_list_item, parent, false);
        return new TicketViewHolder(view, callback);
    }

    @Override
    protected void onBindViewHolder(TicketViewHolder holder, int position, @NonNull Ticket model) {
        holder.bind(model);
    }
}
