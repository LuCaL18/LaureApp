package com.uniba.mobile.cddgl.laureapp.data.model;

import com.uniba.mobile.cddgl.laureapp.data.TaskState;

import java.io.Serializable;

public class Task implements Serializable {

    private String studenteId;
    private String relatore;
    private String descrizione;
    private String nomeTask;
    private String scadenza;
    private TaskState stato;
    private String tesiId;

    public Task() {
    }

    public Task(String studenteId, String relatore, String descrizione, String nomeTask, String scadenza, TaskState stato, String tesiId) {
        this.studenteId = studenteId;
        this.relatore = relatore;
        this.descrizione = descrizione;
        this.nomeTask = nomeTask;
        this.scadenza = scadenza;
        this.stato = stato;
        this.tesiId = tesiId;
    }

    public String getStudent() {
        return studenteId;
    }

    public void setStudent(String studenteId) {
        this.studenteId = studenteId;
    }

    public String getRelators() {
        return relatore;
    }

    public void setRelators(String relatore) {
        this.relatore = relatore;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getNomeTask() {
        return nomeTask;
    }

    public void setNomeTask(String nomeTask) {
        this.nomeTask = nomeTask;
    }

    public String getScadenza() {
        return scadenza;
    }

    public void setScadenza(String scadenza) {
        this.scadenza = scadenza;
    }

    public TaskState getStato() {
        return stato;
    }

    public void setStato(TaskState stato) {
        this.stato = stato;
    }

    public String getTesiId() {
        return tesiId;
    }

    public void setTesiId(String tesiId) {
        this.tesiId = tesiId;
    }
}
