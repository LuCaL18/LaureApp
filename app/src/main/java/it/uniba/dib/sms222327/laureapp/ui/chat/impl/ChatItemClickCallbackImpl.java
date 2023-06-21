package it.uniba.dib.sms222327.laureapp.ui.chat.impl;

import it.uniba.dib.sms222327.laureapp.ui.chat.ChatViewModel;
import it.uniba.dib.sms222327.laureapp.ui.chat.interfaces.ChatItemClickCallback;

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
