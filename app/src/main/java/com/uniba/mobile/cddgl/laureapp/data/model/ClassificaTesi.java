package com.uniba.mobile.cddgl.laureapp.data.model;

import java.util.List;

public class ClassificaTesi {
    private List<Tesi> tesi;
    private String studenteId;

    public ClassificaTesi(List<Tesi> tesi, String studenteId) {
        this.tesi = tesi;
        this.studenteId = studenteId;
    }

    public List<Tesi> getTesi() {
        return tesi;
    }

    public void setTesi(List<Tesi> tesi) {
        this.tesi = tesi;
    }

    public String getStudenteId() {
        return studenteId;
    }

    public void setStudenteId(String studenteId) {
        this.studenteId = studenteId;
    }
}
