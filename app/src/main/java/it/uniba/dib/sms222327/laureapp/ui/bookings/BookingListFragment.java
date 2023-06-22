package it.uniba.dib.sms222327.laureapp.ui.bookings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.ChangeEventListener;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import it.uniba.dib.sms222327.laureapp.MainActivity;
import it.uniba.dib.sms222327.laureapp.MainViewModel;
import it.uniba.dib.sms222327.laureapp.R;
import it.uniba.dib.sms222327.laureapp.data.BookingState;
import it.uniba.dib.sms222327.laureapp.data.RoleUser;
import it.uniba.dib.sms222327.laureapp.data.model.Booking;
import it.uniba.dib.sms222327.laureapp.ui.bookings.adapters.BookingAdapter;
import it.uniba.dib.sms222327.laureapp.ui.bookings.impl.BookingItemClickCallbackImpl;
import it.uniba.dib.sms222327.laureapp.ui.bookings.interfaces.BookingItemClickCallback;

public class BookingListFragment extends Fragment {

    //Tab visualizzati nel layout
    private final int TAB_OPEN = 0;
    private final int TAB_ACCEPTED = 1;
    private final int TAB_REFUSED = 2;

    private BottomNavigationView navBar;
    private RecyclerView bookingListRecyclerView;
    private BookingAdapter adapterOpen;
    private BookingAdapter adapterAccepted;
    private BookingAdapter adapterRefused;
    private BookingViewModel bookingViewModel;
    private LinearLayout textNoBookings;
    private ScrollView bookingListScroll;
    private ChangeEventListener changeEventListenerOpen;
    private ChangeEventListener changeEventListenerAccepted;
    private ChangeEventListener changeEventListenerRefused;
    private int currentTab;

    public BookingListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Ottieni il view Model dal parent
        ViewModelProvider viewModelProvider = new ViewModelProvider(requireParentFragment());
        bookingViewModel = viewModelProvider.get(BookingViewModel.class);

        if (savedInstanceState != null && savedInstanceState.getString("current_tab") != null) {
            currentTab = Integer.parseInt(savedInstanceState.getString("current_tab"));
        } else {
            currentTab = TAB_OPEN;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflate del layout
        return inflater.inflate(R.layout.fragment_booking_list, container, false);
    }

    private FirestoreRecyclerOptions<Booking> createOption(String id, RoleUser role, BookingState state) {
        //creazione per le option da impostare per il FirestoreRecycler
        try {
            String columnId;

            if (role.equals(RoleUser.STUDENT)) {
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
        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        bookingListRecyclerView = view.findViewById(R.id.booking_list_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        bookingListRecyclerView.setLayoutManager(linearLayoutManager);

        TabLayout bookingTabLayout = view.findViewById(R.id.tab_layout);
        bookingListScroll = view.findViewById(R.id.scroll_view_booking_list);
        textNoBookings = view.findViewById(R.id.text_no_bookings);

        String id = mainViewModel.getIdUser();
        RoleUser role = mainViewModel.getUser().getValue().getRole();

        if (id == null) {
            bookingListScroll.setVisibility(View.GONE);
            textNoBookings.setVisibility(View.VISIBLE);
            return;
        }

        BookingItemClickCallback callback = new BookingItemClickCallbackImpl(bookingViewModel);

        // Crea gli adapter per i diversi stati delle prenotazioni
        adapterOpen = new BookingAdapter(createOption(id, role, BookingState.OPEN), callback);
        adapterAccepted = new BookingAdapter(createOption(id, role, BookingState.ACCEPTED), callback);
        adapterRefused = new BookingAdapter(createOption(id, role, BookingState.REFUSED), callback);

        // Crea i listener degli eventi di cambio per gli adapter
        changeEventListenerOpen = createListenersForAdapter(adapterOpen);
        changeEventListenerAccepted = createListenersForAdapter(adapterAccepted);
        changeEventListenerRefused = createListenersForAdapter(adapterRefused);

        bookingTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == TAB_OPEN) {
                    bookingListRecyclerView.setAdapter(adapterOpen);
                    currentTab = TAB_OPEN;

                    adapterOpen.getSnapshots().addChangeEventListener(changeEventListenerOpen);
                    adapterAccepted.getSnapshots().removeChangeEventListener(changeEventListenerAccepted);
                    adapterRefused.getSnapshots().removeChangeEventListener(changeEventListenerRefused);
                } else if (tab.getPosition() == TAB_ACCEPTED) {
                    bookingListRecyclerView.setAdapter(adapterAccepted);
                    currentTab = TAB_ACCEPTED;

                    adapterOpen.getSnapshots().removeChangeEventListener(changeEventListenerOpen);
                    adapterAccepted.getSnapshots().addChangeEventListener(changeEventListenerAccepted);
                    adapterRefused.getSnapshots().removeChangeEventListener(changeEventListenerRefused);
                } else if (tab.getPosition() == TAB_REFUSED) {
                    bookingListRecyclerView.setAdapter(adapterRefused);
                    currentTab = TAB_REFUSED;

                    adapterOpen.getSnapshots().removeChangeEventListener(changeEventListenerOpen);
                    adapterAccepted.getSnapshots().removeChangeEventListener(changeEventListenerAccepted);
                    adapterRefused.getSnapshots().addChangeEventListener(changeEventListenerRefused);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        adapterOpen.startListening();
        adapterAccepted.startListening();
        adapterRefused.startListening();

        bookingTabLayout.selectTab(bookingTabLayout.getTabAt(currentTab));

        if (currentTab == TAB_OPEN) {
            bookingListRecyclerView.setAdapter(adapterOpen);
            adapterOpen.getSnapshots().addChangeEventListener(changeEventListenerOpen);
        }

        bookingViewModel.getBooking().observe(getViewLifecycleOwner(), booking -> {

            if (booking == null || bookingViewModel.isAlreadyRead()) {
                return;
            }

            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_nav_bookingListFragment_to_bookingFragment);
        });
    }

    private ChangeEventListener createListenersForAdapter(BookingAdapter adapter) {
        return new ChangeEventListener() {
            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
                // Handle child item changed
            }

            @Override
            public void onDataChanged() {
                if (adapter.getSnapshots().isEmpty()) {
                    bookingListScroll.setVisibility(View.GONE);
                    textNoBookings.setVisibility(View.VISIBLE);
                } else {
                    bookingListScroll.setVisibility(View.VISIBLE);
                    textNoBookings.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
                Log.e("AdapterBooking", e.getMessage());
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("current_tab", String.valueOf(currentTab));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        try {
            adapterAccepted.stopListening();
            adapterOpen.stopListening();
            adapterRefused.stopListening();
        } catch (Exception e) {
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

        changeEventListenerRefused = null;
        changeEventListenerAccepted = null;
        changeEventListenerOpen = null;
    }
}
