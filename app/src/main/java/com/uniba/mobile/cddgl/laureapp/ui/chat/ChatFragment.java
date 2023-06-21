package com.uniba.mobile.cddgl.laureapp.ui.chat;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.uniba.mobile.cddgl.laureapp.MainViewModel;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.NotificationType;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.Message;
import com.uniba.mobile.cddgl.laureapp.databinding.FragmentChatBinding;
import com.uniba.mobile.cddgl.laureapp.ui.chat.viewHolder.MessageViewHolder;
import com.uniba.mobile.cddgl.laureapp.ui.chat.viewHolder.ReceiveMessageHolder;
import com.uniba.mobile.cddgl.laureapp.ui.chat.viewHolder.SendMessageViewHolder;
import com.uniba.mobile.cddgl.laureapp.util.BaseRequestNotification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Fragment che si occupa della visualizzazione e della gestione di una chat
 */
public class ChatFragment extends Fragment {

    private final static String CLASS_NAME = "ChatFragment";
    private final static String MEMBERS_KEY = "members";
    private final static String CHAT_ID_KEY = "chat_id";

    private FragmentChatBinding binding;
    private String chatId = "";
    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ImageView arrowNotification;
    private LinearLayoutManager linearLayoutManager;
    private final Map<String, LoggedInUser> otherMembers = new HashMap<>();
    private final List<BaseRequestNotification> notificationsList = new ArrayList<>();
    private ChatViewModel chatModel;
    private LoggedInUser currentUser;
    private BottomNavigationView navBar;
    private boolean isFirstChanged;

    private FirebaseRecyclerAdapter<Message, MessageViewHolder> adapter;

    public ChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getParentFragment() != null) {

            if(savedInstanceState != null && savedInstanceState.getSerializable(MEMBERS_KEY) != null) {
                LoggedInUser[] loggedInUsers = (LoggedInUser[]) savedInstanceState.getSerializable(MEMBERS_KEY);

                for (LoggedInUser member : loggedInUsers) {

                    if(!member.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        otherMembers.put(member.getId(), member);
                        notificationsList.add(new BaseRequestNotification(member.getId(), NotificationType.MESSAGE));
                    }
                }

                chatId = savedInstanceState.getString(CHAT_ID_KEY);

                return;
            }

            chatModel = new ViewModelProvider(requireParentFragment()).get(ChatViewModel.class);

            for (LoggedInUser member : chatModel.getMembers().getValue()) {

                if(!member.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    otherMembers.put(member.getId(), member);
                    notificationsList.add(new BaseRequestNotification(member.getId(), NotificationType.MESSAGE));
                }
            }
            chatId = chatModel.getIdChat();

            isFirstChanged = true;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false);

        super.onViewCreated(binding.getRoot(), savedInstanceState);

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        currentUser = mainViewModel.getUser().getValue();

        mainViewModel.getUser().observe(getViewLifecycleOwner(), loggedInUser -> currentUser = loggedInUser);
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
                if(isFirstChanged && getView() != null) {
                    LinearLayout loadingLayout = getView().findViewById(R.id.loading_chat);
                    loadingLayout.setVisibility(View.GONE);
                }
                isFirstChanged = false;

                int lastMessagePosition = this.getItemCount() - 1;

                if(lastMessagePosition < 0) {
                    return;
                }

                int viewType = getItemViewType(lastMessagePosition);
                int lastVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisibleItemPosition == (lastMessagePosition - 1) || lastVisibleItemPosition == -1) {
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

            @NonNull
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
                        view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_message_me_date, parent, false);
                        return new MessageViewHolder(view);
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, Message model) {
                // Bind the data for the message to the ViewHolder
                holder.bind(model, getDisplayNameOfMember(model.getSenderId()), getPhotoUrlOfMember(model.getSenderId()));
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

        sendNotification(messageText);
    }

    private String getDisplayNameOfMember(String id) {
        try {
            return otherMembers.getOrDefault(id, null).getDisplayName();
        } catch (NullPointerException e) {
            Log.d(CLASS_NAME, "Unable fetch display name of member " + id);
            return null;
        }
    }

    private String getPhotoUrlOfMember(String id) {
        try {
            return otherMembers.getOrDefault(id, null).getPhotoUrl();
        } catch (NullPointerException e) {
            Log.d(CLASS_NAME, "Unable fetch photo url of member " + id);
            return null;
        }
    }

    private List<String> getTokenOfMember() {

        List<String> tokens = new ArrayList<>();
        try {
            for (String id : otherMembers.keySet()) {
                tokens.add(otherMembers.get(id).getToken());
            }
        } catch (NullPointerException e) {
            Log.e(CLASS_NAME, "Unable fetch tokens of member");
        }
        return tokens;
    }

    private void sendNotification(String message) {

        for (BaseRequestNotification notification : notificationsList) {
            String title = getString(R.string.title_message, currentUser.getDisplayName());
            notification.setNotification(title, message);

            Map<String, Object> data = new HashMap<>();
            data.put("receiveId", notification.getReceiveId());
            data.put("type", notification.getType());
            data.put("senderName", currentUser.getDisplayName());
            data.put("body", message);
            data.put("chatId", chatId);
            data.put("nameChat", chatModel.getNameChat());
            data.put("timestamp", System.currentTimeMillis());
            notification.addData(data);

            notification.sendRequest(Request.Method.POST, this.getContext());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(MEMBERS_KEY, chatModel.getMembers().getValue());
        savedInstanceState.putString(CHAT_ID_KEY, chatId);
    }

    @Override
    public void onStop() {
        super.onStop();

        if( chatModel != null) {
            chatModel.setIdChat(null);
            chatModel.getMembers().setValue(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.stopListening();
        navBar.setVisibility(View.VISIBLE);

        adapter = null;
        binding = null;

    }
}