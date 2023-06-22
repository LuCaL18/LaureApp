package it.uniba.dib.sms222327.laureapp.ui.calendario.viewModels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import it.uniba.dib.sms222327.laureapp.data.model.Ricevimento;

public class MeetingViewModel extends ViewModel {

    private final MutableLiveData<Ricevimento> ricevimento = new MutableLiveData<>();
    private boolean isAlreadyRead = false;

    public MeetingViewModel() {}

    public void init(String id) {

        isAlreadyRead = false;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ricevimento").document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Ricevimento ricevimentoData = documentSnapshot.toObject(Ricevimento.class);
                            ricevimento.setValue(ricevimentoData);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("MeetingViewModel", "Unable fetch ricevimento data");
                    }
                });
    }

    public MutableLiveData<Ricevimento> getRicevimento() {
        return ricevimento;
    }

    public boolean isAlreadyRead() {
        return isAlreadyRead;
    }

    public void setAlreadyRead(boolean alreadyRead) {
        isAlreadyRead = alreadyRead;
    }
}
