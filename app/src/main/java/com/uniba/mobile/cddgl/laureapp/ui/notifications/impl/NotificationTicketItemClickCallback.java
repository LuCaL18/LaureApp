package com.uniba.mobile.cddgl.laureapp.ui.notifications.impl;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.ui.notifications.interfaces.NotificationCallbackItem;
import com.uniba.mobile.cddgl.laureapp.ui.ticket.TicketViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.ticket.interfaces.TicketItemClickCallback;
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
