package com.uniba.mobile.cddgl.laureapp.ui.bookings.viewHolder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.BookingState;
import com.uniba.mobile.cddgl.laureapp.data.model.Booking;
import com.uniba.mobile.cddgl.laureapp.ui.bookings.interfaces.BookingItemClickCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookingViewHolder extends RecyclerView.ViewHolder {
    private final TextView titleView;
    private final TextView bodyView;
    private final TextView timestamp;
    private String idBooking;
    private final Context context;

    public BookingViewHolder(@NonNull View itemView, BookingItemClickCallback callback) {
        super(itemView);

        context = itemView.getContext();

        titleView = itemView.findViewById(R.id.booking_thesis_name);
        bodyView = itemView.findViewById(R.id.booking_thesis_body);
        timestamp = itemView.findViewById(R.id.text_timestamp_booking);

        itemView.setOnClickListener(view -> {
            itemView.setSelected(true);
            callback.onBookingClicked((idBooking));
        });
    }

    public void bind(Booking booking) {

        this.idBooking = booking.getId();
        String bodyText;

        titleView.setText(booking.getNameThesis() + " (" + booking.getIdThesis() + ")");
        try {
            if(booking.getState().equals(BookingState.OPEN)) {
                bodyText = context.getString(R.string.notification_body_open_booking, booking.getNameStudent()+ " " + booking.getSurnameStudent(), booking.getNameThesis());
            } else if (booking.getState().equals(BookingState.ACCEPTED)){
                bodyText = context.getString(R.string.booking_accepted_by_professor);
            } else {
                bodyText = context.getString(R.string.booking_refused_by_professor);
            }

            bodyView.setText(bodyText);
        } catch (Exception e) {
            bodyView.setVisibility(View.GONE);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date date;

        date = new Date(booking.getTimestamp());

        timestamp.setText(formatter.format(date));
    }
}
