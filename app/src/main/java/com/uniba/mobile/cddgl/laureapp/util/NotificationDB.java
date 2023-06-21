package com.uniba.mobile.cddgl.laureapp.util;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.data.NotificationType;
import com.uniba.mobile.cddgl.laureapp.data.model.Notification;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe per il salvatagio delle notifiche su Firestore
 */
public class NotificationDB {

    private final CollectionReference collectionReference;

    public NotificationDB() {
        this.collectionReference = FirebaseFirestore.getInstance().collection("notifications");
    }


    public void addNotification(JSONObject notificationJSON) {
        try {
            Notification notification = parserJSONNotification(notificationJSON.get("data").toString());

            if (notification.getType().equals(NotificationType.MESSAGE)) {
                addMessage(notification);
                return;
            }

            addTicket(notification);
        } catch (JSONException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void addMessage(Notification notification) {
        collectionReference.whereEqualTo("receiveId", notification.getReceiveId())
                .whereEqualTo("chatId", notification.getChatId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot.isEmpty()) {
                                // no object with the same receiveId and chatId, add new object
                                collectionReference.add(notification);
                            } else {
                                // object with the same receiveId and chatId exists, update message field
                                for (QueryDocumentSnapshot document : querySnapshot) {

                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("senderName", notification.getSenderName());
                                    updates.put("body", "body_messages");
                                    updates.put("timestamp", notification.getTimestamp());

                                    collectionReference.document(document.getId()).update(updates);
                                }
                            }
                        }
                    }
                });
    }

    private void addTicket(Notification notification) {
        collectionReference.add(notification);
    }

    private Notification parserJSONNotification(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Notification.class);
    }
}
