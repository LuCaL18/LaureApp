package com.uniba.mobile.cddgl.laureapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.messaging.FirebaseMessaging;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.databinding.ActivityLoginBinding;
import com.uniba.mobile.cddgl.laureapp.ui.login.LoginViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.login.LoginViewModelFactory;

import java.io.Serializable;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AppBarConfiguration appBarConfiguration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarLogin.topAppBarLogin);

        appBarConfiguration = new AppBarConfiguration.Builder(R.id.startFragment)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_login);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        LoginViewModel loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);
        loginViewModel.getLoggedUser().observe(this, new Observer<LoggedInUser>() {

            @Override
            public void onChanged(LoggedInUser loggedInUser) {
                if (loggedInUser == null) {
                    return;
                }

                fetchTokenFCM();
                goToMainActivity(loggedInUser);
            }
        });
    }
    // Metodo per passare all'activity principale (MainActivity) dopo il login
    private void goToMainActivity(LoggedInUser loggedInUser) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle bundle = new Bundle();
        bundle.putSerializable(MainActivity.LOGGED_USER, (Serializable) loggedInUser);
        intent.putExtras(bundle);

        startActivity(intent);
        finish();
    }
    // Metodo per ottenere il token di registrazione del dispositivo per le notifiche Firebase Cloud Messaging (FCM)
    private void fetchTokenFCM() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener((task -> {
            if (!task.isSuccessful()) {
                Log.w("Main Activity", "Fetching FCM registration token failed", task.getException());
            }
        }));
    }

//    public void hideSupportActionBar() {
//        try {
//            getSupportActionBar().hide();
//        } catch(NullPointerException e) {
//            Log.e("LoginActivity", e.getMessage());
//        }
//    }
//
//    public void showSupportActionBar() {
//        try {
//            getSupportActionBar().show();
//        } catch(NullPointerException e) {
//            Log.e("LoginActivity", e.getMessage());
//        }
//    }

    // Metodo per gestire la pressione del pulsante di navigazione "indietro"
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_login);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}