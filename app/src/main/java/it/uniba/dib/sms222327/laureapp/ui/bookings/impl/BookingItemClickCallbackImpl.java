package it.uniba.dib.sms222327.laureapp.ui.bookings.impl;

import it.uniba.dib.sms222327.laureapp.ui.bookings.BookingViewModel;
import it.uniba.dib.sms222327.laureapp.ui.bookings.interfaces.BookingItemClickCallback;

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
