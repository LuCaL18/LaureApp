package com.uniba.mobile.cddgl.laureapp.ui.bookings.viewHolder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.BookingState;
import com.uniba.mobile.cddgl.laureapp.data.model.Booking;
import com.uniba.mobile.cddgl.laureapp.ui.bookings.interfaces.BookingItemClickCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * La classe BookingViewHolder estende RecyclerView.ViewHolder
 * ed è responsabile per la gestione delle viste degli elementi all'interno di un RecyclerView.
 */
public class BookingViewHolder extends RecyclerView.ViewHolder {
    private final TextView titleView;
    private final TextView bodyView;
    private final TextView timestamp;
    private String idBooking;
    private final Context context;

    public BookingViewHolder(@NonNull View itemView, BookingItemClickCallback callback) {
        super(itemView);

        // Ottieni il context
        context = itemView.getContext();

        titleView = itemView.findViewById(R.id.booking_thesis_name);
        bodyView = itemView.findViewById(R.id.booking_thesis_body);
        timestamp = itemView.findViewById(R.id.text_timestamp_booking);

        // Imposta un listener per il clic sulla view dell'elemento
        itemView.setOnClickListener(view -> {
            // Imposta la view selezionata come true
            itemView.setSelected(true);
            // Richiama il callback passando l'id della prenotazione
            callback.onBookingClicked(idBooking);
        });
    }

    public void bind(Booking booking) {
        // Associa i dati della prenotazione al ViewHolder

        this.idBooking = booking.getId();
        String bodyText;

        // Imposta il testo del titolo con il nome della tesi e l'id della tesi
        titleView.setText(booking.getNameThesis() + " (" + booking.getIdThesis() + ")");

        try {
            // Verifica lo stato della prenotazione e imposta il testo del body di conseguenza
            if (booking.getState().equals(BookingState.OPEN)) {
                bodyText = context.getString(R.string.notification_body_open_booking, booking.getNameStudent() + " " + booking.getSurnameStudent(), booking.getNameThesis());
            } else if (booking.getState().equals(BookingState.ACCEPTED)) {
                bodyText = context.getString(R.string.booking_accepted_by_professor);
            } else {
                bodyText = context.getString(R.string.booking_refused_by_professor);
            }

            // Imposta il testo del body
            bodyView.setText(bodyText);
        } catch (Exception e) {
            // In caso di eccezione, nascondi la vista del body
            bodyView.setVisibility(View.GONE);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date date = new Date(booking.getTimestamp());

        // Imposta il testo del timestamp con la data formattata
        timestamp.setText(formatter.format(date));
    }
}

