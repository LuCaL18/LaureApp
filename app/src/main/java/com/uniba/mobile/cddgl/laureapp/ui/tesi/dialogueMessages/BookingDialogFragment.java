package com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogueMessages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.BookingConstraints;
import com.uniba.mobile.cddgl.laureapp.data.BookingState;
import com.uniba.mobile.cddgl.laureapp.data.NotificationType;
import com.uniba.mobile.cddgl.laureapp.data.TicketState;
import com.uniba.mobile.cddgl.laureapp.data.model.Booking;
import com.uniba.mobile.cddgl.laureapp.util.BaseRequestNotification;

import java.util.HashMap;
import java.util.Map;

public class BookingDialogFragment extends DialogFragment {

    private final String studentId, email, nameStudent, surnameStudent, professorId, idThesis, nameThesis;

    public BookingDialogFragment(String studentId, String emailStudent, String nameStudent, String surnameStudent, String professorId, String idThesis, String nameThesis) {
        this.studentId = studentId;
        this.email = emailStudent;
        this.nameStudent = nameStudent;
        this.surnameStudent = surnameStudent;
        this.professorId = professorId;
        this.idThesis = idThesis;
        this.nameThesis = nameThesis;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_booking, container, false);

        TextInputEditText emailText = view.findViewById(R.id.email_booking);
        emailText.setText(email);

        TextInputEditText nameText = view.findViewById(R.id.first_name_booking);
        nameText.requestFocus();
        nameText.setText(nameStudent);

        TextInputEditText surnameText = view.findViewById(R.id.last_name_booking);
        surnameText.setText(surnameStudent);

        CheckBox mTimeline = view.findViewById(R.id.timeline_booking);
        CheckBox mSkill = view.findViewById(R.id.skill_booking);
        CheckBox mExam = view.findViewById(R.id.exam_booking);
        CheckBox mGPA = view.findViewById(R.id.votes_booking);
        EditText mJustification = view.findViewById(R.id.justification_constraints_booking);
        TextView tJustificationTitle = view.findViewById(R.id.justification_constraints_booking_title);

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!mTimeline.isChecked() || !mSkill.isChecked() || !mExam.isChecked() || !mGPA.isChecked()) {
                    mJustification.setVisibility(View.VISIBLE);
                    tJustificationTitle.setVisibility(View.VISIBLE);

                } else {
                    mJustification.setVisibility(View.GONE);
                    tJustificationTitle.setVisibility(View.GONE);
                }
            }
        };

        mTimeline.setOnCheckedChangeListener(listener);
        mSkill.setOnCheckedChangeListener(listener);
        mExam.setOnCheckedChangeListener(listener);
        mGPA.setOnCheckedChangeListener(listener);

        Button cancel = view.findViewById(R.id.cancel_booking);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button send = view.findViewById(R.id.send_booking);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String justification = mJustification.getText().toString();
                BookingConstraints constraints = new BookingConstraints(mTimeline.isChecked(), mGPA.isChecked(), mExam.isChecked(), mSkill.isChecked());

                Booking booking = new Booking(studentId, professorId, email, nameStudent, surnameStudent, idThesis, nameThesis, constraints, justification);

                CollectionReference bookCollection = FirebaseFirestore.getInstance().collection("bookings");
                bookCollection.document(booking.getId()).set(booking).addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        showSaveToast(R.string.booking_successfully_saved);
                        sendNotification(professorId, booking);
                        dismiss();

                        return;
                    }

                    showSaveToast(R.string.impossible_to_save_the_booking);
                });
            }
        });

        return view;
    }

    private void sendNotification(String receiverId, Booking booking) {
        BaseRequestNotification notification = new BaseRequestNotification(receiverId, NotificationType.BOOKING);

        String title = getString(R.string.notification_title_booking, booking.getNameThesis());
        String message;
        String body;

        body = "booking_opened";
        message = getString(R.string.notification_body_open_booking, booking.getNameStudent()+ " " + booking.getSurnameStudent(), booking.getNameThesis());

        notification.setNotification(title, message);

        Map<String, Object> data = new HashMap<>();
        data.put("receiveId", receiverId);
        data.put("type", notification.getType());
        data.put("senderName", booking.getNameStudent() + " " + booking.getSurnameStudent());
        data.put("body", body);
        data.put("ticketId", booking.getId());
        data.put("nameChat", booking.getNameThesis());
        data.put("timestamp", System.currentTimeMillis());
        notification.addData(data);

        notification.sendRequest(Request.Method.POST, this.getContext());

    }


    private void showSaveToast(@StringRes Integer message) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    message,
                    Toast.LENGTH_LONG).show();
        }
    }


}
