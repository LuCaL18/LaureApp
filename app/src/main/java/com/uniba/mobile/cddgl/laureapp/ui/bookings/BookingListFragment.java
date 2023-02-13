package com.uniba.mobile.cddgl.laureapp.ui.bookings;

import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.uniba.mobile.cddgl.laureapp.MainActivity;
import com.uniba.mobile.cddgl.laureapp.MainViewModel;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.BookingState;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.TicketState;
import com.uniba.mobile.cddgl.laureapp.data.model.Booking;
import com.uniba.mobile.cddgl.laureapp.data.model.Ticket;
import com.uniba.mobile.cddgl.laureapp.ui.bookings.adapters.BookingAdapter;
import com.uniba.mobile.cddgl.laureapp.ui.bookings.impl.BookingItemClickCallbackImpl;
import com.uniba.mobile.cddgl.laureapp.ui.bookings.interfaces.BookingItemClickCallback;
import com.uniba.mobile.cddgl.laureapp.ui.ticket.adapters.TicketAdapter;

public class BookingListFragment extends Fragment {

    private BottomNavigationView navBar;
    private RecyclerView bookingListRecyclerView;
    private BookingAdapter adapterOpen;
    private BookingAdapter adapterAccepted;
    private BookingAdapter adapterRefused;
    private BookingViewModel bookingViewModel;

    public BookingListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider viewModelProvider = new ViewModelProvider(requireParentFragment());
        bookingViewModel = viewModelProvider.get(BookingViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_booking_list, container, false);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        bookingListRecyclerView = root.findViewById(R.id.booking_list_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        bookingListRecyclerView.setLayoutManager(linearLayoutManager);

        TabLayout bookingTabLayout = root.findViewById(R.id.tab_layout);
        final TextView textView = root.findViewById(R.id.text_no_bookings);

        String id = mainViewModel.getUser().getValue().getId();
        RoleUser role = mainViewModel.getUser().getValue().getRole();

        if (id == null) {
            textView.setVisibility(View.VISIBLE);
            return root;
        }

        BookingItemClickCallback callback = new BookingItemClickCallbackImpl(bookingViewModel);

        adapterOpen = new BookingAdapter(createOption(id, role, BookingState.OPEN), callback);
        adapterAccepted = new BookingAdapter(createOption(id, role, BookingState.ACCEPTED), callback);
        adapterRefused = new BookingAdapter(createOption(id, role, BookingState.REFUSED), callback);

        bookingTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    bookingListRecyclerView.setAdapter(adapterOpen);
                } else if (tab.getPosition() == 1) {
                    bookingListRecyclerView.setAdapter(adapterAccepted);
                } else {
                    bookingListRecyclerView.setAdapter(adapterRefused);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        bookingListRecyclerView.setAdapter(adapterOpen);

        adapterOpen.startListening();
        adapterAccepted.startListening();
        adapterRefused.startListening();
        return root;
    }

    private FirestoreRecyclerOptions<Booking> createOption(String id, RoleUser role, BookingState state) {

        try {
            String columnId;

            if(role.equals(RoleUser.STUDENT)) {
                columnId = "studentId";
            } else if (role.equals(RoleUser.PROFESSOR)) {
                columnId = "profId";
            } else {
                return new FirestoreRecyclerOptions.Builder<Booking>().build();
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference notificationsRef = db.collection("bookings");
            Query query = notificationsRef.whereEqualTo(columnId, id)
                    .whereEqualTo("state", state.name())
                    .orderBy("timestamp", Query.Direction.DESCENDING);

            return new FirestoreRecyclerOptions.Builder<Booking>()
                    .setQuery(query, Booking.class)
                    .build();

        } catch (Exception e) {
            Log.e("BookingListFragment", e.getMessage());
            return new FirestoreRecyclerOptions.Builder<Booking>().build();
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookingViewModel.getBooking().observe(getViewLifecycleOwner(), booking -> {

            if(booking == null || bookingViewModel.isAlreadyRead()) {
                return;
            }

            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_nav_bookingListFragment_to_bookingFragment);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        try {
            adapterAccepted.stopListening();
            adapterOpen.stopListening();
            adapterRefused.startListening();
        } catch(Exception e) {
            Log.w("BookingListFragment", e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        navBar.setVisibility(View.VISIBLE);
        NavigationView navigationView = requireActivity().findViewById(R.id.nav_view_menu);
        navigationView.getMenu().findItem(MainActivity.BOOKING).setChecked(false);
        navBar = null;
    }
}
