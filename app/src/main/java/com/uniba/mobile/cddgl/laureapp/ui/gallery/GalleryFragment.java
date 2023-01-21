package com.uniba.mobile.cddgl.laureapp.ui.gallery;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.databinding.FragmentGalleryBinding;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private BottomNavigationView navBar;
    private MenuProvider provider;
    private Toolbar toolbar;
    private Drawable iconPre;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGallery;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);

        NavController navController = NavHostFragment.findNavController(this);

        toolbar = getActivity().findViewById(R.id.topAppBar);

        iconPre = toolbar.getNavigationIcon();
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NavigationView navigationView = requireActivity().findViewById(R.id.nav_view_menu);
        navigationView.getMenu().findItem(R.id.nav_gallery).setChecked(false);
        navBar.setVisibility(View.VISIBLE);
        navBar = null;
        binding = null;
        provider = null;
        toolbar = null;
    }
}