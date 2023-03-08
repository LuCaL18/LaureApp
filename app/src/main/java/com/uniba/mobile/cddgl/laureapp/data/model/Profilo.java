package com.uniba.mobile.cddgl.laureapp.data.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.uniba.mobile.cddgl.laureapp.MainActivity;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;

import java.util.HashMap;
import java.util.Map;
@IgnoreExtraProperties
public class Profilo  {

    private String id;
    private String email;
    private String displayName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public RoleUser getRole() {
        return role;
    }

    public void setRole(RoleUser role) {
        this.role = role;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String name;
    private String surname;
    private String birthDay;
    private String bio;
    private RoleUser role;
    private String photoUrl;
    private String token;

    public Profilo(){};

    public Profilo(String id, String email, String displayName, String name, String surname, String birthDay, String bio, RoleUser role, String photoUrl, String token){
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.name = name;
        this.surname = surname;
        this.birthDay = birthDay;
        this.bio = bio;
        this.role = role;
        this.photoUrl = photoUrl;
        this.token = token;
    };

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("email", email);
        result.put("displayName", displayName);
        result.put("name", name);
        result.put("surname", surname);
        result.put("birthDay", birthDay);
        result.put("bio", bio);
        result.put("role", role);
        result.put("photoUrl", photoUrl);
        result.put("token", token);
        return result;
    }

};
