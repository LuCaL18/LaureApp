package it.uniba.dib.sms222327.laureapp.ui.ticket.viewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms222327.laureapp.R;
import it.uniba.dib.sms222327.laureapp.data.TicketState;
import it.uniba.dib.sms222327.laureapp.data.model.Ticket;
import it.uniba.dib.sms222327.laureapp.ui.ticket.interfaces.TicketItemClickCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * La classe TicketViewHolder estende RecyclerView.ViewHolder
 * ed è responsabile per la gestione delle viste degli elementi all'interno di un RecyclerView.
 */
public class TicketViewHolder extends RecyclerView.ViewHolder {
    private final TextView titleView;
    private final TextView bodyView;
    private final TextView timestamp;
    private String idTicket;

    public TicketViewHolder(View itemView, TicketItemClickCallback callback) {
        super(itemView);
        titleView = itemView.findViewById(R.id.ticket_tesi_name);
        bodyView = itemView.findViewById(R.id.ticket_tesi_body);
        timestamp = itemView.findViewById(R.id.text_timestamp_ticket);

        itemView.setOnClickListener(view -> {
            itemView.setSelected(true);
            callback.onTicketClicked(idTicket);
        });
    }

    public void bind(Ticket ticket) {

        this.idTicket = ticket.getId();
        String bodyText;

        titleView.setText(ticket.getNameTesi());
        try {
            if(ticket.getState().equals(TicketState.OPEN)) {
                bodyText = ticket.getTextSender().length() > 50 ? ticket.getTextSender().substring(0, 49) + "..." : ticket.getTextSender();
            } else {
                bodyText = ticket.getTextReceiver().length() > 50 ? ticket.getTextReceiver().substring(0, 49) + "..." : ticket.getTextReceiver();
            }

            bodyView.setText(bodyText);
        } catch (Exception e) {
            bodyView.setVisibility(View.GONE);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date date;

        if(ticket.getState().equals(TicketState.OPEN)) {
            date = new Date(ticket.getTimestampSender());
        } else {
            date = new Date(ticket.getTimestampReceiver());
        }

        timestamp.setText(formatter.format(date));
    }
}
