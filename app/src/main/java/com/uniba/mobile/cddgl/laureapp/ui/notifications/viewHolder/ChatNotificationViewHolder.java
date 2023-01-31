package com.uniba.mobile.cddgl.laureapp.ui.notifications.viewHolder;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Notification;
import com.uniba.mobile.cddgl.laureapp.ui.notifications.impl.NotificationChatItemClickCallback;

public class ChatNotificationViewHolder extends NotificationViewHolder{

    private final NotificationChatItemClickCallback chatItemClickCallback;
    private String chatId;
    private final Context context;
    private boolean selected = false;

    public ChatNotificationViewHolder(@NonNull View itemView, NotificationChatItemClickCallback callback) {
        super(itemView);

        context = itemView.getContext();
        chatItemClickCallback = callback;

        itemView.setOnClickListener(view -> {
//            chatNameTextView.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.item_selected));
            itemView.setSelected(true);
            selected = true;
            chatItemClickCallback.onChatClicked(chatId);
        });
    }

    @Override
    public void bind(Notification notification, String idNotification) {
        super.bind(notification, idNotification);
        chatId = notification.getChatId();

        iconImageView.setImageResource(R.mipmap.ic_messages);

        if(notification.getBody().equals("body_messages")) {
            titleTextView.setText(context.getString(R.string.title_messages, notification.getNameChat()));
            bodyTextView.setText(context.getString(R.string.body_messages, notification.getNameChat()));

            return;
        }

        titleTextView.setText(context.getString(R.string.title_message, notification.getSenderName()));
        bodyTextView.setText(notification.getBody());
    }

    @Override
    public void deleteNotification() {
        if(selected) {
            chatItemClickCallback.deleteNotification(idNotification);
        }
    }
}
