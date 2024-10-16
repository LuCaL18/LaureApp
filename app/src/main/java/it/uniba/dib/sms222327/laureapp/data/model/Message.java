package it.uniba.dib.sms222327.laureapp.data.model;

import androidx.annotation.Keep;

/**
 * Classe istanza del messaggio inviato in una chat
 */
public class Message {
    private String senderId;
    private String message;
    private long timestamp;

    @Keep
    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }

    public Message(String senderId, String message) {
        this.senderId = senderId;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

