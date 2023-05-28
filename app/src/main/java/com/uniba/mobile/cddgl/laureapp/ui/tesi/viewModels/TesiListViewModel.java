package com.uniba.mobile.cddgl.laureapp.ui.tesi.viewModels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TesiListViewModel extends ViewModel {

    private final MutableLiveData<List<Tesi>> tesiList = new MutableLiveData<>();

    public TesiListViewModel() {
    }

    public MutableLiveData<List<Tesi>> getTesiList() {
        return tesiList;
    }

    public void removeTesiFromTesiList(Tesi tesi) {

        try {
            List<Tesi> newTesiList = new ArrayList<>();

            if(tesiList.getValue() != null) {
                newTesiList.addAll(tesiList.getValue());
                newTesiList.remove(tesi);

                tesiList.setValue(newTesiList);
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    return;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String studenteId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference classificaRef = db.collection("tesi_classifiche").document(studenteId);
                Map<String, Object> updates = new HashMap<>();
                updates.put("tesi", getIdOfThesis(newTesiList));

                classificaRef.update(updates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i("removeTesiFromTesiList", "Tesi removed from ranking");
                    } else {
                        Log.w("removeTesiFromTesiList", "unable remove tesi from ranking");
                    }
                });
            }

        } catch (Exception e) {
            Log.e("removeTesiFromTesiList", e.getMessage());
        }
    }

    public void saveTesiList(String userID, List<Tesi> tesiList) {

        Map<String, Object> updates = new HashMap<>();
        updates.put("tesi", getIdOfThesis(tesiList));

        FirebaseFirestore.getInstance().collection("tesi_classifiche").document(userID).update(updates);
    }

    private List<String> getIdOfThesis(List<Tesi> tesiList) {

        List<String> listId = new ArrayList<>();

        for (Tesi tesi : tesiList) {
            listId.add(tesi.getId());
        }

        return listId;
    }
}
