package com.uniba.mobile.cddgl.laureapp.ui.login;

import androidx.annotation.Nullable;

class RegisterFormState extends LoginFormState {
    @Nullable
    private Integer nameError;
    @Nullable
    private Integer surnameError;
    @Nullable
    private Integer bioError;

    public RegisterFormState(@Nullable Integer usernameError, @Nullable Integer passwordError, @Nullable Integer nameError, @Nullable Integer surnameError, @Nullable Integer bioError) {
        super(usernameError, passwordError);
        this.nameError = nameError;
        this.surnameError = surnameError;
        this.bioError = bioError;
    }

    RegisterFormState(boolean isDataValid) {
        super(isDataValid);
        this.bioError = null;
        this.nameError = null;
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
    public Integer getBioError() {
        return bioError;
    }

}
