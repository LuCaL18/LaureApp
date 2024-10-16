package it.uniba.dib.sms222327.laureapp.ui.login;

import androidx.annotation.Nullable;

import it.uniba.dib.sms222327.laureapp.data.model.LoggedInUser;

/**
 * Authentication result : success (user details) or error message.
 */
public class LoginResult {
    @Nullable
    private LoggedInUser success;
    @Nullable
    private Integer error;

    LoginResult(@Nullable Integer error) {
        this.error = error;
    }

    LoginResult(@Nullable LoggedInUser success) {
        this.success = success;
    }

    @Nullable
    public LoggedInUser getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}