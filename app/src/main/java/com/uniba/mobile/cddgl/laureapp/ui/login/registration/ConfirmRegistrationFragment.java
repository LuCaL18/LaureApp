package com.uniba.mobile.cddgl.laureapp.ui.login.registration;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.ui.login.LoginViewModel;

import java.util.Locale;

/**
 * Fragment che si occupa della visualizzazione di richiesta conferma registrazione. Una volta ricevuta il fragment è distrutto
 */
public class ConfirmRegistrationFragment extends Fragment {

    private FirebaseUser user;
    private LoginViewModel loginViewModel;
    private boolean isEmailSent;
    private FirebaseAuth auth;
    private Handler handler = new Handler();
    private Runnable runnable;

    public ConfirmRegistrationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        loginViewModel.getIsUserVerification().setValue(true);
        runnable = new Runnable() {
            @Override
            public void run() {
                user.reload().addOnSuccessListener(aVoid -> {
                    user = auth.getCurrentUser();
                    if (user != null) {
                        if (user.isEmailVerified() && loginViewModel!= null) {
                            loginViewModel.confirmRegistration(user.getUid());
                        }
                    }
                });
                handler.postDelayed(this, 1000); // Delay of 1 second
            }
        };

        user = auth.getCurrentUser();

        TextView text = view.findViewById(R.id.textViewConfirmRegistration);
        TextView timer = view.findViewById(R.id.timerResend);
        text.setText(getResources().getString(R.string.request_confirm_email_verified, user.getEmail()));

        Button resendButton = view.findViewById(R.id.buttonResendEmail);
        resendButton.setOnClickListener(view1 -> {

            resendButton.setEnabled(false);
            timer.setVisibility(View.VISIBLE);
            sendEmailVerification();
            new CountDownTimer(180000, 1000) { // 3 minutes
                public void onTick(long millisUntilFinished) {
                    int minutes = (int) (millisUntilFinished / 1000) / 60;
                    int seconds = (int) (millisUntilFinished / 1000) % 60;
                    String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                    timer.setText(time);
                }

                public void onFinish() {
                    timer.setVisibility(View.INVISIBLE);
                    resendButton.setEnabled(true);
                }
            }.start();
        });

        LoggedInUser currentLoggedUser = loginViewModel.getCurrentLoggedUser();

        UserProfileChangeRequest update = new UserProfileChangeRequest.Builder().setDisplayName(currentLoggedUser.getDisplayName()).build();
        user.updateProfile(update).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                sendEmailVerification();
            }
        });

        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), loginResult -> {
            if (loginResult == null) {
                return;
            }
            if (loginResult.getSuccess() != null) {
                LoggedInUser result = loginResult.getSuccess();
                updateUiWithUser(result.getDisplayName());
                loginViewModel.setLoggedUser(result);
            }
        });
    }

    private void sendEmailVerification() {
        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (!isEmailSent) {
                if (task.isSuccessful()) {
                    handler.post(runnable);
                    isEmailSent = true;
                } else {
                    sendEmailVerification();
                }
            }
        });
    }

    private void updateUiWithUser(String displayName) {
        String welcome = getString(R.string.welcome) + displayName;
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(getContext().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
        runnable = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (user != null) {
            if (!user.isEmailVerified()) {
                user.delete();
            }
        }
        handler = null;
        loginViewModel = null;
    }
}