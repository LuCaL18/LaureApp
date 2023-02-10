package com.uniba.mobile.cddgl.laureapp.ui.ticket;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.MainViewModel;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.NotificationType;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.TicketState;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.Ticket;
import com.uniba.mobile.cddgl.laureapp.util.BaseRequestNotification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TicketFragment extends Fragment {

    public static final String TICKET_KEY = "new_ticket";

    private BottomNavigationView navBar;
    private Ticket ticket;
    private LoggedInUser user;
    private TicketViewModel ticketViewModel;
    private View root;
    private Button sendButton;

    public TicketFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider viewModelProvider = new ViewModelProvider(requireParentFragment());
        ticketViewModel = viewModelProvider.get(TicketViewModel.class);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        user = mainViewModel.getUser().getValue();

        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);

        if(getArguments() != null) {
            ticket = (Ticket) getArguments().getSerializable(TICKET_KEY);

            return;
        }

        ticket = ticketViewModel.getTicket().getValue();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_ticket, container, false);

        TextView titleTicket = root.findViewById(R.id.text_title_ticket);
        TextView emailStudentTicket = root.findViewById(R.id.text_view_recipient_email);
        TextView emailProfTicket = root.findViewById(R.id.text_view_email_prof);

        titleTicket.setText(ticket.getNameTesi() + " - " + ticket.getIdTesi());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users")
                .child(user.getRole().equals(RoleUser.STUDENT) ? ticket.getIdReceiver() : ticket.getIdSender())
                .child("email");

        reference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                String email = task.getResult().getValue(String.class);

                if(user.getRole().equals(RoleUser.STUDENT)) {
                    emailStudentTicket.setText(getString(R.string.email_student_title, user.getEmail()));
                    emailProfTicket.setText(getString(R.string.email_prof_title, email));
                } else {
                    emailStudentTicket.setText(getString(R.string.email_student_title, email));
                    emailProfTicket.setText(getString(R.string.email_prof_title, user.getEmail()));
                }
            }
        });

        if (ticket.getState().equals(TicketState.NEW)) {
            renderNewTicket(root);
        } else if (ticket.getState().equals(TicketState.OPEN)) {
            renderOpenTicket(root);
        } else {
            renderClosedTicket(root);
        }

        this.root = root;
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ticketViewModel.setAlreadyRead(true);
    }

    private void renderNewTicket(View root) {
        EditText senderEditText = root.findViewById(R.id.text_sender_ticket);

        EditText receiverText = root.findViewById(R.id.text_receiver_ticket);
        receiverText.setVisibility(View.GONE);

        Button sendButton = root.findViewById(R.id.button_send);

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                String senderText = senderEditText.getText().toString();

                Pattern regex = Pattern.compile("^\\S.*", Pattern.DOTALL);
                Matcher regexMatcher = regex.matcher(senderText);

                if (regexMatcher.find()) {
                    sendButton.setEnabled(true);
                    return;
                }
                sendButton.setEnabled(false);
            }
        };

        senderEditText.addTextChangedListener(afterTextChangedListener);

        sendButton.setOnClickListener(v -> showConfirmDialog(getString(R.string.message_dialog_ticket_request)));
    }

    private void renderOpenTicket(View root) {

        TextView timestampSender = root.findViewById(R.id.timestamp_ticket_sender);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date date = new Date(ticket.getTimestampSender());
        timestampSender.setText(formatter.format(date));
        timestampSender.setVisibility(View.VISIBLE);

        EditText senderEditText = root.findViewById(R.id.text_sender_ticket);
        senderEditText.setVisibility(View.GONE);

        TextView senderTextView = root.findViewById(R.id.textView_sender_ticket);
        senderTextView.setText(ticket.getTextSender());
        senderTextView.setVisibility(View.VISIBLE);

        EditText receiverText = root.findViewById(R.id.text_receiver_ticket);
        sendButton = root.findViewById(R.id.button_send);

        if (user.getRole().equals(RoleUser.STUDENT)) {
            sendButton.setVisibility(View.GONE);
            return;
        }

        receiverText.setVisibility(View.VISIBLE);

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                String receiver = receiverText.getText().toString();

                Pattern regex = Pattern.compile("^\\S.*", Pattern.DOTALL);
                Matcher regexMatcher = regex.matcher(receiver);

                if (regexMatcher.find()) {
                    sendButton.setEnabled(true);
                    return;
                }
                sendButton.setEnabled(false);
            }
        };

        receiverText.addTextChangedListener(afterTextChangedListener);

        receiverText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(receiverText, InputMethodManager.SHOW_IMPLICIT);

        sendButton.setOnClickListener(v -> showConfirmDialog(getString(R.string.message_dialog_ticket_response)));
    }

    private void renderClosedTicket(View root) {

        TextView timestampSender = root.findViewById(R.id.timestamp_ticket_sender);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date date = new Date(ticket.getTimestampSender());
        timestampSender.setVisibility(View.VISIBLE);
        timestampSender.setText(formatter.format(date));

        EditText senderEditText = root.findViewById(R.id.text_sender_ticket);
        senderEditText.setVisibility(View.GONE);

        TextView senderTextView = root.findViewById(R.id.textView_sender_ticket);
        senderTextView.setText(ticket.getTextSender());
        senderTextView.setVisibility(View.VISIBLE);

        TextView timestampReceiver = root.findViewById(R.id.timestamp_ticket_receiver);
        Date dateReceiver = new Date(ticket.getTimestampReceiver());
        timestampReceiver.setVisibility(View.VISIBLE);
        timestampReceiver.setText(formatter.format(dateReceiver));

        TextView receiverTextView = root.findViewById(R.id.text_view_receiver_ticket);
        receiverTextView.setText(ticket.getTextReceiver());
        receiverTextView.setVisibility(View.VISIBLE);

        root.findViewById(R.id.button_send).setVisibility(View.GONE);
    }

    private void showConfirmDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.title_dialog_ticket));
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.yes_text), (dialog, which) -> sendTicket());
        builder.setNegativeButton(getString(R.string.no_text), null);
        builder.create().show();
    }

    @Override
    public void onResume() {
        super.onResume();

        navBar.setVisibility(View.GONE);

        EditText senderText = root.findViewById(R.id.text_sender_ticket);
        if(senderText.getVisibility() == View.VISIBLE) {
            senderText.requestFocus();
//
//            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(senderText, InputMethodManager.SHOW_IMPLICIT);
        }

        EditText receiverText = root.findViewById(R.id.text_receiver_ticket);
        if(receiverText.getVisibility() == View.VISIBLE) {
            receiverText.requestFocus();
//
//            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(receiverText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void sendTicket() {

        if(ticket.getState().equals(TicketState.NEW)) {
            sendTicketRequest();
        } else {
            sendTicketResponse();
        }
    }

    private void sendTicketRequest() {

        EditText senderText = root.findViewById(R.id.text_sender_ticket);
        TextView timestampTextView = root.findViewById(R.id.timestamp_ticket_sender);
        TextView senderTextView = root.findViewById(R.id.textView_sender_ticket);


        String requestText = senderText.getText().toString();
        long timestamp = new Date().getTime();

        ticket.setTextSender(requestText);
        ticket.setTimestampSender(timestamp);
        ticket.setState(TicketState.OPEN);

        CollectionReference ticketsCollection = FirebaseFirestore.getInstance().collection("tickets");
        ticketsCollection.add(ticket).addOnCompleteListener(task -> {
           if(task.isSuccessful()) {
               DocumentReference documentReference = task.getResult();
               ticket.setId(documentReference.getId());

               senderText.setVisibility(View.GONE);

               SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
               Date date = new Date(timestamp);
               timestampTextView.setText(formatter.format(date));
               timestampTextView.setVisibility(View.VISIBLE);

               senderTextView.setText(requestText);
               senderTextView.setVisibility(View.VISIBLE);

               Map<String, Object> updates = new HashMap<>();
               updates.put("id", ticket.getId());
               ticketsCollection.document(documentReference.getId()).update(updates);

               sendButton.setVisibility(View.GONE);

               sendNotification(ticket.getIdReceiver());
           } else {
               showToastError(getString(R.string.notification_ticket_request_exception));
           }
        });

    }

    private void sendTicketResponse() {
        EditText receiverEditText = root.findViewById(R.id.text_receiver_ticket);
        TextView timestampTextView = root.findViewById(R.id.timestamp_ticket_receiver);
        TextView receiverTextView = root.findViewById(R.id.text_view_receiver_ticket);

        String responseText = receiverEditText.getText().toString();
        long timestamp = new Date().getTime();

        ticket.setTextReceiver(responseText);
        ticket.setTimestampReceiver(timestamp);
        ticket.setState(TicketState.CLOSED);

        CollectionReference ticketsCollection = FirebaseFirestore.getInstance().collection("tickets");

        Map<String, Object> updates = new HashMap<>();
        updates.put("textReceiver", responseText);
        updates.put("timestampReceiver", timestamp);
        updates.put("state", TicketState.CLOSED.name());

        ticketsCollection.document(ticket.getId()).update(updates).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                receiverEditText.setVisibility(View.GONE);

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                Date date = new Date(timestamp);
                timestampTextView.setText(formatter.format(date));
                timestampTextView.setVisibility(View.VISIBLE);

                receiverTextView.setText(responseText);
                receiverTextView.setVisibility(View.VISIBLE);

                sendButton.setVisibility(View.GONE);
                sendNotification(ticket.getIdSender());
            } else {
                showToastError(getString(R.string.notification_ticket_response_exception));
            }
        });


    }

    private void sendNotification(String receiverId) {
        BaseRequestNotification notification = new BaseRequestNotification(receiverId, NotificationType.TICKET);

        String title = getString(R.string.title_notification_ticket, ticket.getNameTesi());
        String message;
        String body;

        if(ticket.getState().equals(TicketState.OPEN)) {
            body = "open_ticket";
            message = getString(R.string.notification_body_open_ticket, user.getEmail(), ticket.getNameTesi());
        } else {
            body = "closed_ticket";
            message = getString(R.string.notification_body_closed_ticket, user.getEmail(), ticket.getNameTesi());
        }

        notification.setNotification(title, message);

        Map<String, Object> data = new HashMap<>();
        data.put("receiveId", receiverId);
        data.put("type", notification.getType());
        data.put("senderName", user.getEmail());
        data.put("body", body);
        data.put("ticketId", ticket.getId());
        data.put("nameChat", ticket.getNameTesi());
        data.put("timestamp", System.currentTimeMillis());
        notification.addData(data);

        notification.sendRequest(Request.Method.POST, this.getContext());

    }

    private void showToastError(String error) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(getContext().getApplicationContext(), error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ticketViewModel.getTicket().setValue(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        navBar.setVisibility(View.VISIBLE);
    }
}