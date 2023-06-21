package it.uniba.dib.sms222327.laureapp.ui.chat.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import it.uniba.dib.sms222327.laureapp.R;
import it.uniba.dib.sms222327.laureapp.data.model.Message;

/**
 * La classe ReceiveMessageHolder estende MessageViewHolder.
 * Si occupa di mostrare i messaggi ricevuti in una chat
 */
public class ReceiveMessageHolder extends MessageViewHolder {

    private final TextView displayNameTextView;
    private final ImageView userPhotoImageView;

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
    public void bind(Message message, @NonNull String displayName, @Nullable String photoProfile) {
        super.bind(message, displayName, photoProfile);
        displayNameTextView.setText(displayName);

        if(photoProfile != null) {
            Glide.with(itemView).load(photoProfile).transform(new CircleCrop()).into(userPhotoImageView);
        }
    }
}
