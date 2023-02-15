package com.uniba.mobile.cddgl.laureapp.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.uniba.mobile.cddgl.laureapp.data.model.Task;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<Integer> countNotification = new MutableLiveData<>();
    private final MutableLiveData<List<Task>> tasks = new MutableLiveData<>();

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("TESI");

        readTask();
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

    public void readTask() {

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference tasksRef = db.collection("task");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // replace with your user id

        Query query = tasksRef.whereArrayContains("relators", userId);

        query.get().addOnCompleteListener(taskQuery -> {
            if (taskQuery.isSuccessful()) {
                List<Task> taskList = new ArrayList<>();

                for (QueryDocumentSnapshot document : taskQuery.getResult()) {
                    Task task = document.toObject(Task.class);
                    taskList.add(task);
                }
                tasks.setValue(taskList);
            } else {
                Log.d("HomeFragmentViewModel", "Error getting documents: ", taskQuery.getException());
            }
        });
    }

    public MutableLiveData<List<Task>> getTasks() {
        return tasks;
    }
}