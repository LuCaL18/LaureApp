package com.uniba.mobile.cddgl.laureapp.data.model;

public class Tesi {

    private String nomeTesi;
    private String relatore;
    private String[] relatori;
    private String[] vincoli;
    private Task[] task;
    private String nomeStudente;

    public Tesi (String nomeTesi, String relatore) {
        this.nomeTesi = nomeTesi;
        this.relatore = relatore;
    }

    public Tesi (String nomeTesi, String relatore, String nomeStudente) {
        this.nomeTesi = nomeTesi;
        this.relatore = relatore;
        this.nomeStudente = nomeStudente;
    }

    public Tesi (String nomeTesi, String relatore, String[] relatori, String[] vincoli, Task[] task, String nomeStudente) {
        this.nomeTesi = nomeTesi;
        this.relatore = relatore;
        this.relatori = relatori;
        this.vincoli = vincoli;
        this.task = task;
        this.nomeStudente = nomeStudente;
    }

    public Tesi (String nomeTesi, String relatore, String[] relatori, String[] vincoli, String nomeStudente) {
        this.nomeTesi = nomeTesi;
        this.relatore = relatore;
        this.relatori = relatori;
        this.vincoli = vincoli;
        this.nomeStudente = nomeStudente;
    }

    public String getNomeStudente() {
        return nomeStudente;
    }

    public void setNomeStudente(String nomeStudente) {
        this.nomeStudente = nomeStudente;
    }

    public Task[] getTask() {
        return task;
    }

    public void setTask(Task[] task) {
        this.task = task;
    }

    public String[] getRelatori() {
        return relatori;
    }

    public void setRelatori(String[] relatori) {
        this.relatori = relatori;
    }

    public String getRelatore() {
        return relatore;
    }

    public void setRelatore(String relatore) {
        this.relatore = relatore;
    }

    public String getNomeTesi() {
        return nomeTesi;
    }

    public void setNomeTesi(String nomeTesi) {
        this.nomeTesi = nomeTesi;
    }

    public String[] getVincoli() {
        return vincoli;
    }

    public void setVincoli(String[] vincoli) {
        this.vincoli = vincoli;
    }

}
