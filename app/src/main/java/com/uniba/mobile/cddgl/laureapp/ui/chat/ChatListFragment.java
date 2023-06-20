package com.uniba.mobile.cddgl.laureapp.ui.chat;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.uniba.mobile.cddgl.laureapp.MainActivity;
import com.uniba.mobile.cddgl.laureapp.MainViewModel;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.ChatData;
import com.uniba.mobile.cddgl.laureapp.ui.chat.impl.ChatItemClickCallbackImpl;
import com.uniba.mobile.cddgl.laureapp.ui.chat.interfaces.ChatItemClickCallback;
import com.uniba.mobile.cddgl.laureapp.ui.chat.viewHolder.ChatViewHolder;

/**
 * Fragment che si occupa della visualizzazione e della gestione della lista delle chat
 */
public class ChatListFragment extends Fragment {

    private FirestoreRecyclerOptions<ChatData> options;
    private FirestoreRecyclerAdapter<ChatData, ChatViewHolder> adapter;
    private ChatViewModel chatViewModel;
    private ChatItemClickCallback callback;
    private BottomNavigationView navBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        ViewModelProvider viewModelProvider = new ViewModelProvider(requireParentFragment());
        chatViewModel = viewModelProvider.get(ChatViewModel.class);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        callback = new ChatItemClickCallbackImpl(chatViewModel);

        Query query = FirebaseFirestore.getInstance().collection("chats").whereArrayContains("members", mainViewModel.getIdUser());
        options = new FirestoreRecyclerOptions.Builder<ChatData>()
                .setQuery(query, ChatData.class)
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        RecyclerView chatListRecyclerView = view.findViewById(R.id.chat_list_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        chatListRecyclerView.setLayoutManager(linearLayoutManager);

        adapter = new FirestoreRecyclerAdapter<ChatData, ChatViewHolder>(options) {

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat, parent, false);

                return new ChatViewHolder(view, callback);
            }

            @Override
            protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull ChatData model) {
                holder.bind(model);
            }
        };

        chatListRecyclerView.setAdapter(adapter);

        adapter.startListening();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = NavHostFragment.findNavController(this);

        chatViewModel.getMembers().observe(getViewLifecycleOwner(), loggedInUsers -> {
            if (loggedInUsers == null) {
                return;
            }
            navController.navigate(R.id.action_chatListFragment_to_chatFragment);
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
        adapter.stopListening();
        chatViewModel.getMembers().removeObservers(getViewLifecycleOwner());
        NavigationView navigationView = requireActivity().findViewById(R.id.nav_view_menu);
        navigationView.getMenu().findItem(MainActivity.CHAT).setChecked(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (navBar != null) {
            navBar.setVisibility(View.VISIBLE);
        }

        navBar = null;
        adapter = null;
        options = null;
    }

}