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


    public VisualizeThesisViewModel() {

        List<PersonaTesi> relators = new ArrayList<>();
        relators.add(new PersonaTesi("122333", "Peppone vb", "peppone@gmail.vom"));
        relators.add(new PersonaTesi("143433", "Vambrico Rossi", "nonèunamial@gmail.vom"));

        List<String> documents = new ArrayList<>();
        documents.add("ciao.jpeg");

        Tesi tesi = new Tesi("Nome Tesi", relators,
                new PersonaTesi("DsZ6p1vN56SccxJlbLIOxJ9Lntx2", "Jesuis Professor", "ilmeglio@gmail.com"),
                "Questa è una descrizione bella lunga della tesu per chè non sono cosa scriver e okkokokok",
                "informatica", "compuot", "devi saper accendere il computer",
                "4 mesi", "matematica, francese, itp, italiano, inglese, reti di calcolatori",
                30, documents, false, "https://www.example.com");
        tesi.setId("123456");

//        tesi.setImageTesi("https://firebasestorage.googleapis.com/v0/b/laureapp-b5243.appspot.com/o/images%2Fprofile%2Fadam.jpg?alt=media&token=8ade6ce0-02c1-4edb-a605-8a86825ab577");

        thesis.setValue(tesi);
    }

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
