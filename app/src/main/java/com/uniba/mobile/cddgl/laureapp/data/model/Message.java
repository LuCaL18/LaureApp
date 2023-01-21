package com.uniba.mobile.cddgl.laureapp.data.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Message {
    private String senderId;
    private String message;
    private long timestamp;

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

