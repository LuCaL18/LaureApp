package com.uniba.mobile.cddgl.laureapp.ui.chat.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Message;

public class ReceiveMessageHolder extends MessageViewHolder {

    private TextView displayNameTextView;
    private ImageView userPhotoImageView;

    public ReceiveMessageHolder(@NonNull View itemView) {
        super(itemView);
        messageTextView = itemView.findViewById(R.id.text_gchat_message_other);
        timestampTextView = itemView.findViewById(R.id.text_timestamp_other);

        displayNameTextView = itemView.findViewById(R.id.text_gchat_user_other);
        userPhotoImageView = itemView.findViewById(R.id.image_gchat_profile_other);
    }

    public ReceiveMessageHolder(@NonNull View itemView, boolean isFirstOfDay) {
        super(itemView);
        messageTextView = itemView.findViewById(R.id.text_gchat_message_other);
        timestampTextView = itemView.findViewById(R.id.text_timestamp_other);

        displayNameTextView = itemView.findViewById(R.id.text_gchat_user_other);
        userPhotoImageView = itemView.findViewById(R.id.image_gchat_profile_other);

        if (isFirstOfDay) {
            headerTextView = itemView.findViewById(R.id.text_gchat_date_other);
        }

    }

    @Override
    public void bind(Message message, @Nullable String displayName, @Nullable String photoProfile) {
        super.bind(message, displayName, photoProfile);
        displayNameTextView.setText(displayName);
    }
}
