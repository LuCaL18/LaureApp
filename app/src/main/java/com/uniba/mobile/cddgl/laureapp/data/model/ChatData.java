package com.uniba.mobile.cddgl.laureapp.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ChatData {

    private String id;
    private String name;
    private HashMap<String, Boolean> members;

    public ChatData() {
    }

    public ChatData(String id, HashMap<String, Boolean> members, String name) {
        this.id = id;
        this.members = members;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public  HashMap<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers( HashMap<String, Boolean> members) {
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


