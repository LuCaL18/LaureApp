package it.uniba.dib.sms222327.laureapp.ui.notifications.impl;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import it.uniba.dib.sms222327.laureapp.ui.notifications.interfaces.NotificationCallbackItem;
import it.uniba.dib.sms222327.laureapp.ui.ticket.TicketViewModel;
import it.uniba.dib.sms222327.laureapp.ui.ticket.interfaces.TicketItemClickCallback;

/**
 * Implementazione dell'interfaccia NotificationCallbackItem e TicketItemClickCallback
 */
public class NotificationTicketItemClickCallback implements NotificationCallbackItem, TicketItemClickCallback {

    private final TicketViewModel model;

    public NotificationTicketItemClickCallback(TicketViewModel model) {
        this.model = model;
    }

    @Override
    public void deleteNotification(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notificationsRef = db.collection("notifications");
        notificationsRef.document(id).delete();
    }

    @Override
    public void onTicketClicked(String id) {
        model.init(id);
    }
}
