package it.uniba.dib.sms222327.laureapp.ui.notifications.impl;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import it.uniba.dib.sms222327.laureapp.ui.chat.ChatViewModel;
import it.uniba.dib.sms222327.laureapp.ui.chat.interfaces.ChatItemClickCallback;
import it.uniba.dib.sms222327.laureapp.ui.notifications.interfaces.NotificationCallbackItem;

/**
 * Implementazione dell'interfaccia NotificationCallbackItem e ChatItemClickCallback
 */
public class NotificationChatItemClickCallback implements ChatItemClickCallback, NotificationCallbackItem {

    private final ChatViewModel chatViewModel;

    public NotificationChatItemClickCallback(ChatViewModel model) {
        chatViewModel = model;
    }

    @Override
    public void onChatClicked(String chatId) {
        chatViewModel.init(chatId);
    }


    @Override
    public void deleteNotification(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notificationsRef = db.collection("notifications");
        notificationsRef.document(id).delete();
    }
}
