package it.uniba.dib.sms222327.laureapp.data.model;

import androidx.annotation.Keep;

import it.uniba.dib.sms222327.laureapp.data.RoleUser;

import java.io.Serializable;
import java.util.List;

/**
 * Classe di dati che contiene le informazioni dell'utente loggato recuperate da LoginRepository
 */
public class LoggedInUser implements Serializable {

    private String id; // Identificatore dell'utente
    private String email; // Email dell'utente
    private String displayName; // Nome visualizzato dell'utente
    private String name; // Nome dell'utente
    private String surname; // Cognome dell'utente
    private String birthDay; // Data di nascita dell'utente
    private String bio; // Biografia dell'utente
    private RoleUser role; // Ruolo dell'utente
    private String photoUrl; // URL della foto dell'utente
    private String token; // Token di autenticazione dell'utente
    private List<String> ambiti; // Lista degli ambiti dell'utente


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