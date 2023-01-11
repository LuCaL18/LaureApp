package com.uniba.mobile.cddgl.laureapp.ui.task;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.databinding.FragmentNewTaskBinding;

public class NewTaskFragment extends Fragment {

    private com.uniba.mobile.cddgl.laureapp.ui.task.NewTaskViewModel newTaskViewModel;
    private FragmentNewTaskBinding binding;
    private EditText nometaskEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentNewTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTaskViewModel = new ViewModelProvider(requireActivity()).get(NewTaskViewModel.class);

        final EditText nometaskEditText = binding.nometask;
        final EditText statoEditText = binding.stato;
        final EditText descrizioneEditText = binding.descrizione;
        final Button addTaskButton = binding.button;
        final TextView nometaskTesto = binding.nometaskTesto;
        final TextView statoTesto = binding.statoTesto;
        final TextView descrizioneTesto = binding.descrizioneTesto;

        newTaskViewModel.getNewTaskFormState().observe(getViewLifecycleOwner(), newtaskFormState -> {
            if (newtaskFormState == null) {
                return;
            }
            addTaskButton.setEnabled(newtaskFormState.isDataValid());
        });

        newTaskViewModel.getNewTaskResult().observe(getViewLifecycleOwner(), newtaskResult -> {
            if (newtaskResult == null) {
                return;
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                newTaskViewModel.newtaskDataChanged(nometaskEditText.getText().toString(),
                        statoEditText.getText().toString(),descrizioneEditText.getText().toString());
            }
        };
        nometaskEditText.addTextChangedListener(afterTextChangedListener);
        statoEditText.addTextChangedListener(afterTextChangedListener);
        descrizioneEditText.addTextChangedListener(afterTextChangedListener);
        descrizioneEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                newTaskViewModel.newtask(nometaskEditText.getText().toString(),
                        statoEditText.getText().toString(),descrizioneEditText.getText().toString());
            }
            return false;
        });

        addTaskButton.setOnClickListener(v -> {
            newTaskViewModel.newtask(nometaskEditText.getText().toString(),
                    statoEditText.getText().toString(),descrizioneEditText.getText().toString());
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}