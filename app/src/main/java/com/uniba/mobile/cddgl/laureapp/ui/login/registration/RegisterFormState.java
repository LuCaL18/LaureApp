package com.uniba.mobile.cddgl.laureapp.ui.login.registration;

import androidx.annotation.Nullable;

import com.uniba.mobile.cddgl.laureapp.ui.login.LoginFormState;

/**
 * Classe che estende LoginFormState utilizzata per
 * registrare lo stato della registrazione i base agli input ricevuti
 */
public class RegisterFormState extends LoginFormState {
    @Nullable
    private final Integer nameError;
    @Nullable
    private final Integer surnameError;
    @Nullable
    private final Integer dateError;
    @Nullable
    private final Integer bioError;
    @Nullable
    private final Integer confirmPasswordError;

    public RegisterFormState(@Nullable Integer usernameError, @Nullable Integer passwordError, @Nullable Integer nameError, @Nullable Integer surnameError, @Nullable Integer dateError, @Nullable Integer bioError, @Nullable Integer confirmPasswordError) {
        super(usernameError, passwordError);
        this.nameError = nameError;
        this.surnameError = surnameError;
        this.dateError = dateError;
        this.bioError = bioError;
        this.confirmPasswordError = confirmPasswordError;
    }

    public RegisterFormState(boolean isDataValid) {
        super(isDataValid);
        this.bioError = null;
        this.nameError = null;
        this.surnameError = null;
        this.dateError = null;
        this.confirmPasswordError = null;
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

    @Nullable
    public Integer getConfirmPasswordError() {
        return confirmPasswordError;
    }
}
