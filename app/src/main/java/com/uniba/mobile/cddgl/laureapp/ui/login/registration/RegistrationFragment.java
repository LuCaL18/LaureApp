package com.uniba.mobile.cddgl.laureapp.ui.login.registration;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.databinding.FragmentRegistrationBinding;
import com.uniba.mobile.cddgl.laureapp.ui.component.DatePickerFragment;
import com.uniba.mobile.cddgl.laureapp.ui.login.LoginViewModel;
import com.uniba.mobile.cddgl.laureapp.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class RegistrationFragment extends Fragment {

    private final int RADIO_BUTTON_STUDENT = R.id.radio_button_student;
    private final int RADIO_BUTTON_PROFESSOR = R.id.radio_button_professor;

    private LoginViewModel loginViewModel;
    private FragmentRegistrationBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        NavController navController = NavHostFragment.findNavController(this);

        final EditText usernameEditText = binding.username;
        final EditText nameEditText = binding.name;
        final EditText surnameEditText = binding.surname;
        final EditText dobEditText = binding.birthDay;
        final EditText bioEditText = binding.bio;
        final EditText passwordEditText = binding.password;
        final EditText confirmPasswordEditText = binding.confirmPassword;
        final Button registerButton = binding.register;
        final ProgressBar loadingProgressBar = binding.loading;
        final Spinner spinnerAmbiti = binding.spinnerAmbiti;

        List<String> ambiti = new ArrayList<>();

        dobEditText.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment(R.layout.fragment_registration);
            datePicker.show(getParentFragmentManager(), "date picker");
        });


        loginViewModel.getRegisterFormState().observe(getViewLifecycleOwner(), registerFormState -> {
            if (registerFormState == null) {
                return;
            }
            registerButton.setEnabled(registerFormState.isDataValid());
            if (registerFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(registerFormState.getUsernameError()));
            }
            if (registerFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(registerFormState.getPasswordError()));
            }
            if (registerFormState.getNameError() != null) {
                nameEditText.setError(getString(registerFormState.getNameError()));
            }
            if (registerFormState.getSurnameError() != null) {
                surnameEditText.setError(getString(registerFormState.getSurnameError()));
            }
            if (registerFormState.getDateError() != null) {
                dobEditText.setError(getString(registerFormState.getDateError()));
            }
            if (registerFormState.getBioError() != null) {
                bioEditText.setError(getString(registerFormState.getBioError()));
            }
            if (registerFormState.getConfirmPasswordError() != null) {
                confirmPasswordEditText.setError(getString(registerFormState.getConfirmPasswordError()));
            }
        });

        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
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
                loginViewModel.registerDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(), nameEditText.getText().toString(),
                        surnameEditText.getText().toString(), dobEditText.getText().toString(),
                        bioEditText.getText().toString(), confirmPasswordEditText.getText().toString());
            }
        };

        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        nameEditText.addTextChangedListener(afterTextChangedListener);
        surnameEditText.addTextChangedListener(afterTextChangedListener);
        dobEditText.addTextChangedListener(afterTextChangedListener);
        bioEditText.addTextChangedListener(afterTextChangedListener);
        confirmPasswordEditText.addTextChangedListener(afterTextChangedListener);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.ambiti, R.layout.custom_item_spinner);
        adapter.setDropDownViewResource(R.layout.custom_item_spinner);

        spinnerAmbiti.setAdapter(adapter);

        ChipGroup chipGroup = binding.chipGroupAmbiti;

        spinnerAmbiti.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                Chip chip = new Chip(getContext());
                chip.setText(selectedItem);
                chip.setCloseIconTint(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                chip.setCloseIconVisible(true);
                chip.setChipBackgroundColorResource(R.color.primary_green);
                chip.setTextColor(getResources().getColor(R.color.white));


                String selectedItemKey = Utility.convertScopesToEnum(selectedItem);
                if (!ambiti.contains(selectedItemKey)) {
                    ambiti.add(selectedItemKey);
                    chipGroup.addView(chip);
                }

                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chipGroup.removeView(chip);
                        ambiti.remove(selectedItemKey);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.register(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(), nameEditText.getText().toString(),
                        surnameEditText.getText().toString(), dobEditText.getText().toString(),
                        bioEditText.getText().toString(), getRoleFromRadioButton(), ambiti);
            }
            return false;
        });

        registerButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.register(usernameEditText.getText().toString(),
                    passwordEditText.getText().toString(), nameEditText.getText().toString(),
                    surnameEditText.getText().toString(), dobEditText.getText().toString(),
                    bioEditText.getText().toString(), getRoleFromRadioButton(), ambiti);
        });

        loginViewModel.getIsUserVerification().observe(getViewLifecycleOwner(), isUserVerification -> {
            if (!isUserVerification) {
                navController.navigate(R.id.action_signInFragment_to_confirmRegistrationFragment);
            }
        });
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    errorString,
                    Toast.LENGTH_LONG).show();
        }
    }

    private RoleUser getRoleFromRadioButton() {
        RadioGroup radioGroupRole = binding.radioGroupRole;
        switch (radioGroupRole.getCheckedRadioButtonId()) {
            case RADIO_BUTTON_PROFESSOR:
                return RoleUser.PROFESSOR;
            case RADIO_BUTTON_STUDENT:
            default:
                return RoleUser.STUDENT;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}