package com.uniba.mobile.cddgl.laureapp.data.model;

import androidx.annotation.NonNull;

import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Tesi implements Serializable, Cloneable {

    private String id;
    private String nomeTesi;
    private String imageTesi;
    private List<PersonaTesi> realatori;
    private PersonaTesi professor;
    private String descrizione;
    private String ambito;
    private String chiave;
    private String skill;
    private String tempistiche;
    private String esami;
    private String mediaVoto;
    private List<String> documents;
    private PersonaTesi student;
    private Boolean isAssigned;
    private String note;

    public Tesi() {
    }

    public Tesi(String nomeTesi, List<PersonaTesi> realatori, PersonaTesi professor, String descrizione, String ambito, String chiave, String skill, String tempistiche, String esami, String mediaVoto, List<String> documents, Boolean isAssigned, String note) {
        this.id = UUID.randomUUID().toString();
        this.nomeTesi = nomeTesi;
        this.realatori = realatori;
        this.professor = professor;
        this.descrizione = descrizione;
        this.ambito = ambito;
        this.chiave = chiave;
        this.skill = skill;
        this.tempistiche = tempistiche;
        this.esami = esami;
        this.mediaVoto = mediaVoto;
        this.documents = documents;
        this.isAssigned = isAssigned;
        this.note = note;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getNomeTesi() {
        return nomeTesi;
    }

    public void setNomeTesi(String nomeTesi) {
        this.nomeTesi = nomeTesi;
    }

    public List<PersonaTesi> getRealatori() {
        return realatori;
    }

    public void setRealatori(List<PersonaTesi> realatori) {
        this.realatori = realatori;
    }

    public PersonaTesi getProfessor() {
        return professor;
    }

    public void setProfessor(PersonaTesi professor) {
        this.professor = professor;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getAmbito() {
        return ambito;
    }

    public void setAmbito(String ambito) {
        this.ambito = ambito;
    }

    public String getChiave() {
        return chiave;
    }

    public void setChiave(String chiave) {
        this.chiave = chiave;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getTempistiche() {
        return tempistiche;
    }

    public void setTempistiche(String tempistiche) {
        this.tempistiche = tempistiche;
    }

    public String getEsami() {
        return esami;
    }

    public void setEsami(String esami) {
        this.esami = esami;
    }

    public String getMediaVoto() {
        return mediaVoto;
    }

    public void setMediaVoto(String mediaVoto) {
        this.mediaVoto = mediaVoto;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }

    public PersonaTesi getStudent() {
        return student;
    }

    public void setStudent(PersonaTesi student) {
        this.student = student;
    }

    public Boolean getAssigned() {
        return isAssigned;
    }

    public void setAssigned(Boolean assigned) {
        isAssigned = assigned;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getImageTesi() {
        return imageTesi;
    }

    public void setImageTesi(String imageTesi) {
        this.imageTesi = imageTesi;
    }

    @Override
    public String toString() {
        return "Tesi{" +
                "id='" + id + '\'' +
                ", nomeTesi='" + nomeTesi + '\'' +
                ", imageTesi='" + imageTesi + '\'' +
                ", realatori=" + realatori +
                ", professor=" + professor +
                ", descrizione='" + descrizione + '\'' +
                ", ambito='" + ambito + '\'' +
                ", chiave='" + chiave + '\'' +
                ", skill='" + skill + '\'' +
                ", tempistiche='" + tempistiche + '\'' +
                ", esami='" + esami + '\'' +
                ", mediaVoto='" + mediaVoto + '\'' +
                ", documents=" + documents +
                ", student=" + student +
                ", isAssigned=" + isAssigned +
                ", note='" + note + '\'' +
                '}';
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tesi tesi = (Tesi) o;
        return id.equals(tesi.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
