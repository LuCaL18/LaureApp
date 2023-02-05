package com.uniba.mobile.cddgl.laureapp.data.model;

import com.google.firebase.firestore.PropertyName;

import java.util.List;
import java.util.Map;

public class ClassificaTesi extends Tesi {
    private List<Tesi> tesi;
    private String studenteId;

    public ClassificaTesi() {
        //
    }

    public ClassificaTesi(List<Tesi> tesi, String studenteId) {
        this.tesi = tesi;
        this.studenteId = studenteId;
    }

    @PropertyName("tesi")
    public List<Tesi> getTesi() {
        return tesi;
    }

    @PropertyName("tesi")
    public void setTesi(List<Tesi> tesi) {
        this.tesi = tesi;
    }

    public String getStudenteId() {
        return studenteId;
    }

    public void setStudenteId(String studenteId) {
        this.studenteId = studenteId;
    }

    public void addTesi(Tesi tesi) {
        this.tesi.add(tesi);
    }

    @Override
    public String getNomeTesi() {
        return super.getNomeTesi();
    }

    @Override
    public String getRelatore() {
        return super.getRelatore();
    }
}

