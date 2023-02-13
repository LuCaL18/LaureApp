package com.uniba.mobile.cddgl.laureapp.ui.notifications.viewHolder;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Notification;
import com.uniba.mobile.cddgl.laureapp.ui.notifications.impl.NotificationTicketItemClickCallback;

public class TicketNotificationViewHolder extends NotificationViewHolder{

    private boolean selected;
    private final NotificationTicketItemClickCallback notificationTicketItemClickCallback;
    private String ticketId;
    private final Context context;

    public TicketNotificationViewHolder(@NonNull View itemView, NotificationTicketItemClickCallback callback) {
        super(itemView);

        context = itemView.getContext();
        this.selected = false;
        this.notificationTicketItemClickCallback = callback;

        itemView.setOnClickListener(view -> {
//            chatNameTextView.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.item_selected));
            itemView.setSelected(true);
            selected = true;
            notificationTicketItemClickCallback.onTicketClicked(ticketId);
        });
    }

    @Override
    public void bind(Notification notification, String idNotification) {
        super.bind(notification, idNotification);

        ticketId = notification.getTicketId();
        iconImageView.setImageResource(R.mipmap.ic_ticket);

        titleTextView.setText(context.getString(R.string.title_notification_ticket, notification.getNameChat()));

        if(notification.getBody().equals("open_ticket")) {
            bodyTextView.setText(context.getString(R.string.notification_body_open_ticket, notification.getSenderName(), notification.getNameChat()));
        } else {
            bodyTextView.setText(context.getString(R.string.notification_body_closed_ticket, notification.getSenderName(), notification.getNameChat()));
        }

    }

    @Override
    public void deleteNotification() {
        if(selected) {
            notificationTicketItemClickCallback.deleteNotification(idNotification);
        }
    }
}
