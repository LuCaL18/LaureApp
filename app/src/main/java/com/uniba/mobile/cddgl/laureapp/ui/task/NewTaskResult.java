package com.uniba.mobile.cddgl.laureapp.ui.task;

import androidx.annotation.Nullable;

import com.uniba.mobile.cddgl.laureapp.data.model.NewTaskIn;

/**
 * Authentication result : success (user details) or error message.
 */
public class NewTaskResult {
    @Nullable
    private NewTaskIn success;
    @Nullable
    private Integer error;

    NewTaskResult(@Nullable Integer error) {
        this.error = error;
    }

    NewTaskResult(@Nullable NewTaskIn success) {
        this.success = success;
    }

    @Nullable
    public NewTaskIn getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}