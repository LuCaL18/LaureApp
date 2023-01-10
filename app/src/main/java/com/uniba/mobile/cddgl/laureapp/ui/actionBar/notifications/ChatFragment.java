package com.uniba.mobile.cddgl.laureapp.ui.actionBar.notifications;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Message;
import com.uniba.mobile.cddgl.laureapp.databinding.FragmentChatBinding;
import com.uniba.mobile.cddgl.laureapp.ui.actionBar.notifications.viewHolder.MessageViewHolder;
import com.uniba.mobile.cddgl.laureapp.ui.actionBar.notifications.viewHolder.ReceiveMessageHolder;
import com.uniba.mobile.cddgl.laureapp.ui.actionBar.notifications.viewHolder.SendMessageViewHolder;


public class ChatFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";

    private FragmentChatBinding binding;
    private String userId;
    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private FloatingActionButton sendButton;
    private BottomNavigationView navBar;

    private FirebaseRecyclerAdapter<Message, MessageViewHolder> adapter;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String userId) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false);

        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);

        // Initialize the RecyclerView and set an adapter for displaying the chat messages
        chatRecyclerView = binding.chatRecyclerView;
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setupAdapter();

        // Get a reference to the message input field and send button
        messageEditText = binding.messageEditText;
        sendButton = binding.sendButton;

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

        adapter.startListening();

        return binding.getRoot();
    }

    private void setupAdapter() {
        // Query the Firebase database for the chat messages
        Query query = FirebaseDatabase.getInstance().getReference().child("messages").orderByChild("timestamp");

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
            public int getItemViewType(int position) {

                Message message = this.getItem(position);

                try {
                    Message messagePrev = this.getItem(position - 1);

                    if(messagePrev.getDayMessage().equals(message.getDayMessage())) {
                        if (message.getSenderId().equals(FirebaseAuth.getInstance().getUid())) {
                            return VIEW_TYPE_MESSAGE_SENT;
                        }
                        return VIEW_TYPE_MESSAGE_RECEIVED;
                    }

                    if (message.getSenderId().equals(FirebaseAuth.getInstance().getUid())) {
                        return FIRST_MESSAGE_SENT;
                    }
                    return FIRST_MESSAGE_RECEIVED;

                }catch (Exception e){
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
                holder.bind(model);
            }
        };
        chatRecyclerView.setAdapter(adapter);
    }

    private void addMessage(String messageText) {
        // Create a new message object with the current user's ID and the message text
        Message message = new Message(FirebaseAuth.getInstance().getCurrentUser().getUid(), messageText);

        // Add the message to the Firebase database
        FirebaseDatabase.getInstance().getReference().child("messages").push().setValue(message);

        // Scroll the RecyclerView to the bottom to show the new message

        chatRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navBar.setVisibility(View.VISIBLE);
        navBar = null;
        adapter.stopListening();
        adapter = null;

    }
}



