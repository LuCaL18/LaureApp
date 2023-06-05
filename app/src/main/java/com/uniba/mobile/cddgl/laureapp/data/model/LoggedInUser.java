package com.uniba.mobile.cddgl.laureapp.data.model;

import androidx.annotation.Keep;

import com.uniba.mobile.cddgl.laureapp.data.RoleUser;

import java.io.Serializable;
import java.util.List;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser implements Serializable {

    private String id;
    private String email;
    private String displayName;
    private String name;
    private String surname;
    private String birthDay;
    private String bio;
    private RoleUser role;
    private String photoUrl;
    private String token;
    private List<String> ambiti;


    // NON CANCELLARE SERVE A FIREBASE
    @Keep
    public LoggedInUser() {}

    public LoggedInUser(String id) {
        this.id = id;
    }

    public LoggedInUser(String id, String email, String name, String surname, String birthDay, String bio, RoleUser role, List<String> ambiti) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.birthDay = birthDay;
        this.bio = bio;
        this.role = role;
        this.displayName = name + ' ' + surname;
        this.ambiti = ambiti;
    }

    public LoggedInUser(String email, String displayName) {
        this.email = email;
        this.displayName = displayName;
    }

    public LoggedInUser(String email, String displayName, RoleUser role) {
        this.email = email;
        this.displayName = displayName;
        this.role = role;
    }

    public LoggedInUser(String id, String email, String displayName, String name, String surname, String birthDay, String bio, RoleUser role, String photoUrl, String token) {
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
    }

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

    public List<String> getAmbiti() {
        return ambiti;
    }

    public void setAmbiti(List<String> ambiti) {
        this.ambiti = ambiti;
    }

    @Override
    public String toString() {
        return "LoggedInUser{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", birthDay='" + birthDay + '\'' +
                ", bio='" + bio + '\'' +
                ", role=" + role +
                ", photoUrl='" + photoUrl + '\'' +
                ", token='" + token + '\'' +
                ", ambiti=" + ambiti +
                '}';
    }
}