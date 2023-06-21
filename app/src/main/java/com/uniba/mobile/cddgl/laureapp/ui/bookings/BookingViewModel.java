package com.uniba.mobile.cddgl.laureapp.ui.bookings;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.data.model.Booking;

public class BookingViewModel extends ViewModel {

    private final MutableLiveData<Booking> booking = new MutableLiveData<>();
    private boolean isAlreadyRead = false;

    public BookingViewModel() {}

    /**
     * Inizializza il ViewModel con l'ID della prenotazione.
     * Recupera i dati della prenotazione dal database Firebase Firestore.
     *
     * @param id ID della prenotazione
     */
    public void init(String id) {

        isAlreadyRead = false;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("bookings").document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Booking bookingData = documentSnapshot.toObject(Booking.class);
                            booking.setValue(bookingData);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("BookingViewModel", "Unable fetch ticket data");
                    }
                });
    }

    public MutableLiveData<Booking> getBooking() {
        return booking;
    }

    public boolean isAlreadyRead() {
        return isAlreadyRead;
    }

    public void setAlreadyRead(boolean alreadyRead) {
        isAlreadyRead = alreadyRead;
    }
}
