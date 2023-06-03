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
    private List<String> tesi;
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
    public TesiClassifica(List<String> tesi, String studentId) {
        this.tesi = tesi;
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    /**
     *
     * Getter and Setter relativi
     * a studenteId e tesi
     *
     * */
    @PropertyName("tesi")
    public List<String> getTesi() {
        return tesi;
    }

    @PropertyName("tesi")
    public void setTesi(List<String> tesi) {
        this.tesi = tesi;
    }

    /**
     *
     * Metodo che assegna una nuova tesi da aggiungere alla classifica
     */
    public void addTesi(String idTesi) {
        tesi.add(idTesi);
    }

    /**
     *
     * Metodo che rimuove una tesi già presente nella classifica dalla lista di tesi
     */
    public void removeTesi(String idTesi) {
        tesi.remove(idTesi);
    }

    @Override
    public String toString() {
        return "TesiClassifica{" +
                "studentId='" + studentId + '\'' +
                ", tesi=" + tesi +
                '}';
    }
}
