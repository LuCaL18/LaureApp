package com.uniba.mobile.cddgl.laureapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Fragment per la visualizzazione del logo.
 */
public class LogoFragment extends Fragment {

    private ProgressBar progressBar;
    private BottomNavigationView navBar;
    private boolean isTaskReady;
    private boolean isThesisReady;

    public LogoFragment() {
        isTaskReady = false;
        isThesisReady = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_logo, container, false);
        progressBar = view.findViewById(R.id.loading_logo);

        progressBar.setVisibility(View.VISIBLE);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        mainViewModel.getUser().observe(getViewLifecycleOwner(), loggedInUser -> {
            if(loggedInUser == null) {
                return;
            }

            mainViewModel.readTask(loggedInUser.getRole(), loggedInUser.getId());
            mainViewModel.loadTesi(loggedInUser.getRole());


        });

        mainViewModel.getThesis().observe(getViewLifecycleOwner(), queryDocumentSnapshots -> {
            if(queryDocumentSnapshots == null || !isTaskReady) {
                isThesisReady = true;
                return;
            }

            NavController navController = NavHostFragment.findNavController(this);

            if (navController.getCurrentDestination().getId() == R.id.logo_fragment)  {
                navController.navigate(R.id.action_logo_fragment_to_navigation_home);
                progressBar.setVisibility(View.GONE);
            }

        });

        mainViewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> {
            if(tasks == null || !isThesisReady) {
                isTaskReady = true;
                return;
            }

            NavController navController = NavHostFragment.findNavController(this);

            if (navController.getCurrentDestination().getId() == R.id.logo_fragment)  {
                navController.navigate(R.id.action_logo_fragment_to_navigation_home);
                progressBar.setVisibility(View.GONE);
            }

        });
    }

    @Override
    public void onStart() {
        super.onStart();

        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navBar.setVisibility(View.VISIBLE);
    }
}