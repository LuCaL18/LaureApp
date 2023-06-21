package com.uniba.mobile.cddgl.laureapp.ui.notifications.impl;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.ui.calendario.viewModels.MeetingViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.calendario.interfaces.MeetingItemClickCallback;
import com.uniba.mobile.cddgl.laureapp.ui.notifications.interfaces.NotificationCallbackItem;
/**
 * Implementazione dell'interfaccia NotificationCallbackItem e MeetingItemClickCallback
 */
public class NotificationMeetingItemClickCallback implements NotificationCallbackItem, MeetingItemClickCallback {

    private final MeetingViewModel meetingViewModel;

    public NotificationMeetingItemClickCallback(MeetingViewModel meetingViewModel) {
        this.meetingViewModel = meetingViewModel;
    }

    @Override
    public void onMeetingClicked(String id) {
        meetingViewModel.init(id);
    }

    @Override
    public void deleteNotification(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notificationsRef = db.collection("notifications");
        notificationsRef.document(id).delete();
    }
}
