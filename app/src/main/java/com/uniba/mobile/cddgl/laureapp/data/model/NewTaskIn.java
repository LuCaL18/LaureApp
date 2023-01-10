package com.uniba.mobile.cddgl.laureapp.data.model;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class NewTaskIn {

    private String nomeTask;
    private String stato;
    private String descrizione;

    public NewTaskIn(String nomeTask, String stato, String descrizione, RoleUser student) {
        this.nomeTask = nomeTask;
        this.stato = stato;
        this.descrizione = descrizione;
    }

    public String getNomeTask() {
        return nomeTask;
    }

    public void setNomeTask(String nomeTask) {
        this.nomeTask = nomeTask;
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

}
