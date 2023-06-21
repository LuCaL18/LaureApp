package com.uniba.mobile.cddgl.laureapp.ui.bookings.impl;

import com.uniba.mobile.cddgl.laureapp.ui.bookings.BookingViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.bookings.interfaces.BookingItemClickCallback;

/**
 * Implementazione dell'interfaccia BookingItemClickCallback
 */
public class BookingItemClickCallbackImpl implements BookingItemClickCallback {

    private final BookingViewModel bookingViewModel;

    /**
     * costruttore in cui è passato il model della prenotazione
     * @param bookingViewModel
     */
    public BookingItemClickCallbackImpl(BookingViewModel bookingViewModel) {
        this.bookingViewModel = bookingViewModel;
    }

    /**
     * Metodo che gestisce il click su una prenotazione. Avviene l'inizializzazione del model
     * @param id
     */
    @Override
    public void onBookingClicked(String id) {
        bookingViewModel.init(id);
    }
}
