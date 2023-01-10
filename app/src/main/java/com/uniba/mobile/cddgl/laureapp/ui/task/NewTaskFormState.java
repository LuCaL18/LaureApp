package com.uniba.mobile.cddgl.laureapp.ui.task;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class NewTaskFormState {

    @Nullable
    protected Integer nometaskError;
    @Nullable
    protected Integer statoError;
    protected boolean isDataValid;

    NewTaskFormState(@Nullable Integer nometaskError, @Nullable Integer statoError) {
        this.nometaskError = nometaskError;
        this.statoError = statoError;
    }

    NewTaskFormState(boolean isDataValid) {
        this.nometaskError = null;
        this.statoError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getNometaskError() {
        return nometaskError;
    }

    @Nullable
    Integer getStatoError() {
        return statoError;
    }

    boolean isDataValid() {
        return isDataValid;
    }

}
