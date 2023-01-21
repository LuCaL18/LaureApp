package com.uniba.mobile.cddgl.laureapp;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.ui.login.LoginViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.login.LoginViewModelFactory;
import com.uniba.mobile.cddgl.laureapp.databinding.ActivityLoginBinding;

import java.io.Serializable;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LoginViewModel loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);
        loginViewModel.getLoggedUser().observe(this, new Observer<LoggedInUser>() {

            @Override
            public void onChanged(LoggedInUser loggedInUser) {
                if (loggedInUser == null) {
                    return;
                }
                goToMainActivity(loggedInUser);
            }
        });
    }

    private void goToMainActivity(LoggedInUser loggedInUser) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle bundle = new Bundle();
        bundle.putSerializable(MainActivity.LOGGED_USER, (Serializable) loggedInUser);
        intent.putExtras(bundle);

        startActivity(intent);
        finish();
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