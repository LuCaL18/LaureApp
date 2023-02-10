package com.uniba.mobile.cddgl.laureapp;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;

import java.util.Map;

public class MainViewModel extends ViewModel {

    private final static String CLASSNAME = "MainViewModel";

    private final MutableLiveData<LoggedInUser> user = new MutableLiveData<>();
    private String idUser;
    private String fileToOpen;
    private Long downloadReference;
    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference();


    public MainViewModel() {}

    public void init(LoggedInUser user) {

        if(user.getRole() == null) {
            this.fetchDataUser(user.getId());
            return;
        }

        this.user.setValue(user);
        this.idUser = user.getId();
    }

    public void fetchDataUser(String id) {
        db.child("users").child(id).get().addOnCompleteListener(task -> {
           if(task.isSuccessful()) {
               user.setValue(task.getResult().getValue(LoggedInUser.class));
               idUser = id;
           }
        });
    }

    public void updateUser(Map<String, Object> updates) {

        if(user.getValue() == null) {
            Log.w(CLASSNAME, "User is null");
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getValue().getId());

        userRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> fetchDataUser(user.getValue().getId()))
                .addOnFailureListener(e -> Log.e(CLASSNAME, "Unable updates user"));
    }

    public MutableLiveData<LoggedInUser> getUser() {
        return user;
    }

    public void setUser(LoggedInUser user) {
        this.user.setValue(user);
    }

    public String getIdUser() {
        return idUser;
    }

    public String getFileToOpen() {
        return fileToOpen;
    }

    public void setFileToOpen(String fileToOpen) {
        this.fileToOpen = fileToOpen;
    }

    public Long getDownloadReference() {
        return downloadReference;
    }

    public void setDownloadReference(Long downloadReference) {
        this.downloadReference = downloadReference;
    }
}
