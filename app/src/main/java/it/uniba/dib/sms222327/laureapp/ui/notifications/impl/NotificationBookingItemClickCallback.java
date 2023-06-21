package it.uniba.dib.sms222327.laureapp.ui.notifications.impl;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import it.uniba.dib.sms222327.laureapp.ui.bookings.BookingViewModel;
import it.uniba.dib.sms222327.laureapp.ui.bookings.interfaces.BookingItemClickCallback;
import it.uniba.dib.sms222327.laureapp.ui.notifications.interfaces.NotificationCallbackItem;

/**
 * Implementazione dell'interfaccia NotificationCallbackItem e BookingItemClickCallback
 */
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
