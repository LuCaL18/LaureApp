package com.uniba.mobile.cddgl.laureapp.ui.chat.viewHolder;

import android.view.View;

import androidx.annotation.NonNull;

import com.uniba.mobile.cddgl.laureapp.R;

/**
 * La classe ReceiveMessageHolder estende MessageViewHolder.
 * Si occupa di mostrare i messaggi inviati in una chat
 */
public class SendMessageViewHolder extends MessageViewHolder {

    public SendMessageViewHolder(@NonNull View itemView) {
        super(itemView);

        messageTextView = itemView.findViewById(R.id.message_text_view);
        timestampTextView = itemView.findViewById(R.id.text_timestamp_me);
    }

    public SendMessageViewHolder(@NonNull View itemView, boolean isFirstOfDay) {
        super(itemView);
        messageTextView = itemView.findViewById(R.id.message_text_view);
        timestampTextView = itemView.findViewById(R.id.text_timestamp_me);

        if (isFirstOfDay) {
            headerTextView = itemView.findViewById(R.id.text_gchat_date_me);
            headerTextView.setVisibility(View.VISIBLE);
        }

    }
}
