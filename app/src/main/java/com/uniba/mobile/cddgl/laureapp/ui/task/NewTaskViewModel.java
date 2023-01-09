package com.uniba.mobile.cddgl.laureapp.ui.task;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewTaskViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NewTaskViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is newtask fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}