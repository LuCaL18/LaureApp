package com.uniba.mobile.cddgl.laureapp.data.model;

import java.io.Serializable;
import java.util.List;

public class TesiClassifica implements Serializable {

    private String studentId;
    private List<String> tesi;

    public TesiClassifica() {
    }

    public TesiClassifica(String studentId, List<String> tesi) {
        this.studentId = studentId;
        this.tesi = tesi;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public List<String> getTesi() {
        return tesi;
    }

    public void setTesi(List<String> tesi) {
        this.tesi = tesi;
    }

    public void addTesi(String idTesi) {
        tesi.add(idTesi);
    }

    public void removeTesi(String idTesi) {
        tesi.remove(idTesi);
    }

    @Override
    public String toString() {
        return "TesiClassifica{" +
                "studentId='" + studentId + '\'' +
                ", tesi=" + tesi +
                '}';
    }
}
