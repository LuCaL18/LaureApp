package com.uniba.mobile.cddgl.laureapp.data.model;

import java.util.ArrayList;

public class ChatData {

    private String id;
    private String name;
    private ArrayList<String> members;

    public ChatData() {
    }

    public ChatData(String id, ArrayList<String> members, String name) {
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

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


