package com.uniba.mobile.cddgl.laureapp.ui.notifications.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Notification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class NotificationViewHolder extends RecyclerView.ViewHolder {

    protected TextView titleTextView;
    protected TextView bodyTextView;
    protected TextView timestampTextView;
    protected ImageView iconImageView;
    protected String idNotification;

    public NotificationViewHolder(@NonNull View itemView) {
        super(itemView);

        titleTextView = itemView.findViewById(R.id.title_notification);
        bodyTextView = itemView.findViewById(R.id.body_notification);
        timestampTextView = itemView.findViewById(R.id.text_timestamp_notification);
        iconImageView = itemView.findViewById(R.id.image_item_notifications);
    }

    public void bind(Notification notification, String idNotification) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date date = new Date(notification.getTimestamp());
        timestampTextView.setText(formatter.format(date));

        this.idNotification = idNotification;
    }

    public abstract void deleteNotification();
}
