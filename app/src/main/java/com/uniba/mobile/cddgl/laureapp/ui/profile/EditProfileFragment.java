package com.uniba.mobile.cddgl.laureapp.ui.profile;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.uniba.mobile.cddgl.laureapp.MainViewModel;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.EnumScopes;
import com.uniba.mobile.cddgl.laureapp.data.Result;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.ui.component.DatePickerFragment;
import com.uniba.mobile.cddgl.laureapp.ui.profile.viewModels.EditProfileViewModel;
import com.uniba.mobile.cddgl.laureapp.util.Utility;

import java.util.ArrayList;
import java.util.List;


public class EditProfileFragment extends Fragment {

    private MainViewModel mainViewModel;
    private EditProfileViewModel editProfileViewModel;
    private BottomNavigationView navBar;
    private EditText emailEditText;
    private EditText nameEditText;
    private EditText lastNameEditText;
    private EditText birthDateEditText;
    private EditText bioEditText;
    private ChipGroup chipGroup;
    private List<String> scopes;
    private Button saveButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        editProfileViewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameEditText = view.findViewById(R.id.name_edit_text);
        lastNameEditText = view.findViewById(R.id.last_name_edit_text);
        birthDateEditText = view.findViewById(R.id.birth_date_edit_text);
        emailEditText = view.findViewById(R.id.email_edit_text);
        bioEditText = view.findViewById(R.id.bio_edit_profile);
        Spinner spinnerScopes = view.findViewById(R.id.spinner_scopes);
        saveButton = view.findViewById(R.id.save_button);

        birthDateEditText.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment(R.layout.fragment_edit_profile);
            datePicker.show(getParentFragmentManager(), "date picker");
        });

        scopes = new ArrayList<>();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.ambiti, R.layout.custom_item_spinner);
        adapter.setDropDownViewResource(R.layout.custom_item_spinner);
        spinnerScopes.setAdapter(adapter);
        chipGroup = view.findViewById(R.id.chip_group_scopes);
        spinnerScopes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                createChipScope(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                editProfileViewModel.editProdileDataChanged(nameEditText.getText().toString(),
                        lastNameEditText.getText().toString(), birthDateEditText.getText().toString(),
                        bioEditText.getText().toString());
            }
        };

        nameEditText.addTextChangedListener(afterTextChangedListener);
        lastNameEditText.addTextChangedListener(afterTextChangedListener);
        birthDateEditText.addTextChangedListener(afterTextChangedListener);
        bioEditText.addTextChangedListener(afterTextChangedListener);

        mainViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                displayUserData(user);
            }
        });

        mainViewModel.getEditUserResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) {
                return;
            }

            if (result instanceof Result.Success) {
                Toast.makeText(requireContext(), getString(R.string.edit_profile_success), Toast.LENGTH_SHORT).show();
                NavController navController = NavHostFragment.findNavController(this);
                navController.popBackStack();
                mainViewModel.getEditUserResult().setValue(null);
            } else {
                Toast.makeText(getContext(), getString(R.string.update_user_failed), Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(v -> saveProfileChanges());
    }


    private void displayUserData(LoggedInUser user) {
        nameEditText.setText(user.getName());
        lastNameEditText.setText(user.getSurname());
        birthDateEditText.setText(user.getBirthDay());
        emailEditText.setText(user.getEmail());

        if (user.getBio() != null && !user.getBio().isEmpty()) {
            bioEditText.setText(user.getBio());
        }

        if (user.getAmbiti() != null && !user.getAmbiti().isEmpty()) {
            for (String scope : user.getAmbiti()) {
                createChipScope(Utility.translateScopesFromEnum(getResources(), EnumScopes.valueOf(scope)));
            }
        }
    }

    private void createChipScope(String scope) {
        Chip chip = new Chip(getContext());
        chip.setText(scope);
        chip.setCloseIconTint(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        chip.setCloseIconVisible(true);
        chip.setChipBackgroundColorResource(R.color.primary_green);
        chip.setTextColor(getResources().getColor(R.color.white));


        String scopeKey = Utility.convertScopesToEnum(scope);
        if (!scopes.contains(scopeKey)) {
            scopes.add(scopeKey);
            chipGroup.addView(chip);
        }

        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(chip);
                scopes.remove(scopeKey);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        navBar = requireActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        editProfileViewModel.getEditProfileFormState().observe(getViewLifecycleOwner(), editProfileFormState -> {
            if (editProfileFormState == null) {
                return;
            }
            saveButton.setEnabled(editProfileFormState.isDataValid());
            if (editProfileFormState.getNameError() != null) {
                nameEditText.setError(getString(editProfileFormState.getNameError()));
            }
            if (editProfileFormState.getSurnameError() != null) {
                lastNameEditText.setError(getString(editProfileFormState.getSurnameError()));
            }
            if (editProfileFormState.getDateError() != null) {
                birthDateEditText.setError(getString(editProfileFormState.getDateError()));
            }
            if (editProfileFormState.getBioError() != null) {
                bioEditText.setError(getString(editProfileFormState.getBioError()));
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mainViewModel.getEditUserResult().setValue(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navBar = requireActivity().findViewById(R.id.nav_view);
        if (navBar != null) {
            navBar.setVisibility(View.VISIBLE);
        }
    }

    private void saveProfileChanges() {
        String newName = nameEditText.getText().toString();
        String newLastName = lastNameEditText.getText().toString();
        String newBirthDate = birthDateEditText.getText().toString();
        String bio = bioEditText.getText().toString();

        // Update the user object with new values
        LoggedInUser user = mainViewModel.getUser().getValue();
        if (user != null) {
            user.setName(newName);
            user.setSurname(newLastName);
            user.setBirthDay(newBirthDate);
            user.setBio(bio);
            user.setAmbiti(scopes);

            // Update the user data in the ViewModel and Firestore
            mainViewModel.getUser().setValue(user);
            mainViewModel.updateUser(user);
        }
    }
}