package com.uniba.mobile.cddgl.laureapp.ui.actionBar.notifications.viewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uniba.mobile.cddgl.laureapp.data.model.Message;

public abstract class MessageViewHolder extends RecyclerView.ViewHolder {
    protected TextView messageTextView;
    protected TextView headerTextView;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(Message message);
}
