package com.uniba.mobile.cddgl.laureapp.data.model;

import java.io.Serializable;
import java.util.List;

public class TesiClassifica implements Serializable {

    private String studentId;
    private List<Tesi> tesi;

    public TesiClassifica() {
    }

    public TesiClassifica(String studentId, List<Tesi> tesi) {
        this.studentId = studentId;
        this.tesi = tesi;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public List<Tesi> getTesi() {
        return tesi;
    }

    public void setTesi(List<Tesi> tesi) {
        this.tesi = tesi;
    }

    @Override
    public String toString() {
        return "TesiClassifica{" +
                "studentId='" + studentId + '\'' +
                ", tesi=" + tesi +
                '}';
    }
}
