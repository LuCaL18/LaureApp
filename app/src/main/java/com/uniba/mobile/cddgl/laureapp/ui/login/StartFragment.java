package com.uniba.mobile.cddgl.laureapp.ui.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;

public class StartFragment extends Fragment {
    private NavController navController;
    private LoginViewModel loginViewModel;

    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.navController = NavHostFragment.findNavController(this);


        Button loginButton = view.findViewById(R.id.buttonLogin);
        Button signInButton = view.findViewById(R.id.buttonSignIn);
        TextView guestText = view.findViewById(R.id.textGuest);

        loginButton.setOnClickListener(view1 -> navController.navigate(R.id.action_startFragment_to_loginFragment));

        signInButton.setOnClickListener(view1 -> navController.navigate(R.id.action_startFragment_to_signInFragment));

        guestText.setOnClickListener(view1 -> loginViewModel.setLoggedUser(new LoggedInUser("ospite", "GUEST", RoleUser.GUEST)));
    }

    @Override
    public void onStart() {
        super.onStart();
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navController = null;
    }
}