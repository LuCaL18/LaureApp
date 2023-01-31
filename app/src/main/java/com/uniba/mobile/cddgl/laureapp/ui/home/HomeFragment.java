package com.uniba.mobile.cddgl.laureapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.uniba.mobile.cddgl.laureapp.MainActivity;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.databinding.FragmentHomeBinding;
import com.uniba.mobile.cddgl.laureapp.ui.home.menu.HomeMenu;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeMenu provider;
    public static final int NOTIFICATION_APP_BAR = R.id.notification_app_bar;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        NavController navController = NavHostFragment.findNavController(this);

        if(actionBar != null) {
            actionBar.setTitle(R.string.app_name_upperCase);
        }

        provider = new HomeMenu(navController);

        requireActivity().addMenuProvider(provider);

        provider.getMenu().observe(getViewLifecycleOwner(), menu -> {
            if(menu != null) {
                homeViewModel.getCountNotification().observe(getViewLifecycleOwner(), integer -> provider.setBadgeIcon(integer));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().removeMenuProvider(provider);
        binding = null;
        provider = null;
    }
}