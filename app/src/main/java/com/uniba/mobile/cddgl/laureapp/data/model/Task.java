package com.uniba.mobile.cddgl.laureapp.data.model;

import com.uniba.mobile.cddgl.laureapp.data.TaskState;

import java.io.Serializable;
import java.util.List;

public class Task implements Serializable {

    private String student;
    private List<String> relators;
    private String descrizione;
    private String nomeTask;
    private String scadenza;
    private TaskState stato;

    public Task() {
    }

    public Task(String student, List<String> relators, String descrizione, String nomeTask, String scadenza, TaskState stato) {
        this.student = student;
        this.relators = relators;
        this.descrizione = descrizione;
        this.nomeTask = nomeTask;
        this.scadenza = scadenza;
        this.stato = stato;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public List<String> getRelators() {
        return relators;
    }

    public void setRelators(List<String> relators) {
        this.relators = relators;
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
}
