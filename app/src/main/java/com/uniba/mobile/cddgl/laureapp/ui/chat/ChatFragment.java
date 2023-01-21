package com.uniba.mobile.cddgl.laureapp.ui.chat;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.Message;
import com.uniba.mobile.cddgl.laureapp.databinding.FragmentChatBinding;
import com.uniba.mobile.cddgl.laureapp.ui.chat.viewHolder.MessageViewHolder;
import com.uniba.mobile.cddgl.laureapp.ui.chat.viewHolder.ReceiveMessageHolder;
import com.uniba.mobile.cddgl.laureapp.ui.chat.viewHolder.SendMessageViewHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private String chatId = "";
    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private BottomNavigationView navBar;
    private ImageView arrowNotification;
    private LinearLayoutManager linearLayoutManager;
    private final Map<String, String> membersDisplayName = new HashMap<>();
    private ChatViewModel chatModel;

    private FirebaseRecyclerAdapter<Message, MessageViewHolder> adapter;

    public ChatFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getParentFragment() != null) {

            chatModel = new ViewModelProvider(requireParentFragment()).get(ChatViewModel.class);

            for (LoggedInUser member: chatModel.getMembers().getValue()) {
                membersDisplayName.put(member.getId(), member.getDisplayName());
            }
            chatId = chatModel.getIdChat();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false);

        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);

        // Initialize the RecyclerView and set an adapter for displaying the chat messages
        chatRecyclerView = binding.chatRecyclerView;
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(linearLayoutManager);
        setupAdapter();

        // Get a reference to the message input field and send button
        messageEditText = binding.messageEditText;
        FloatingActionButton sendButton = binding.sendButton;
        arrowNotification = binding.newMessageArrow;

        // Set a click listener for the send button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the message from the input field
                String message = messageEditText.getText().toString();

                // Add the message to the Firebase database
                addMessage(message);

                // Clear the input field
                messageEditText.setText("");
            }
        });

        //animation for arrow notifications
        ObjectAnimator animator = ObjectAnimator.ofFloat(arrowNotification, "translationY", 0, 20);
        animator.setDuration(500);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();

        arrowNotification.setOnClickListener(view -> {
            chatRecyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
            arrowNotification.setVisibility(View.GONE);
            arrowNotification.clearAnimation();
        });

        adapter.startListening();

        return binding.getRoot();
    }

    private void setupAdapter() {
        // Query the Firebase database for the chat messages
        Query query = FirebaseDatabase.getInstance().getReference().child("messages").child(chatId).orderByChild("timestamp");

        FirebaseRecyclerOptions<Message> options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .build();

        // Set up the FirebaseRecyclerAdapter to display the chat messages
        adapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(options) {

            private static final int VIEW_TYPE_MESSAGE_SENT = 1;
            private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
            private static final int FIRST_MESSAGE_SENT = 3;
            private static final int FIRST_MESSAGE_RECEIVED = 4;

            @Override
            public void onDataChanged() {
                super.onDataChanged();

                int lastMessagePosition = this.getItemCount() - 1;
                int viewType = getItemViewType(lastMessagePosition);
                int lastVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisibleItemPosition == (lastMessagePosition -1) || lastVisibleItemPosition == -1) {
                    chatRecyclerView.scrollToPosition(lastMessagePosition);
                    return;
                }

                if (viewType == VIEW_TYPE_MESSAGE_RECEIVED || viewType == FIRST_MESSAGE_RECEIVED) {
                    arrowNotification.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public int getItemViewType(int position) {

                Message message = this.getItem(position);

                try {
                    Message messagePrev = this.getItem(position - 1);
                    DateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

                    if (dayFormat.format(messagePrev.getTimestamp()).equals(dayFormat.format(message.getTimestamp()))) {
                        if (message.getSenderId().equals(FirebaseAuth.getInstance().getUid())) {
                            return VIEW_TYPE_MESSAGE_SENT;
                        }
                        return VIEW_TYPE_MESSAGE_RECEIVED;
                    }

                    if (message.getSenderId().equals(FirebaseAuth.getInstance().getUid())) {
                        return FIRST_MESSAGE_SENT;
                    }
                    return FIRST_MESSAGE_RECEIVED;

                } catch (Exception e) {
                    if (message.getSenderId().equals(FirebaseAuth.getInstance().getUid())) {
                        return FIRST_MESSAGE_SENT;
                    }
                    return FIRST_MESSAGE_RECEIVED;
                }
            }

            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, using the layout file for chat message items
                View view;
                switch (viewType) {
                    case FIRST_MESSAGE_SENT:
                        view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_message_me_date, parent, false);

                        return new SendMessageViewHolder(view, true);
                    case VIEW_TYPE_MESSAGE_SENT:
                        view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_message_me, parent, false);

                        return new SendMessageViewHolder(view);
                    case FIRST_MESSAGE_RECEIVED:
                        view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_message_other_date, parent, false);

                        return new ReceiveMessageHolder(view, true);
                    case VIEW_TYPE_MESSAGE_RECEIVED:
                        view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_message_other, parent, false);

                        return new ReceiveMessageHolder(view);
                    default:
                        return null;
                }
            }

            @Override
            protected void onBindViewHolder(MessageViewHolder holder, int position, Message model) {
                // Bind the data for the message to the ViewHolder
                holder.bind(model, getDisplayNameOfMember(model.getSenderId()), null);
            }
        };
        chatRecyclerView.setAdapter(adapter);

        chatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (linearLayoutManager != null) {
                    int lastVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    int itemCount = linearLayoutManager.getItemCount();
                    if (lastVisibleItemPosition == itemCount - 1) {
                        arrowNotification.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void addMessage(String messageText) {
        // Create a new message object with the current user's ID and the message text
        Message message = new Message(FirebaseAuth.getInstance().getCurrentUser().getUid(), messageText);
        // Add the message to the Firebase database
        FirebaseDatabase.getInstance().getReference().child("messages").child(chatId).push().setValue(message);
    }

    private String getDisplayNameOfMember(String id) {
        return membersDisplayName.getOrDefault(id, null);
    }

    @Override
    public void onStop() {
        super.onStop();
        chatModel.setIdChat(null);
        chatModel.getMembers().setValue(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        navBar.setVisibility(View.VISIBLE);
        adapter.stopListening();

        navBar = null;
        adapter = null;
        binding = null;

    }
}



