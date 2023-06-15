package com.uniba.mobile.cddgl.laureapp.ui.profile.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uniba.mobile.cddgl.laureapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfileViewModel extends ViewModel {

    private final MutableLiveData<EditProfileFormState> editProfileFormState = new MutableLiveData<>();

    public EditProfileViewModel() {
    }

    public MutableLiveData<EditProfileFormState> getEditProfileFormState() {
        return editProfileFormState;
    }

    public void editProdileDataChanged(String name, String surname, String date, String bio) {
        if (!isNameValid(name)) {
            editProfileFormState.setValue(new EditProfileFormState( R.string.invalid_name, null, null, null));
        } else if (!isNameValid(surname)) {
            editProfileFormState.setValue(new EditProfileFormState(null, R.string.invalid_surname, null, null));
        } else if (!isDateValid(date)) {
            editProfileFormState.setValue(new EditProfileFormState(null, null, R.string.invalid_date, null));
        } else if (!isBioValid(bio)) {
            editProfileFormState.setValue(new EditProfileFormState(null, null, null, R.string.invalid_bio));
        } else {
            editProfileFormState.setValue(new EditProfileFormState(true));
        }
    }

    // A placeholder name and surname validation check
    private boolean isNameValid(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        String strPattern = "[~!@#$%^&*()_+{}\\[\\]:;,.<>/?-]";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(name);
        return !m.find();
    }

    private boolean isDateValid(String dateString) {
        if (dateString == null) {
            return false;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = dateFormat.parse(dateString);
            return date.getTime() < new Date().getTime();
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isBioValid(String bio) {
        return bio.split("").length < 200;
    }
}
