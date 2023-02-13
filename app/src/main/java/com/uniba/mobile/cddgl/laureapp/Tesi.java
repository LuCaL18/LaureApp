package com.uniba.mobile.cddgl.laureapp;

import java.util.Map;

public class Tesi {
    private String id_tesi;
    private String nome_tesi;
    private String descrizione;
    private String relatore;

    private String skill;

    private String tempistiche;

    private String ambito;

    private String chiave;

    private Map<String, String> co_relatori;
    private String task[];

    private int media;

    private String studente;

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

    public Map<String, String> getCo_relatori() {
        return co_relatori;
    }

    public void setCo_relatori(Map<String, String> co_relatori) {
        this.co_relatori = co_relatori;
    }

    public String[] getTask() {
        return task;
    }

    public void setTask(String[] task) {
        this.task = task;
    }
}
