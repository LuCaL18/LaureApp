package it.uniba.dib.sms222327.laureapp.ui.notifications.viewHolder;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import it.uniba.dib.sms222327.laureapp.R;
import it.uniba.dib.sms222327.laureapp.data.model.Notification;
import it.uniba.dib.sms222327.laureapp.ui.notifications.impl.NotificationMeetingItemClickCallback;

/**
 * La classe MeetingNotificationViewHolder estende NotificationViewHolder
 * ed è responsabile per la gestione della vista delle notifiche dei ricevimenti
 */
public class MeetingNotificationViewHolder extends NotificationViewHolder {

    private boolean selected;
    private final NotificationMeetingItemClickCallback notificationMeetingItemClickCallback;
    private String meetingId;
    private final Context context;

    public MeetingNotificationViewHolder(@NonNull View itemView, NotificationMeetingItemClickCallback callback) {
        super(itemView);

        context = itemView.getContext();
        this.selected = false;
        this.notificationMeetingItemClickCallback = callback;

        itemView.setOnClickListener(view -> {
//            chatNameTextView.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.item_selected));
            itemView.setSelected(true);
            selected = true;
            notificationMeetingItemClickCallback.onMeetingClicked(meetingId);
        });
    }

    @Override
    public void bind(Notification notification, String idNotification) {
        super.bind(notification, idNotification);

        meetingId = notification.getMeetingId();
        iconImageView.setImageResource(R.drawable.ic_baseline_book_online_24);

        titleTextView.setText("Meeting");

        bodyTextView.setText(notification.getBody());
    }

    @Override
    public void deleteNotification() {
        if(selected) {
            notificationMeetingItemClickCallback.deleteNotification(idNotification);
        }
    }
}
