package com.uniba.mobile.cddgl.laureapp.ui.notifications.impl;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.ui.bookings.BookingViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.bookings.interfaces.BookingItemClickCallback;
import com.uniba.mobile.cddgl.laureapp.ui.notifications.interfaces.NotificationCallbackItem;

public class NotificationBookingItemClickCallback implements NotificationCallbackItem, BookingItemClickCallback {

    private final BookingViewModel bookingViewModel;

    public NotificationBookingItemClickCallback(BookingViewModel bookingViewModel) {
        this.bookingViewModel = bookingViewModel;
    }

    @Override
    public void onBookingClicked(String id) {
        bookingViewModel.init(id);
    }

    @Override
    public void deleteNotification(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notificationsRef = db.collection("notifications");
        notificationsRef.document(id).delete();
    }
}
