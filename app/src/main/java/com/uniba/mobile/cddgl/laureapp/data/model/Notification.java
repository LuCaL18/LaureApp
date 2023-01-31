package com.uniba.mobile.cddgl.laureapp.data.model;

import androidx.annotation.Nullable;

import com.uniba.mobile.cddgl.laureapp.data.NotificationType;

public class Notification {

    private String receiveId;
    private String senderName;
    private String body;
    private long timestamp;
    private NotificationType type;

    @Nullable
    private String ticketId;

    @Nullable
    private String chatId;
    @Nullable
    private String nameChat;

    public Notification() {}

    public Notification(String receiveId, String senderName, String body) {
        this.receiveId = receiveId;
        this.senderName = senderName;
        this.body = body;
    }

    public Notification(String receiveId, String senderName, String body, long timestamp, NotificationType type, @Nullable String chatId, @Nullable String nameChat) {
        this.receiveId = receiveId;
        this.senderName = senderName;
        this.body = body;
        this.timestamp = timestamp;
        this.type = type;
        this.chatId = chatId;
        this.nameChat = nameChat;
    }

    public Notification(String receiveId, String senderName, String body, long timestamp, NotificationType type, @Nullable String ticketId) {
        this.receiveId = receiveId;
        this.senderName = senderName;
        this.body = body;
        this.timestamp = timestamp;
        this.type = type;
        this.ticketId = ticketId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Nullable
    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(@Nullable String ticketId) {
        this.ticketId = ticketId;
    }

    @Nullable
    public String getChatId() {
        return chatId;
    }

    public void setChatId(@Nullable String chatId) {
        this.chatId = chatId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId;
    }

    @Nullable
    public String getNameChat() {
        return nameChat;
    }

    public void setNameChat(@Nullable String nameChat) {
        this.nameChat = nameChat;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "receiveId='" + receiveId + '\'' +
                ", senderName='" + senderName + '\'' +
                ", body='" + body + '\'' +
                ", timestamp=" + timestamp +
                ", type=" + type +
                ", ticketId='" + ticketId + '\'' +
                ", chatId='" + chatId + '\'' +
                ", nameChat='" + nameChat + '\'' +
                '}';
    }
}
