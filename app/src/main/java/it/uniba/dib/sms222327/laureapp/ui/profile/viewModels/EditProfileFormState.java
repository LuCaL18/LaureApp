package it.uniba.dib.sms222327.laureapp.ui.profile.viewModels;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
public class EditProfileFormState {

    @Nullable
    private final Integer nameError;
    @Nullable
    private final Integer surnameError;
    @Nullable
    private final Integer dateError;
    @Nullable
    private final Integer bioError;
    private boolean isDataValid;

    public EditProfileFormState(@Nullable Integer nameError, @Nullable Integer surnameError, @Nullable Integer dateError, @Nullable Integer bioError) {
        this.nameError = nameError;
        this.surnameError = surnameError;
        this.dateError = dateError;
        this.bioError = bioError;
        this.isDataValid = false;
    }

    public EditProfileFormState(boolean isDataValid) {
        this.isDataValid = isDataValid;
        this.bioError = null;
        this.nameError = null;
        this.surnameError = null;
        this.dateError = null;
    }

    @Nullable
    public Integer getNameError() {
        return nameError;
    }

    @Nullable
    public Integer getSurnameError() {
        return surnameError;
    }

    @Nullable
    public Integer getDateError() {
        return dateError;
    }

    @Nullable
    public Integer getBioError() {
        return bioError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
