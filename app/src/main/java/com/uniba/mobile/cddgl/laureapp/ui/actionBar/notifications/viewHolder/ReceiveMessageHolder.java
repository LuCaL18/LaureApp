package com.uniba.mobile.cddgl.laureapp.ui.actionBar.notifications.viewHolder;

import android.view.View;

import androidx.annotation.NonNull;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReceiveMessageHolder extends MessageViewHolder {
    public ReceiveMessageHolder(@NonNull View itemView) {
        super(itemView);
        messageTextView = itemView.findViewById(R.id.text_gchat_message_other);
    }

    public ReceiveMessageHolder(@NonNull View itemView, boolean isFirstOfDay) {
        super(itemView);
        messageTextView = itemView.findViewById(R.id.text_gchat_message_other);

        if(isFirstOfDay) {
            headerTextView = itemView.findViewById(R.id.text_gchat_date_other);
        }

    }

    @Override
    public void bind(Message message) {
        messageTextView.setText(message.getMessage());

        if(headerTextView != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date(message.getTimestamp());
            headerTextView.setText(formatter.format(date));
        }
    }
}
