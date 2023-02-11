package com.uniba.mobile.cddgl.laureapp.data.model;

public enum StatoTask {
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
