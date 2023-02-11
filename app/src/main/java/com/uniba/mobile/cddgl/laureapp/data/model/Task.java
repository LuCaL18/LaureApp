package com.uniba.mobile.cddgl.laureapp.data.model;

import com.google.firebase.firestore.PropertyName;

enum StatoTask {
    NEW("Nuovo"),
    RUNNING("In esecuzione"),
    END("Finito");

    private final String descrizione;

    StatoTask(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return descrizione;
    }
}

public class Task {
    private String nometask;
    private StatoTask stato;
    private String descrizione;
    private String scadenza;

    public Task (String nometask, StatoTask stato, String descrizione, String scadenza) {
        this.nometask = nometask;
        this.stato = stato;
        this.descrizione = descrizione;
        this.scadenza = scadenza;
    }

    public Task () {

    }

    @PropertyName("nomeTask")
    public String getNometask() {
        return nometask;
    }

    @PropertyName("nomeTask")
    public void setNometask(String nometask) {
        this.nometask = nometask;
    }

    public StatoTask getStato() {
        return stato;
    }

    public void setStato(StatoTask stato) {
        this.stato = stato;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getScadenza() {
        return scadenza;
    }

    public void setScadenza(String scadenza) {
        this.scadenza = scadenza;
    }
}

