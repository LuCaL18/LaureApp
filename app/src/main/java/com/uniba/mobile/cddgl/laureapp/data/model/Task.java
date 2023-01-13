package com.uniba.mobile.cddgl.laureapp.data.model;

public class Task {

    private String nometask;
    private String stato;
    private String descrizione;
    private String scadenza;

    public Task (String nometask, String stato, String descrizione, String scadenza) {
        this.nometask = nometask;
        this.stato = stato;
        this.descrizione = descrizione;
        this.scadenza = scadenza;
    }

    public Task () {

    }

    public String getNometask() {
        return nometask;
    }

    public void setNometask(String nometask) {
        this.nometask = nometask;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
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
