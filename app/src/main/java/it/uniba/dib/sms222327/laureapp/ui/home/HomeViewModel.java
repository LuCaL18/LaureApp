package it.uniba.dib.sms222327.laureapp.ui.home;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * ViewModel del fragment HomeFragment utilizzato per il conteggio delle notifiche
 */
public class HomeViewModel extends ViewModel {

    private final MutableLiveData<Integer> countNotification = new MutableLiveData<>();

    public HomeViewModel() {
        readNotificationCount();
    }

    public MutableLiveData<Integer> getCountNotification() {
        return countNotification;
    }

    private void readNotificationCount() {

        String id;

        try {
            id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (Exception e) {
            countNotification.setValue(0);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("notifications").whereEqualTo("receiveId", id);
        query.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.w("HomeViewModel", "Listen failed.", e);
                return;
            }

            int numDocuments = 0;
            if(querySnapshot != null) {
                numDocuments = querySnapshot.size();
            }

            countNotification.setValue(numDocuments);
            Log.d("HomeViewModel", "Number of documents in collection: " + numDocuments);
        });
    }
}