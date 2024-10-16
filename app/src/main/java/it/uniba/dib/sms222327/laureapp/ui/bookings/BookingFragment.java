package it.uniba.dib.sms222327.laureapp.ui.bookings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Request;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms222327.laureapp.MainViewModel;
import it.uniba.dib.sms222327.laureapp.R;
import it.uniba.dib.sms222327.laureapp.data.BookingConstraints;
import it.uniba.dib.sms222327.laureapp.data.BookingState;
import it.uniba.dib.sms222327.laureapp.data.NotificationType;
import it.uniba.dib.sms222327.laureapp.data.PersonaTesi;
import it.uniba.dib.sms222327.laureapp.data.RoleUser;
import it.uniba.dib.sms222327.laureapp.data.model.Booking;
import it.uniba.dib.sms222327.laureapp.data.model.ChatData;
import it.uniba.dib.sms222327.laureapp.data.model.LoggedInUser;
import it.uniba.dib.sms222327.laureapp.data.model.Tesi;
import it.uniba.dib.sms222327.laureapp.util.BaseRequestNotification;

public class BookingFragment extends Fragment {

    private static final String CLASS_ID = "BookingFragment";

    private static final int DELETE = R.id.delete_booking;
    private static final String BOOKING_KEY = "booking";

    private BottomNavigationView navBar;
    private Booking booking;
    private LoggedInUser user;
    private BookingViewModel bookingViewModel;
    private TextView resultBooking;
    private LinearLayout buttonsLinearLayout;
    private MenuProvider providerMenu;

    public BookingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider viewModelProvider = new ViewModelProvider(requireParentFragment());
        bookingViewModel = viewModelProvider.get(BookingViewModel.class);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        user = mainViewModel.getUser().getValue();

        if (savedInstanceState != null && savedInstanceState.getSerializable(BOOKING_KEY) != null) {
            booking = (Booking) savedInstanceState.getSerializable(BOOKING_KEY);

            return;
        }

        booking = bookingViewModel.getBooking().getValue();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_booking, container, false);

        TextView titleBooking = root.findViewById(R.id.text_title_booking);
        TextView emailStudent = root.findViewById(R.id.text_view_recipient_email);
        TextView fullNameStudent = root.findViewById(R.id.text_view_full_name_student);

        titleBooking.setText(booking.getNameThesis() + " - " + booking.getIdThesis());
        emailStudent.setText(booking.getEmailStudent());
        fullNameStudent.setText(booking.getNameStudent() + " " + booking.getSurnameStudent());

        TextView timelinesTextView = root.findViewById(R.id.timeline_booking);
        TextView averageGradeTextView = root.findViewById(R.id.votes_booking);
        TextView skillTextView = root.findViewById(R.id.skill_booking);
        TextView examTextView = root.findViewById(R.id.exam_booking);

        BookingConstraints constraints = booking.getConstraints();


        if (constraints.isTimelines()) {
            timelinesTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_circle_outline_24, 0, 0, 0);
        } else {
            timelinesTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_cancel_24, 0, 0, 0);
        }

        if (constraints.isAverageGrade()) {
            averageGradeTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_circle_outline_24, 0, 0, 0);
        } else {
            averageGradeTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_cancel_24, 0, 0, 0);
        }

        if (constraints.isNecessaryExam()) {
            examTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_circle_outline_24, 0, 0, 0);
        } else {
            examTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_cancel_24, 0, 0, 0);
        }

        if (constraints.isSkills()) {
            skillTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_circle_outline_24, 0, 0, 0);
        } else {
            skillTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_cancel_24, 0, 0, 0);
        }

        if ((!constraints.isTimelines() || !constraints.isAverageGrade() || !constraints.isNecessaryExam() || !constraints.isSkills()) && booking.getMotivation() != null) {
            root.findViewById(R.id.justification_constraints_booking_title).setVisibility(View.VISIBLE);

            TextView justificationText = root.findViewById(R.id.justification_constraints_booking);
            justificationText.setVisibility(View.VISIBLE);

            justificationText.setText(booking.getMotivation());
        }

        Button rejectButton = root.findViewById(R.id.reject_booking);
        Button acceptButton = root.findViewById(R.id.accept_booking);

        buttonsLinearLayout = root.findViewById(R.id.buttons_response_booking);

        if (user.getRole().equals(RoleUser.PROFESSOR) && booking.getState().equals(BookingState.OPEN)) {
            buttonsLinearLayout.setVisibility(View.VISIBLE);
        }

        resultBooking = root.findViewById(R.id.result_booking);

        if (booking.getState().equals(BookingState.ACCEPTED)) {

            resultBooking.setVisibility(View.VISIBLE);
            if (user.getRole().equals(RoleUser.PROFESSOR)) {
                resultBooking.setText(R.string.you_have_accepted_this_booking);
            } else {
                resultBooking.setText(R.string.your_booking_has_been_accepted);
                resultBooking.setTextColor(getResources().getColor(R.color.message_green));
            }
        }

        if (booking.getState().equals(BookingState.REFUSED)) {

            resultBooking.setVisibility(View.VISIBLE);
            if (user.getRole().equals(RoleUser.PROFESSOR)) {
                resultBooking.setText(R.string.you_have_refused_this_booking);
            } else {
                resultBooking.setText(R.string.your_booking_has_been_refused);
                resultBooking.setTextColor(getResources().getColor(com.google.android.material.R.color.design_default_color_error));
            }
        }

        rejectButton.setOnClickListener(view -> showConfirmDialog(getString(R.string.refuse_booking_dialog), BookingState.REFUSED));
        acceptButton.setOnClickListener(view -> showConfirmDialog(getString(R.string.accept_booking_dialog), BookingState.ACCEPTED));

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Impostazione del comportamento quando il fragment viene visualizzato
        bookingViewModel.setAlreadyRead(true);
        NavController navController = NavHostFragment.findNavController(this);

        if (user.getRole().equals(RoleUser.STUDENT) && booking.getState().equals(BookingState.OPEN)) {
            // Configurazione del menu per gli studenti
            providerMenu = new MenuProvider() {
                @Override
                public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                    menu.clear();
                    menuInflater.inflate(R.menu.app_bar_booking, menu);
                }

                @Override
                public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                    if (menuItem.getItemId() == DELETE) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(getString(R.string.delete_booking));
                        builder.setMessage(getString(R.string.delete_booking_dialog_message));

                        builder.setPositiveButton(getString(R.string.yes_text), ((dialogInterface, i) -> {
                            FirebaseFirestore.getInstance().collection("bookings")
                                    .document(booking.getId())
                                    .delete()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            showToast(getString(R.string.booking_successfully_cancelled));
                                            navController.popBackStack();
                                            return;
                                        }

                                        showToast(getString(R.string.unable_to_delete_the_booking));
                                    });
                        }));

                        builder.setNegativeButton(getString(R.string.no_text), null);
                        builder.create().show();
                    }

                    return false;
                }
            };

            requireActivity().addMenuProvider(providerMenu);
        }
    }


    /**
     * Visualizzazione di un dialog di conferma per la scelta dell'utente
     *
     * @param message
     * @param newState
     */
    private void showConfirmDialog(String message, BookingState newState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.title_booking_dialog_confirm));
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.yes_text), (dialog, which) -> {

            if (newState.equals(BookingState.ACCEPTED)) {
                sendAcceptedResponse();
                return;
            }

            sendRefusedResponse();
        });
        builder.setNegativeButton(getString(R.string.no_text), null);
        builder.create().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Configurazione della visualizzazione della barra di navigazione
        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);
    }

    /**
     * Invio della risposta di accettazione della prenotazione al database
     * Invio di una notifica al destinatario
     * Aggiunta dello studente alla tesi
     */
    private void sendAcceptedResponse() {
        booking.setState(BookingState.ACCEPTED);
        Map<String, Object> updates = new HashMap<>();

        updates.put("state", BookingState.ACCEPTED);
        updates.put("timestamp", System.currentTimeMillis());

        FirebaseFirestore.getInstance().collection("bookings")
                .document(booking.getId())
                .update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast(getString(R.string.response_sent_successfully));
                        sendNotification(booking.getStudentId());

                        addStudentToThesis();
                        return;
                    }

                    showToast(getString(R.string.unable_to_send_response));
                });
    }

    /**
     * Invio della risposta di rifiuto della prenotazione al database
     * Invio di una notifica al destinatario
     * Aggiornamento della vista con il risultato del rifiuto
     */
    private void sendRefusedResponse() {

        booking.setState(BookingState.REFUSED);
        Map<String, Object> updates = new HashMap<>();

        updates.put("state", BookingState.REFUSED);
        updates.put("timestamp", System.currentTimeMillis());

        FirebaseFirestore.getInstance().collection("bookings")
                .document(booking.getId())
                .update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast(getString(R.string.response_sent_successfully));
                        sendNotification(booking.getStudentId());

                        resultBooking.setText(getText(R.string.you_have_refused_this_booking));
                        resultBooking.setTextColor(getResources().getColor(com.google.android.material.R.color.design_default_color_error));
                        resultBooking.setVisibility(View.VISIBLE);

                        buttonsLinearLayout.setVisibility(View.GONE);
                        return;
                    }

                    showToast(getString(R.string.unable_to_send_response));
                });
    }

    private void sendNotification(String receiverId) {
        // Invio di una notifica al destinatario
        BaseRequestNotification notification = new BaseRequestNotification(receiverId, NotificationType.BOOKING);

        String title = getString(R.string.notification_title_booking, booking.getNameThesis());
        String message;
        String body;

        if (booking.getState().equals(BookingState.ACCEPTED)) {
            body = "accepted_booking";
            message = getString(R.string.your_booking_has_been_accepted);
        } else {
            body = "refused_booking";
            message = getString(R.string.your_booking_has_been_refused);
        }

        notification.setNotification(title, message);

        Map<String, Object> data = new HashMap<>();
        data.put("receiveId", receiverId);
        data.put("type", notification.getType());
        data.put("senderName", user.getName() + " " + user.getSurname());
        data.put("body", body);
        data.put("ticketId", booking.getId());
        data.put("nameChat", booking.getNameThesis());
        data.put("timestamp", System.currentTimeMillis());
        notification.addData(data);

        notification.sendRequest(Request.Method.POST, this.getContext());

    }

    private void showToast(String message) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(getContext().getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Aggiunta dello studente alla tesi nel database.
     * Aggiunta dello studente alla chat relativa alla tesi
     */
    private void addStudentToThesis() {
        try {

            Map<String, Object> updates = new HashMap<>();
            updates.put("isAssigned", true);
            updates.put("student", new PersonaTesi(booking.getStudentId(), booking.getNameStudent() + " " + booking.getSurnameStudent(), booking.getEmailStudent(), null));

            CollectionReference thesisCollection = FirebaseFirestore.getInstance().collection("tesi");
            thesisCollection.document(booking.getIdThesis()).update(updates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    addStudentToChat();

                    resultBooking.setText(getText(R.string.you_have_accepted_this_booking));
                    resultBooking.setTextColor(getResources().getColor(R.color.message_green));
                    resultBooking.setVisibility(View.VISIBLE);

                    buttonsLinearLayout.setVisibility(View.GONE);
                } else {
                    showToast(getString(R.string.unable_to_assign_the_thesis));
                    rollBackAcceptedResponse();
                }
            });

        } catch (Exception e) {
            Log.e("BookingFragment", e.getMessage());
            showToast(getString(R.string.unable_to_assign_the_thesis));
            rollBackAcceptedResponse();
        }
    }

    private void rollBackAcceptedResponse() {
        // Annullamento della risposta di accettazione
        booking.setState(BookingState.OPEN);
        Map<String, Object> updates = new HashMap<>();

        updates.put("state", BookingState.OPEN);
        updates.put("timestamp", booking.getTimestamp());

        FirebaseFirestore.getInstance().collection("bookings")
                .document(booking.getId())
                .update(updates);
    }

    /**
     * Aggiunta dello studente alla chat relativa alla tesi. Crea una chat se non presente
     */
    private void addStudentToChat() {
        DocumentReference chatRef = FirebaseFirestore.getInstance().collection("chats").document(booking.getIdThesis());

        chatRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot result = task.getResult();
                final ChatData[] chat = {result.toObject(ChatData.class)};

                if (chat[0] != null) {
                    chat[0].getMembers().add(booking.getStudentId());
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("members", chat[0].getMembers());

                    chatRef.update(updates);
                } else {

                    DocumentReference tesiRef = FirebaseFirestore.getInstance().collection("tesi").document(booking.getIdThesis());
                    tesiRef.get().addOnCompleteListener(task1 -> {
                        List<String> memeber = new ArrayList<>();
                        if (task1.isSuccessful()) {
                            Tesi tesi = task1.getResult().toObject(Tesi.class);
                            if (tesi != null && tesi.getCoRelatori() != null) {
                                for (PersonaTesi personaTesi : tesi.getCoRelatori()) {
                                    memeber.add(personaTesi.getId());
                                }
                            }
                        }
                        memeber.add(booking.getProfId());
                        memeber.add(booking.getStudentId());
                        chat[0] = new ChatData(booking.getIdThesis(), memeber, booking.getNameThesis());

                        chatRef.set(chat[0]).addOnFailureListener(e -> Log.e(CLASS_ID, "Unable set chat --> " + e));
                    });

                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(BOOKING_KEY, booking);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bookingViewModel.getBooking().setValue(null);

        if (user.getRole().equals(RoleUser.STUDENT)) {
            requireActivity().removeMenuProvider(providerMenu);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        navBar.setVisibility(View.VISIBLE);
    }
}
