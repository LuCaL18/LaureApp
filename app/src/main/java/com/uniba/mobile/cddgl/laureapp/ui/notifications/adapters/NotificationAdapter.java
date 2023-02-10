package com.uniba.mobile.cddgl.laureapp.ui.notifications.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.NotificationType;
import com.uniba.mobile.cddgl.laureapp.data.model.Notification;
import com.uniba.mobile.cddgl.laureapp.ui.bookings.interfaces.BookingItemClickCallback;
import com.uniba.mobile.cddgl.laureapp.ui.chat.interfaces.ChatItemClickCallback;
import com.uniba.mobile.cddgl.laureapp.ui.notifications.impl.NotificationBookingItemClickCallback;
import com.uniba.mobile.cddgl.laureapp.ui.notifications.impl.NotificationChatItemClickCallback;
import com.uniba.mobile.cddgl.laureapp.ui.notifications.impl.NotificationTicketItemClickCallback;
import com.uniba.mobile.cddgl.laureapp.ui.notifications.viewHolder.BookingNotificationViewHolder;
import com.uniba.mobile.cddgl.laureapp.ui.notifications.viewHolder.ChatNotificationViewHolder;
import com.uniba.mobile.cddgl.laureapp.ui.notifications.viewHolder.NotificationViewHolder;
import com.uniba.mobile.cddgl.laureapp.ui.notifications.viewHolder.TicketNotificationViewHolder;
import com.uniba.mobile.cddgl.laureapp.ui.ticket.interfaces.TicketItemClickCallback;

public class NotificationAdapter extends FirestoreRecyclerAdapter<Notification, NotificationViewHolder> {

    private static final int VIEW_TYPE_CHAT_NOTIFICATION = 1;
    private static final int VIEW_TYPE_TICKET_NOTIFICATION = 2;
    private static final int VIEW_TYPE_BOOKING_NOTIFICATION = 3;

    private final ChatItemClickCallback chatItemClickCallback;
    private final TicketItemClickCallback ticketItemClickCallback;
    private final BookingItemClickCallback bookingItemClickCallback;
    private final TextView textView;
    private final RecyclerView notificationListRecyclerView;

    public NotificationAdapter(FirestoreRecyclerOptions<Notification> options,
                               ChatItemClickCallback chatItemClickCallback,
                               TicketItemClickCallback ticketItemClickCallback,
                               BookingItemClickCallback bookingItemClickCallback,
                               TextView textView,
                               RecyclerView notificationListRecyclerView) {
        super(options);
        this.chatItemClickCallback = chatItemClickCallback;
        this.ticketItemClickCallback = ticketItemClickCallback;
        this.bookingItemClickCallback = bookingItemClickCallback;
        this.textView = textView;
        this.notificationListRecyclerView = notificationListRecyclerView;
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();

        if (this.getItemCount() > 0) {
            notificationListRecyclerView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        } else {
            notificationListRecyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Notification notification = this.getItem(position);

        switch (notification.getType()) {
            case MESSAGE:
                return VIEW_TYPE_CHAT_NOTIFICATION;
            case BOOKING:
                return VIEW_TYPE_BOOKING_NOTIFICATION;
            case TICKET:
            default:
                return VIEW_TYPE_TICKET_NOTIFICATION;
        }
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);

        switch (viewType) {
            case VIEW_TYPE_CHAT_NOTIFICATION:
                return new ChatNotificationViewHolder(view, (NotificationChatItemClickCallback) chatItemClickCallback);
            case VIEW_TYPE_BOOKING_NOTIFICATION:
                return new BookingNotificationViewHolder(view, (NotificationBookingItemClickCallback) bookingItemClickCallback);
            case VIEW_TYPE_TICKET_NOTIFICATION:
            default:
                return new TicketNotificationViewHolder(view, (NotificationTicketItemClickCallback) ticketItemClickCallback);
        }
    }

    @Override
    protected void onBindViewHolder(NotificationViewHolder holder, int position, @NonNull Notification model) {
        holder.bind(model, getSnapshots().getSnapshot(position).getId());
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        for (int i = 0; i < getItemCount(); i++) {
            NotificationViewHolder viewHolder = (NotificationViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolder != null) {
                viewHolder.deleteNotification();
            }
        }
    }
}
