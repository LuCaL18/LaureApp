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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.uniba.mobile.cddgl.laureapp.MainActivity;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.ChatData;
import com.uniba.mobile.cddgl.laureapp.ui.chat.impl.ChatItemClickCallbackImpl;
import com.uniba.mobile.cddgl.laureapp.ui.chat.interfaces.ChatItemClickCallback;
import com.uniba.mobile.cddgl.laureapp.ui.chat.viewHolder.ChatViewHolder;

import java.util.ArrayList;

public class ChatListFragment extends Fragment {

    private FirebaseRecyclerOptions<ChatData> options;
    private FirebaseRecyclerAdapter<ChatData, ChatViewHolder> adapter;
    private ChatViewModel chatViewModel;
    private ChatItemClickCallback callback;
    private BottomNavigationView navBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider viewModelProvider = new ViewModelProvider(requireParentFragment());
        chatViewModel = viewModelProvider.get(ChatViewModel.class);

        callback = new ChatItemClickCallbackImpl(chatViewModel);

        Query query = FirebaseDatabase.getInstance().getReference().child("chats");
        options = new FirebaseRecyclerOptions.Builder<ChatData>()
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

        adapter = new FirebaseRecyclerAdapter<ChatData, ChatViewHolder>(options) {

            private final ArrayList<ChatData> chatDataList = new ArrayList<>();

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                ChatData data = this.getItem(this.getItemCount() -1);
                String key = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if(data.getMembers().containsKey(key)) {
                    chatDataList.add(data);
                }
            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat, parent, false);

                if(chatDataList.size() == 0) {
                    return new ChatViewHolder(view);
                }

                return new ChatViewHolder(view, callback);
            }

            @Override
            protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull ChatData model) {
                if(chatDataList.size() == 0 || !chatDataList.get(position).equals(model)) {
                    return;
                }

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
            if(loggedInUsers == null) {
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        navBar.setVisibility(View.VISIBLE);
        NavigationView navigationView = requireActivity().findViewById(R.id.nav_view_menu);
        navigationView.getMenu().findItem(MainActivity.CHAT).setChecked(false);
        navBar = null;
        adapter = null;
        options = null;
    }

}