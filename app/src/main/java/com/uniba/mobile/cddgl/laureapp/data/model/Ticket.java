package com.uniba.mobile.cddgl.laureapp.data.model;

import androidx.annotation.Nullable;

import com.uniba.mobile.cddgl.laureapp.data.TicketState;

public class Ticket {

    private String idSender;
    private String idReceiver;
    private String nameTesi;
    private String idTesi;
    private TicketState state;
    private String id;

    @Nullable
    private long timestampSender;
    @Nullable
    private String textSender;

    @Nullable
    private String textReceiver;
    @Nullable
    private Long timestampReceiver;

    public Ticket() {
    }

    public Ticket(String id, String idSender, String idReceiver, String nameTesi, String idTesi, TicketState state) {
        this.idSender = idSender;
        this.idReceiver = idReceiver;
        this.nameTesi = nameTesi;
        this.idTesi = idTesi;
        this.state = state;
        this.id = id;
    }

    public Ticket(String idSender, String idReceiver, String nameTesi, String idTesi, TicketState state, String id, long timestampSender, @Nullable String textSender, @Nullable String textReceiver, long timestampReceiver) {
        this.idSender = idSender;
        this.idReceiver = idReceiver;
        this.nameTesi = nameTesi;
        this.idTesi = idTesi;
        this.state = state;
        this.id = id;
        this.timestampSender = timestampSender;
        this.textSender = textSender;
        this.textReceiver = textReceiver;
        this.timestampReceiver = timestampReceiver;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(String idReceiver) {
        this.idReceiver = idReceiver;
    }

    public String getNameTesi() {
        return nameTesi;
    }

    public void setNameTesi(String nameTesi) {
        this.nameTesi = nameTesi;
    }

    public String getIdTesi() {
        return idTesi;
    }

    public void setIdTesi(String idTesi) {
        this.idTesi = idTesi;
    }

    public TicketState getState() {
        return state;
    }

    public void setState(TicketState state) {
        this.state = state;
    }

    public String getTextSender() {
        return textSender;
    }

    public void setTextSender(String textSender) {
        this.textSender = textSender;
    }

    public String getTextReceiver() {
        return textReceiver;
    }

    public void setTextReceiver(String textReceiver) {
        this.textReceiver = textReceiver;
    }

    public long getTimestampSender() {
        return timestampSender;
    }

    public void setTimestampSender(long timestampSender) {
        this.timestampSender = timestampSender;
    }

    public long getTimestampReceiver() {
        return timestampReceiver;
    }

    public void setTimestampReceiver(@Nullable Long timestampReceiver) {
        this.timestampReceiver = timestampReceiver;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "idSender='" + idSender + '\'' +
                ", idReceiver='" + idReceiver + '\'' +
                ", nameTesi='" + nameTesi + '\'' +
                ", idTesi='" + idTesi + '\'' +
                ", state=" + state +
                ", id='" + id + '\'' +
                ", timestampSender=" + timestampSender +
                ", textSender='" + textSender + '\'' +
                ", textReceiver='" + textReceiver + '\'' +
                ", timestampReceiver=" + timestampReceiver +
                '}';
    }
}
