package com.uniba.mobile.cddgl.laureapp.ui.chat.viewHolder;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.ChatData;
import com.uniba.mobile.cddgl.laureapp.ui.chat.interfaces.ChatItemClickCallback;

public class ChatViewHolder extends RecyclerView.ViewHolder {
    private TextView chatNameTextView;
    private View divider;
    private ChatItemClickCallback callback;

    @Nullable
    private String chatId;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setVisibility(View.GONE);
    }

    public ChatViewHolder(@NonNull View itemView, ChatItemClickCallback callbackItem) {
        super(itemView);

        itemView.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.selector_item_background));
        chatNameTextView = itemView.findViewById(R.id.chat_name);
        divider = itemView.findViewById(R.id.divider);
        this.callback = callbackItem;

        itemView.setOnClickListener(view -> {
//            chatNameTextView.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.item_selected));
            itemView.setSelected(true);
            callback.onChatClicked(chatId);
        });
    }

    public void bind(ChatData chat) {
        chatId = chat.getId();
        chatNameTextView.setText(chat.getName());
    }
}
