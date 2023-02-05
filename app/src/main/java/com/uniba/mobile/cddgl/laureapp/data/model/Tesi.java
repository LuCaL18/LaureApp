package com.uniba.mobile.cddgl.laureapp.data.model;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

public class Tesi {
    private String nomeTesi;
    private String relatore;

    public Tesi() {}

    public Tesi(String nomeTesi, String relatore) {
        this.nomeTesi = nomeTesi;
        this.relatore = relatore;
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

    public String getRelatore(String relatore) {
        return relatore;
    }

    public String getNomeTesi(String nomeTesi) {
        return nomeTesi;
    }
}
