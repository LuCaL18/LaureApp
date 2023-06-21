package com.uniba.mobile.cddgl.laureapp.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Notification;
import com.uniba.mobile.cddgl.laureapp.databinding.FragmentNotificationsBinding;
import com.uniba.mobile.cddgl.laureapp.ui.bookings.BookingViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.bookings.interfaces.BookingItemClickCallback;
import com.uniba.mobile.cddgl.laureapp.ui.calendario.interfaces.MeetingItemClickCallback;
import com.uniba.mobile.cddgl.laureapp.ui.calendario.viewModels.MeetingViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.chat.ChatViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.chat.interfaces.ChatItemClickCallback;
import com.uniba.mobile.cddgl.laureapp.ui.notifications.adapters.NotificationAdapter;
import com.uniba.mobile.cddgl.laureapp.ui.notifications.impl.NotificationBookingItemClickCallback;
import com.uniba.mobile.cddgl.laureapp.ui.notifications.impl.NotificationChatItemClickCallback;
import com.uniba.mobile.cddgl.laureapp.ui.notifications.impl.NotificationMeetingItemClickCallback;
import com.uniba.mobile.cddgl.laureapp.ui.notifications.impl.NotificationTicketItemClickCallback;
import com.uniba.mobile.cddgl.laureapp.ui.ticket.TicketViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.ticket.interfaces.TicketItemClickCallback;

/**
 * Fragment che si occupa della gestione e della visualizzazione delle notifiche
 */
public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private BottomNavigationView navBar;
    private NotificationAdapter adapter;
    private ChatViewModel chatViewModel;
    private TicketViewModel ticketViewModel;
    private BookingViewModel bookingViewModel;
    private MeetingViewModel meetingViewModel;
    private RecyclerView notificationListRecyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        ViewModelProvider viewModelProvider = new ViewModelProvider(requireParentFragment());
        chatViewModel = viewModelProvider.get(ChatViewModel.class);
        ticketViewModel = viewModelProvider.get(TicketViewModel.class);
        bookingViewModel = viewModelProvider.get(BookingViewModel.class);
        meetingViewModel = viewModelProvider.get(MeetingViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        notificationListRecyclerView = binding.notificationListRecyclerView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        notificationListRecyclerView.setLayoutManager(linearLayoutManager);


        final TextView textView = binding.textNotifications;
        String id;

        try {
            id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }catch (NullPointerException e) {
            textView.setVisibility(View.VISIBLE);
            notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

            return root;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notificationsRef = db.collection("notifications");
        Query query = notificationsRef.whereEqualTo("receiveId", id).orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Notification> options = new FirestoreRecyclerOptions.Builder<Notification>()
                .setQuery(query, Notification.class)
                .build();

        ChatItemClickCallback chatItemClickCallback = new NotificationChatItemClickCallback(chatViewModel);
        TicketItemClickCallback ticketItemClickCallback = new NotificationTicketItemClickCallback(ticketViewModel);
        BookingItemClickCallback bookingItemClickCallback = new NotificationBookingItemClickCallback(bookingViewModel);
        MeetingItemClickCallback meetingItemClickCallback = new NotificationMeetingItemClickCallback(meetingViewModel);

        adapter = new NotificationAdapter(options, chatItemClickCallback, ticketItemClickCallback, bookingItemClickCallback, meetingItemClickCallback, textView, notificationListRecyclerView);

        notificationListRecyclerView.setAdapter(adapter);

        adapter.startListening();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatViewModel.getMembers().observe(getViewLifecycleOwner(), loggedInUsers -> {
            if(loggedInUsers == null) {
                return;
            }
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_navigation_notifications_to_chatFragment);
        });

        ticketViewModel.getTicket().observe(getViewLifecycleOwner(), ticket -> {
            if(ticket == null || ticketViewModel.isAlreadyRead()) {
                return;
            }

            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_navigation_notifications_to_ticketFragment);

        });

        bookingViewModel.getBooking().observe(getViewLifecycleOwner(), booking -> {
            if(booking == null || bookingViewModel.isAlreadyRead()) {
                return;
            }

            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_navigation_notifications_to_bookingFragment);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        if(adapter != null) {
            adapter.onAttachedToRecyclerView(notificationListRecyclerView);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter != null) {
            adapter.onDetachedFromRecyclerView(notificationListRecyclerView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(adapter != null) {
            adapter.stopListening();
        }

        chatViewModel.getMembers().removeObservers(getViewLifecycleOwner());
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        navBar.setVisibility(View.VISIBLE);
    }
}