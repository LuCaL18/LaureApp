package com.uniba.mobile.cddgl.laureapp.data.model;

import com.uniba.mobile.cddgl.laureapp.data.RoleUser;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String email;
    private String displayName;
    private String name;
    private String surname;
    private String birthDay;
    private String bio;
    private RoleUser role;
    //private String dipartimento;


    public LoggedInUser(String email, String name, String surname, String birthDay, String bio, RoleUser role) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.birthDay = birthDay;
        this.bio = bio;
        this.role = role;
        this.displayName = name + ' ' + surname;
    }

    public LoggedInUser(String email, String displayName) {
        this.email = email;
        this.displayName = displayName;
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

    //    public String getDipartimento() {
//        return dipartimento;
//    }
//
//    public void setDipartimento(String dipartimento) {
//        this.dipartimento = dipartimento;
//    }
}