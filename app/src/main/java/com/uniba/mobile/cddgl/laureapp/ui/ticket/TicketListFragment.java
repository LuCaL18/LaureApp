package com.uniba.mobile.cddgl.laureapp.ui.ticket;

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
import com.uniba.mobile.cddgl.laureapp.MainActivity;
import com.uniba.mobile.cddgl.laureapp.MainViewModel;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.TicketState;
import com.uniba.mobile.cddgl.laureapp.data.model.Ticket;
import com.uniba.mobile.cddgl.laureapp.ui.ticket.adapters.TicketAdapter;
import com.uniba.mobile.cddgl.laureapp.ui.ticket.impl.TicketItemClickCallbackImpl;
import com.uniba.mobile.cddgl.laureapp.ui.ticket.interfaces.TicketItemClickCallback;

public class TicketListFragment extends Fragment {

    private final int TAB_OPENED = 0;
    private final int TAB_CLOSED = 1;

    private BottomNavigationView navBar;
    private RecyclerView ticketListRecyclerView;
    private TicketAdapter adapterOpen;
    private TicketAdapter adapterClosed;
    private TicketViewModel ticketViewModel;
    private LinearLayout textNoTickets;
    private ScrollView ticketListScroll;
    private ChangeEventListener changeEventListenerOpened;
    private ChangeEventListener changeEventListenerClosed;
    private int currentTab;

    public TicketListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider viewModelProvider = new ViewModelProvider(requireParentFragment());
        ticketViewModel = viewModelProvider.get(TicketViewModel.class);

        if (savedInstanceState != null && savedInstanceState.getString("current_tab") != null) {
            currentTab = Integer.parseInt(savedInstanceState.getString("current_tab"));
        } else {
            currentTab = TAB_OPENED;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_ticket_list, container, false);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        ticketListRecyclerView = root.findViewById(R.id.ticket_list_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        ticketListRecyclerView.setLayoutManager(linearLayoutManager);

        TabLayout ticketTabLayout = root.findViewById(R.id.tab_layout);
        textNoTickets = root.findViewById(R.id.text_no_tickets);

        ticketListScroll = root.findViewById(R.id.scroll_view_ticket_list);

        String id = mainViewModel.getUser().getValue().getId();
        RoleUser role = mainViewModel.getUser().getValue().getRole();

        if (id == null) {
            ticketListScroll.setVisibility(View.GONE);
            textNoTickets.setVisibility(View.VISIBLE);
            return root;
        }

        TicketItemClickCallback callback = new TicketItemClickCallbackImpl(ticketViewModel);

        adapterOpen = new TicketAdapter(createOption(id, role, TicketState.OPEN), callback);
        adapterClosed = new TicketAdapter(createOption(id, role, TicketState.CLOSED), callback);

        changeEventListenerOpened = createListenersForAdapter(adapterOpen);
        changeEventListenerClosed = createListenersForAdapter(adapterClosed);

        ticketTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == TAB_OPENED) {
                    // Open Tickets tab selected
                    ticketListRecyclerView.setAdapter(adapterOpen);
                    currentTab = TAB_OPENED;
                    adapterOpen.getSnapshots().addChangeEventListener(changeEventListenerOpened);
                    adapterClosed.getSnapshots().removeChangeEventListener(changeEventListenerClosed);
                } else {
                    // Closed Tickets tab selected
                    ticketListRecyclerView.setAdapter(adapterClosed);
                    currentTab = TAB_CLOSED;
                    adapterClosed.getSnapshots().addChangeEventListener(changeEventListenerClosed);
                    adapterOpen.getSnapshots().removeChangeEventListener(changeEventListenerOpened);
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
        adapterClosed.startListening();

        ticketTabLayout.selectTab(ticketTabLayout.getTabAt(currentTab));

        if (currentTab == TAB_OPENED) {
            ticketListRecyclerView.setAdapter(adapterOpen);
            adapterOpen.getSnapshots().addChangeEventListener(changeEventListenerOpened);
        }

        return root;
    }

    private FirestoreRecyclerOptions<Ticket> createOption(String id, RoleUser role, TicketState state) {

        String columnTimestamp;
        String columnId;

        if (state.equals(TicketState.OPEN)) {
            columnTimestamp = "timestampSender";
        } else {
            columnTimestamp = "timestampReceiver";
        }

        if (role.equals(RoleUser.STUDENT)) {
            columnId = "idSender";
        } else if (role.equals(RoleUser.PROFESSOR)) {
            columnId = "idReceiver";
        } else {
            return new FirestoreRecyclerOptions.Builder<Ticket>().build();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notificationsRef = db.collection("tickets");
        Query query = notificationsRef.whereEqualTo(columnId, id)
                .whereEqualTo("state", state.name())
                .orderBy(columnTimestamp, Query.Direction.DESCENDING);

        return new FirestoreRecyclerOptions.Builder<Ticket>()
                .setQuery(query, Ticket.class)
                .build();
    }


    private ChangeEventListener createListenersForAdapter(TicketAdapter adapter) {
        return new ChangeEventListener() {
            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
                // Handle child item changed
            }

            @Override
            public void onDataChanged() {
                if (adapter.getSnapshots().isEmpty()) {
                    ticketListScroll.setVisibility(View.GONE);
                    textNoTickets.setVisibility(View.VISIBLE);
                } else {
                    ticketListScroll.setVisibility(View.VISIBLE);
                    textNoTickets.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
                Log.e("AdapterTicket", e.getMessage());
            }
        };
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ticketViewModel.getTicket().observe(getViewLifecycleOwner(), ticket -> {

            if (ticket == null || ticketViewModel.isAlreadyRead()) {
                return;
            }

            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_nav_ticket_to_ticketFragment);
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
            adapterClosed.stopListening();
            adapterOpen.stopListening();
        } catch (Exception e) {
            Log.w("TicketListFragment", e.getMessage());
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("current_tab", String.valueOf(currentTab));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        navBar.setVisibility(View.VISIBLE);
        NavigationView navigationView = requireActivity().findViewById(R.id.nav_view_menu);
        navigationView.getMenu().findItem(MainActivity.TICKET).setChecked(false);
        navBar = null;
        changeEventListenerClosed = null;
        changeEventListenerOpened = null;
    }
}