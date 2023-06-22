package it.uniba.dib.sms222327.laureapp.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * La classe MyFirebaseMessagingService estende FirebaseMessagingService,
 * che è una classe di base fornita da Firebase per gestire la ricezione dei messaggi push.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final static String CLASSNAME = "MyFirebaseMessagingService";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        saveTokenUser(token); // Salvataggio del token dell'utente quando viene generato un nuovo token
    }

    private void saveTokenUser(String token) {
        try {
            // Ottieni il riferimento al documento dell'utente corrente nel database Firestore
            DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

            // Crea una mappa con l'aggiornamento da effettuare (aggiornare il campo "token" con il nuovo token)
            Map<String, Object> updates = new HashMap<>();
            updates.put("token", token);

            // Esegui l'aggiornamento del documento dell'utente nel database Firestore
            userRef.update(updates)
                    .addOnSuccessListener(aVoid -> Log.i(CLASSNAME, "user updates")) // Aggiornamento avvenuto con successo
                    .addOnFailureListener(e -> Log.e(CLASSNAME, "Unable updates user")); // Errore durante l'aggiornamento
        } catch (NullPointerException e) {
            Log.w(CLASSNAME, "Unable update token user"); // Gestione dell'eccezione nel caso in cui l'utente corrente sia nullo
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//        if (isMessage(remoteMessage)) {
//            createNotificationMessage(remoteMessage);
//            return;
//        }
//        createNotificationReport(remoteMessage);
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
//
//    private boolean isMessage(RemoteMessage remoteMessage) {
//        try {
//            NotificationType typeMessage = NotificationType.valueOf(remoteMessage.getData().get(BaseRequestNotification.KEY_TYPE));
//
//            return typeMessage.equals(NotificationType.MESSAGE);
//        } catch (IllegalArgumentException e) {
//            return false;
//        }
//    }
//
//    private void createNotificationMessage(RemoteMessage remoteMessage) {
//        //TODO: implements this method
//    }
//
//    private void createNotificationReport(RemoteMessage remoteMessage) {
//        //TODO: implements this method
//    }
//
//    private void createNotification(Notification notification) {
//        // Create an explicit intent for an Activity in your app
//        Intent intent = new Intent(this, MainActivity.class); // Create a notification
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
//                .setSmallIcon(R.drawable.ic_notifications_none_24dp)
//                .setContentTitle(notification.getSenderName())
//                .setContentText(notification.getBody())
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.notify(0, builder.build());
//    }
//
//    private void showDownloadNotification(String fileName) {
//
//        Intent intent = new Intent(this, MainActivity.class); // Create a notification
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "download_channel")
//                .setSmallIcon(R.drawable.ic_baseline_insert_drive_file_24)
//                .setContentTitle("File Downloaded")
//                .setContentText(fileName + " has been successfully downloaded")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//
//        // Create a channel for the notification
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "Download Channel";
//            String description = "Notifications for file downloads";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel("download_channel", name, importance);
//            channel.setDescription(description);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        notificationManager.notify(0, builder.build());
//    }
}
