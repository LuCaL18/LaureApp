package com.uniba.mobile.cddgl.laureapp.data.model;
import com.google.firebase.firestore.PropertyName;

import java.util.List;

public class Tesi {
    private String nomeTesi;
    private String relatore;
    private List<Task> listTask;

    public Tesi() {}

    public Tesi(String nomeTesi, String relatore) {
        this.nomeTesi = nomeTesi;
        this.relatore = relatore;
    }

    public Tesi(String nomeTesi, String relatore, List<Task> listTask) {
        this.nomeTesi = nomeTesi;
        this.relatore = relatore;
        this.listTask = listTask;
    }

    @PropertyName("nome_tesi")
    public String getNomeTesi() {
        return nomeTesi;
    }

    @PropertyName("nome_tesi")
    public void setNomeTesi(String nomeTesi) {
        this.nomeTesi = nomeTesi;
    }

    @PropertyName("relatore")
    public String getRelatore() {
        return relatore;
    }

    @PropertyName("relatore")
    public void setRelatore(String relatore) {
        this.relatore = relatore;
    }

    public List<Task> getListTask() {
        return listTask;
    }

    public void setListTask(List<Task> listTask) {
        this.listTask = listTask;
    }
}
