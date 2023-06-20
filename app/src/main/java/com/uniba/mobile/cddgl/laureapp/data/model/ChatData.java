package com.uniba.mobile.cddgl.laureapp.data.model;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Classe che rapppresenta l'istanza dei dati della chat
 */
public class ChatData implements Serializable {

    private String id; // Identificatore della chat
    private String name; // Nome della chat
    private List<String> members; // Lista dei membri della chat

    @Keep
    public ChatData() {
        // Costruttore vuoto necessario per la serializzazione/deserializzazione
    }

    public ChatData(String id, List<String> members, String name) {
        this.id = id;
        this.members = members;
        this.name = name;
    }

    // Metodi getter e setter per i campi
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers( List<String> members) {
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatData chatData = (ChatData) o;
        return id.equals(chatData.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ChatData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", members=" + members +
                '}';
    }
}


