package com.uniba.mobile.cddgl.laureapp.ui.chat.viewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.uniba.mobile.cddgl.laureapp.data.model.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    protected TextView messageTextView;
    protected TextView timestampTextView;
    protected TextView headerTextView;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(Message message, @Nullable String displayName, @Nullable String photoProfile) {
        messageTextView.setText(message.getMessage());

        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        timestampTextView.setText(timeFormat.format(message.getTimestamp()));

        if (headerTextView != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = new Date(message.getTimestamp());
            headerTextView.setText(formatter.format(date));
        }
    }
}
