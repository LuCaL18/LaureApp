package it.uniba.dib.sms222327.laureapp.ui.notifications.impl;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import it.uniba.dib.sms222327.laureapp.ui.calendario.viewModels.MeetingViewModel;
import it.uniba.dib.sms222327.laureapp.ui.calendario.interfaces.MeetingItemClickCallback;
import it.uniba.dib.sms222327.laureapp.ui.notifications.interfaces.NotificationCallbackItem;
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
