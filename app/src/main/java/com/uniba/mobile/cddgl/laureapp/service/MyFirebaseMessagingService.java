package com.uniba.mobile.cddgl.laureapp.service;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.uniba.mobile.cddgl.laureapp.MainActivity;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Notification;
import com.uniba.mobile.cddgl.laureapp.util.BaseRequestNotification;
import com.uniba.mobile.cddgl.laureapp.data.NotificationType;

import java.util.HashMap;
import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final static String CLASSNAME = "MyFirebaseMessagingService";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        saveTokenUser(token);
    }

    private void saveTokenUser(String token) {
        try {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            Map<String, Object> updates = new HashMap<>();
            updates.put("token", token);

            userRef.updateChildren(updates)
                    .addOnSuccessListener(aVoid -> Log.i(CLASSNAME, "user updates"))
                    .addOnFailureListener(e -> Log.e(CLASSNAME, "Unable updates user"));
        } catch (NullPointerException e) {
            Log.w(CLASSNAME, "Unable update token user");
        }

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // check if the message is message chat to the current user
        if (isMessage(remoteMessage)) {
            createNotificationMessage(remoteMessage);
            return;
        }
        createNotificationReport(remoteMessage);
    }

//    private Notification fetchDataCommonFromMessage(RemoteMessage remoteMessage) {
//        Notification notification = null;
//
//        try {
//            String receiveId = remoteMessage.getData().get(BaseRequestNotification.USER_ID);
//            String title = remoteMessage.getNotification().getTitle();
//            String body = remoteMessage.getNotification().getBody();
//
//            notification = new Notification(receiveId, title, body);
//        } catch (NullPointerException e) {
//            Log.e(CLASSNAME, "Unable fetch data notification to response");
//            return null;
//        }
//
//        try {
//            long timestamp = Long.parseLong(remoteMessage.getData().get("timestamp"));
//            notification.setTimestamp(timestamp);
//        } catch (NumberFormatException e) {
//            Log.e(CLASSNAME, "Unable fetch timestamp notification to response");
//        }
//
//        return notification;
//    }

    private boolean isMessage(RemoteMessage remoteMessage) {
        try {
            NotificationType typeMessage = NotificationType.valueOf(remoteMessage.getData().get(BaseRequestNotification.KEY_TYPE));

            return typeMessage.equals(NotificationType.MESSAGE);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void createNotificationMessage(RemoteMessage remoteMessage) {
    }

    private void createNotificationReport(RemoteMessage remoteMessage) {
    }

    private void createNotification(Notification notification) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class); // Create a notification
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_notifications_none_24dp)
                .setContentTitle(notification.getSenderName())
                .setContentText(notification.getBody())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, builder.build());
    }
}
