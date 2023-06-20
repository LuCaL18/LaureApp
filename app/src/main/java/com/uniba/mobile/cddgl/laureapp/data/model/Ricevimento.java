package com.uniba.mobile.cddgl.laureapp.data.model;

import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;

import java.io.Serializable;
import java.util.List;

/**
 * Classe che rappresenta l'istanza del ricevimento
 */
public class Ricevimento implements Serializable {
    private String ricevimentoId;
    private String relatore;
    private String riepilogo;
    private List<String> task;
    private String nomeTesi;
    private String tesiId;
    private Long time;
    private String titolo;
    private String studente;
    private List<PersonaTesi> co_relatori;

    public Ricevimento() {
    }
    public Ricevimento(String ricevimentoId) {
        this.ricevimentoId = ricevimentoId;
    }


    public Ricevimento(String ricevimentoId, String relatore, String riepilogo, List<String> task, String tesi, String tesiId, Long time, String titolo, String studente, List<PersonaTesi> co_relatori) {
        this.ricevimentoId = ricevimentoId;
        this.relatore=relatore;
        this.riepilogo = riepilogo;
        this.task = task;
        this.nomeTesi = tesi;
        this.tesiId = tesiId;
        this.time = time;
        this.titolo = titolo;
        this.studente=studente;
        this.co_relatori=co_relatori;
    }

    public String getRiepilogo() {
        return riepilogo;
    }

    public void setRiepilogo(String riepilogo) {
        this.riepilogo = riepilogo;
    }

    public List<String> getTask() {
        return task;
    }

    public void setTask(List<String> task) {
        this.task = task;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getNomeTesi() {
        return nomeTesi;
    }

    public void setNomeTesi(String nomeTesi) {
        this.nomeTesi = nomeTesi;
    }

    public String getTesiId() {
        return tesiId;
    }

    public void setTesiId(String tesiId) {
        this.tesiId = tesiId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Ricevimento{" +
                "riepilogo='" + riepilogo + '\'' +
                "\n task=" + task +
                "\n tesi='" + nomeTesi + '\'' +
                "\n tesiId='" + tesiId + '\'' +
                "\n time=" + time +
                "\n titolo='" + titolo + '\'' +
                '}';
    }

    public String getRelatore() {
        return relatore;
    }

    public void setRelatore(String relatore) {
        this.relatore = relatore;
    }

    public String getRicevimentoId() {
        return ricevimentoId;
    }

    public void setRicevimentoId(String ricevimentoId) {
        this.ricevimentoId = ricevimentoId;
    }

    public String getStudente() {
        return studente;
    }

    public void setStudente(String studente) {
        this.studente = studente;
    }

    public List<PersonaTesi> getCo_relatori() {
        return co_relatori;
    }

    public void setCo_relatori(List<PersonaTesi> co_relatori) {
        this.co_relatori = co_relatori;
    }
}
