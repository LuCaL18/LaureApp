package com.uniba.mobile.cddgl.laureapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.uniba.mobile.cddgl.laureapp.databinding.FragmentRicevimentoBinding;
import com.uniba.mobile.cddgl.laureapp.ui.component.DatePickerFragment;




public class RicevimentoFragment extends Fragment {

    private FragmentRicevimentoBinding binding;

    public RicevimentoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRicevimentoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        EditText data = binding.dataE;
        data.setOnClickListener(v -> {
           DialogFragment datePicker = new DatePickerFragment(R.layout.fragment_ricevimento);
           datePicker.show(getParentFragmentManager(), "date picker");
           });
    }

}