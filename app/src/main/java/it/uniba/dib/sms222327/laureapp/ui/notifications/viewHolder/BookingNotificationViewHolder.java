package it.uniba.dib.sms222327.laureapp.ui.notifications.viewHolder;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import it.uniba.dib.sms222327.laureapp.R;
import it.uniba.dib.sms222327.laureapp.data.model.Notification;
import it.uniba.dib.sms222327.laureapp.ui.notifications.impl.NotificationBookingItemClickCallback;

/**
 * La classe BookingViewHolder estende NotificationViewHolder
 * ed è responsabile per la gestione della vista delle notifiche di prenotazione
 */
public class BookingNotificationViewHolder extends NotificationViewHolder {

    private boolean selected;
    private final NotificationBookingItemClickCallback notificationBookingItemClickCallback;
    private String bookingId;
    private final Context context;

    public BookingNotificationViewHolder(@NonNull View itemView, NotificationBookingItemClickCallback callback) {
        super(itemView);

        context = itemView.getContext();
        this.selected = false;
        this.notificationBookingItemClickCallback = callback;

        itemView.setOnClickListener(view -> {
//            chatNameTextView.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.item_selected));
            itemView.setSelected(true);
            selected = true;
            notificationBookingItemClickCallback.onBookingClicked(bookingId);
        });
    }

    @Override
    public void bind(Notification notification, String idNotification) {
        super.bind(notification, idNotification);

        bookingId = notification.getTicketId();
        iconImageView.setImageResource(R.drawable.ic_baseline_book_online_24);

        titleTextView.setText(context.getString(R.string.notification_title_booking, notification.getNameChat()));

        if(notification.getBody().equals("booking_opened")) {
            bodyTextView.setText(context.getString(R.string.notification_body_open_booking, notification.getSenderName(), notification.getNameChat()));
        } else if(notification.getBody().equals("accepted_booking")) {
            bodyTextView.setText(context.getString(R.string.your_booking_has_been_accepted));
        } else {
            bodyTextView.setText(context.getString(R.string.your_booking_has_been_refused));
        }
    }

    @Override
    public void deleteNotification() {
        if(selected) {
            notificationBookingItemClickCallback.deleteNotification(idNotification);
        }
    }
}
