package com.uniba.mobile.cddgl.laureapp.ui.ticket;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.data.model.Ticket;

public class TicketViewModel extends ViewModel {

    private final MutableLiveData<Ticket> ticket = new MutableLiveData<>();
    private boolean isAlreadyRead = false;

    public TicketViewModel() {}

    public void init(String id) {

        isAlreadyRead = false;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("tickets").document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Ticket ticketData = documentSnapshot.toObject(Ticket.class);
                            ticket.setValue(ticketData);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TicketViewModel", "Unable fetch ticket data");
                    }
                });
    }

    public MutableLiveData<Ticket> getTicket() {
        return ticket;
    }

    public boolean isAlreadyRead() {
        return isAlreadyRead;
    }

    public void setAlreadyRead(boolean alreadyRead) {
        isAlreadyRead = alreadyRead;
    }
}
