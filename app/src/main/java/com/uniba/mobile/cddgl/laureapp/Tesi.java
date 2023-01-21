package com.uniba.mobile.cddgl.laureapp;

public class Tesi {
    private String id_tesi;
    private String nome_tesi;
    private String descrizione;
    private String relatore;
    //private String vincoli;
    //private String relatori;
    private String task;

    public Tesi() {

    }

    public String getId_tesi() {
        return id_tesi;
    }

    public void setId_tesi(String id_tesi) {
        this.id_tesi = id_tesi;
    }

    public String getNome_tesi() {
        return nome_tesi;
    }

    public void setNome_tesi(String nome_tesi) {
        this.nome_tesi = nome_tesi;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getRelatore() {
        return relatore;
    }

    public void setRelatore(String relatore) {
        this.relatore = relatore;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
