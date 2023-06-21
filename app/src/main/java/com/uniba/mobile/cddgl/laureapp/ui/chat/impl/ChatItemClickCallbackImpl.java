package com.uniba.mobile.cddgl.laureapp.ui.chat.impl;

import com.uniba.mobile.cddgl.laureapp.ui.chat.ChatViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.chat.interfaces.ChatItemClickCallback;
/**
 * Implementazione dell'interfaccia ChatItemClickCallback
 */

public class ChatItemClickCallbackImpl implements ChatItemClickCallback {

    private final ChatViewModel model;

    public ChatItemClickCallbackImpl(ChatViewModel model) {
        this.model = model;
    }

    @Override
    public void onChatClicked(String chatId) {
        model.init(chatId);
    }
}
