package com.uniba.mobile.cddgl.laureapp.ui.task;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.uniba.mobile.cddgl.laureapp.ui.login.LoginViewModel;

public class NewTaskModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NewTaskViewModel.class)) {
            return (T) new NewTaskViewModel();
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}
