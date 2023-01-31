package com.uniba.mobile.cddgl.laureapp.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<Integer> countNotification = new MutableLiveData<>();

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");

        readNotificationCount();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public MutableLiveData<Integer> getCountNotification() {
        return countNotification;
    }

    private void readNotificationCount() {

        String id;

        try {
            id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (Exception e) {
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("notifications").whereEqualTo("receiveId", id);
        query.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.w("HomeViewModel", "Listen failed.", e);
                return;
            }

            int numDocuments = querySnapshot.size();

            countNotification.setValue(numDocuments);
            Log.d("HomeViewModel", "Number of documents in collection: " + numDocuments);
        });
    }
}