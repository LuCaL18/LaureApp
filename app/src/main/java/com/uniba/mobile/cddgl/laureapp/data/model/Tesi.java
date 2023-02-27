package com.uniba.mobile.cddgl.laureapp.data.model;

import androidx.annotation.NonNull;
import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Tesi implements Serializable, Cloneable {

    private List<String> listTask;
    private String id;
    private String nomeTesi;
    private String imageTesi;
    private List<PersonaTesi> coRelatori;
    private PersonaTesi relatore;
    private String descrizione;
    private String ambito;
    private List<String> chiavi;
    private String skill;
    private int tempistiche;
    private List<String> esami;
    private float mediaVoto;
    private List<String> documents;
    private PersonaTesi student;
    private Boolean isAssigned;
    private String note;
    private long created_at;

    public Tesi() {    }

    public Tesi(String nomeTesi, List<PersonaTesi> coRelatori, PersonaTesi relatore, String descrizione, String ambito, List<String> chiavi,
                String skill, int tempistiche, List<String> esami, float mediaVoto,
                List<String> documents, PersonaTesi student, String note) {
        this.id = UUID.randomUUID().toString();
        this.nomeTesi = nomeTesi;
        this.coRelatori = coRelatori;
        this.relatore = relatore;
        this.descrizione = descrizione;
        this.ambito = ambito;
        this.chiavi = chiavi;
        this.skill = skill;
        this.tempistiche = tempistiche;
        this.esami = esami;
        this.mediaVoto = mediaVoto;
        this.documents = documents;
        this.student = student;
        this.note = note;
        isAssigned = false;
        this.created_at = System.currentTimeMillis();

    }

    public List<String> getListTask() {
        return listTask;
    }

    public void setListTask(List<String> listTask) {
        this.listTask = listTask;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeTesi() {
        return nomeTesi;
    }

    public void setNomeTesi(String nomeTesi) {
        this.nomeTesi = nomeTesi;
    }

    public String getImageTesi() {
        return imageTesi;
    }

    public void setImageTesi(String imageTesi) {
        this.imageTesi = imageTesi;
    }

    public List<PersonaTesi> getCoRelatori() {
        return coRelatori;
    }

    public void setCoRelatori(List<PersonaTesi> coRelatori) {
        this.coRelatori = coRelatori;
    }

    public PersonaTesi getRelatore() {
        return relatore;
    }

    public void setRelatore(PersonaTesi relatore) {
        this.relatore = relatore;
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

    public List<String> getChiavi() {
        return chiavi;
    }

    public void setChiavi(List<String> chiavi) {
        this.chiavi = chiavi;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public int getTempistiche() {
        return tempistiche;
    }

    public void setTempistiche(int tempistiche) {
        this.tempistiche = tempistiche;
    }

    public List<String> getEsami() {
        return esami;
    }

    public void setEsami(List<String> esami) {
        this.esami = esami;
    }

    public float getMediaVoto() {
        return mediaVoto;
    }

    public void setMediaVoto(float mediaVoto) {
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

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
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