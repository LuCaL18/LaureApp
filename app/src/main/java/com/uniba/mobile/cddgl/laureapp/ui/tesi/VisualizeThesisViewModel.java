package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uniba.mobile.cddgl.laureapp.data.DownloadedFile;
import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;

import java.util.ArrayList;
import java.util.List;

public class VisualizeThesisViewModel extends ViewModel {

    private final MutableLiveData<Integer> error = new MutableLiveData<>();
    private final MutableLiveData<DownloadedFile> requestDocument = new MutableLiveData<>();
    private final MutableLiveData<Tesi> thesis = new MutableLiveData<>();


    public VisualizeThesisViewModel() {}

    public MutableLiveData<Integer> getError() {
        return error;
    }

    public MutableLiveData<DownloadedFile> getRequestDocument() {
        return requestDocument;
    }

    public MutableLiveData<Tesi> getThesis() {
        return thesis;
    }
}
