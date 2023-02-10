package com.uniba.mobile.cddgl.laureapp.ui.bookings.impl;

import com.uniba.mobile.cddgl.laureapp.ui.bookings.BookingViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.bookings.interfaces.BookingItemClickCallback;
import com.uniba.mobile.cddgl.laureapp.ui.ticket.TicketViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.ticket.interfaces.TicketItemClickCallback;

public class BookingItemClickCallbackImpl implements BookingItemClickCallback {

    private final BookingViewModel bookingViewModel;

    public BookingItemClickCallbackImpl(BookingViewModel bookingViewModel) {
        this.bookingViewModel = bookingViewModel;
    }

    @Override
    public void onBookingClicked(String id) {
        bookingViewModel.init(id);
    }
}
