package com.uniba.mobile.cddgl.laureapp.data.model;

import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;

public class Relatore extends PersonaTesi {
    private String id_relatore;
    private String email;
    private String nome;
    private String cognome;

    public Relatore(){
        super();
    }

    public String getId_relatore() {
        return id_relatore;
    }

    public void setId_relatore(String id_relatore) {
        this.id_relatore = id_relatore;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
}
