package com.uniba.mobile.cddgl.laureapp.data.model;

import java.io.Serializable;
import java.util.List;

public class Ricevimento implements Serializable {
    private String relatore;
    private String riepilogo;
    private List<String> task;

    private String nomeTesi;

    private String tesiId;

    private Long time;

    private String titolo;

    public Ricevimento() {
    }


    public Ricevimento(String relatore,String riepilogo, List<String> task, String tesi, String tesiId, Long time, String titolo) {
        this.relatore=relatore;
        this.riepilogo = riepilogo;
        this.task = task;
        this.nomeTesi = tesi;
        this.tesiId = tesiId;
        this.time = time;
        this.titolo = titolo;
    }

    public String getRiepilogo() {
        return riepilogo;
    }

    public void setRiepilogo(String riepilogo) {
        this.riepilogo = riepilogo;
    }

    public List<String> getTask() {
        return task;
    }

    public void setTask(List<String> task) {
        this.task = task;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getNomeTesi() {
        return nomeTesi;
    }

    public void setNomeTesi(String nomeTesi) {
        this.nomeTesi = nomeTesi;
    }

    public String getTesiId() {
        return tesiId;
    }

    public void setTesiId(String tesiId) {
        this.tesiId = tesiId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Ricevimento{" +
                "riepilogo='" + riepilogo + '\'' +
                "\n task=" + task +
                "\n tesi='" + nomeTesi + '\'' +
                "\n tesiId='" + tesiId + '\'' +
                "\n time=" + time +
                "\n titolo='" + titolo + '\'' +
                '}';
    }

    public String getRelatore() {
        return relatore;
    }

    public void setRelatore(String relatore) {
        this.relatore = relatore;
    }
}
