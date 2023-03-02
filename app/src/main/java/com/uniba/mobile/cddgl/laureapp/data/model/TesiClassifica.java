package com.uniba.mobile.cddgl.laureapp.data.model;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.List;

/**
 *
 * Classe TesiClassifica in cui sono raccolte le classifiche personalizzate di ogni singolo utente
 *
 */

public class TesiClassifica implements Serializable {

    /* Lista di tesi che raccoglie tutte le tesi da inserire nella classifica */
    private List<Tesi> tesi;
    /* Stringa che rappresenta l'id dello studente all'interno del database */
    private String studentId;

    public TesiClassifica() {
        //
    }

    /**
     *
     * Costruttore della classe
     *
     * consente di istanziare una nuova classifica con all'interno una o più tesi preferite
     *
     * Pre-condizioni: deve esistere una lista di tesi istanziata con almeno una tesi e l'id dello
     *                 studente deve essere rintracciato all'interno del database
     * Post-condizioni: viene creato l'oggetto classificaTesi
     *
     * I campi che saranno oggetto di attenzione all'interno del costruttore sono:
     *
     * tesi      : lista di tesi non vuote
     * studenteId: stringa identificatore univoco dello studente
     *
     */
    public TesiClassifica(List<Tesi> tesi, String studentId) {
        this.tesi = tesi;
        this.studentId = studentId;
    }

    /**
     *
     * Getter and Setter relativi
     * a studenteId e tesi
     *
     * */
    @PropertyName("tesi")
    public List<Tesi> getTesi() {
        return tesi;
    }

    @PropertyName("tesi")
    public void setTesi(List<Tesi> tesi) {
        this.tesi = tesi;
    }

    public String getStudenteId() {
        return studentId;
    }

    public void setStudenteId(String studenteId) {
        this.studentId = studenteId;
    }

    /**
     *
     * Metodo che assegna una nuova tesi da aggiungere alla classifica
     *
     * @param tesi: oggetto di tipo tesi precedentemente istanziato da aggiungere alla lista di tesi personalizzata
     */
    public void addTesi(Tesi tesi) {
        this.tesi.add(tesi);
    }

    /**
     *
     * Metodo che rimuove una tesi già presente nella classifica dalla lista di tesi
     *
     * @param tesiRemove: oggetto di tipo tesi precedentemente istanziato e inserito nella lista tesi da rimuovere dalla classifica
     */
    public void removeTesi(Tesi tesiRemove) {
        this.tesi.remove(tesiRemove);
    }

    @Override
    public String toString() {
        return "TesiClassifica{" +
                "studentId='" + studentId + '\'' +
                ", tesi=" + tesi +
                '}';
    }

}

