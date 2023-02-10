package com.uniba.mobile.cddgl.laureapp.ui.bookings.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Booking;
import com.uniba.mobile.cddgl.laureapp.ui.bookings.interfaces.BookingItemClickCallback;
import com.uniba.mobile.cddgl.laureapp.ui.bookings.viewHolder.BookingViewHolder;

public class BookingAdapter extends FirestoreRecyclerAdapter<Booking, BookingViewHolder> {

    private final BookingItemClickCallback callback;

    public BookingAdapter(@NonNull FirestoreRecyclerOptions<Booking> options, BookingItemClickCallback callback) {
        super(options);
        this.callback = callback;
    }

    @Override
    public BookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_list_item, parent, false);
        return new BookingViewHolder(view, callback);
    }

    @Override
    protected void onBindViewHolder(BookingViewHolder holder, int position, @NonNull Booking model) {
        holder.bind(model);
    }
}
