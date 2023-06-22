package it.uniba.dib.sms222327.laureapp.data.model;

import androidx.annotation.Keep;

import it.uniba.dib.sms222327.laureapp.data.TaskState;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * Classe Task in cui sono raccolte le informazioni relative ad un singolo
 * task associato ad una tesi assegnata ad uno studente
 */

public class Task implements Serializable {

    private String id;
    /* Stringa che rappresenta l'id dello studente all'interno del database */
    private String studenteId;
    /* Lista di Stringhe che rappresenta l'id dei relatori all'interno del database */
    private List<String> relators;
    /* Stringa che rappresenta la descrizione nei dettagli del task assegnato */
    private String descrizione;
    /* Stringa che rappresenta il nome del task assegnato */
    private String nomeTask;
    /* Stringa che rappresenta la data limite in cui completare il task */
    private String scadenza;
    /* Attributo di tipo taskState che rappresenta uno dei quattro possibili stati di avanzamento del task */
    private TaskState stato;
    /* Stringa che rappresenta l'id della tesi all'interno del database nella quale va associato il task */
    private String tesiId;

    @Keep
    public Task() {
    }

    /**
     * Costruttore della classe
     * <p>
     * consente di istanziare un nuovo task da associare ad una tesi esistente
     * <p>
     * Pre-condizioni: deve esistere una tesi con un proprio id associata ad uno studente in cui l'id dello
     * studente deve essere rintracciato all'interno del database, il relatore deve essere
     * registrato al sistema e associato con un proprio id
     * Post-condizioni: viene creato l'oggetto task
     * <p>
     * I campi che saranno oggetto di attenzione all'interno del costruttore sono:
     * <p>
     * relatore   : lista di stringhe identificatore univoco del relatore
     * tesiId     : stringa identificatrice univoca della tesi
     * studenteId : stringa identificatore univoco dello studente
     * descrizione: stringa descrittrice nei dettagli del compito assegnato allo studente
     * nomeTask   : stringa descrittore del nome del task assegnato
     * stato      : taskState descrittore dello stato di avanzamento del task (NEW,STARTED,COMPLETED,CLOSED)
     * scadenza   : stringa descrittrice del termine ultimo in cui completare il task
     */
    public Task(String studenteId, List<String> relators, String descrizione, String nomeTask, String scadenza, TaskState stato, String tesiId) {
        this.id = UUID.randomUUID().toString();
        this.studenteId = studenteId;
        this.relators = relators;
        this.descrizione = descrizione;
        this.nomeTask = nomeTask;
        this.scadenza = scadenza;
        this.stato = stato;
        this.tesiId = tesiId;
    }

    /**
     * Getter and Setter relativi agli attributi dell'oggetto task
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudenteId() {
        return studenteId;
    }

    public void setStudenteId(String studenteId) {
        this.studenteId = studenteId;
    }

    public List<String> getRelators() {
        return relators;
    }

    public void setRelators(List<String> relatore) {
        this.relators = relatore;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getNomeTask() {
        return nomeTask;
    }

    public void setNomeTask(String nomeTask) {
        this.nomeTask = nomeTask;
    }

    public String getScadenza() {
        return scadenza;
    }

    public void setScadenza(String scadenza) {
        this.scadenza = scadenza;
    }

    public TaskState getStato() {
        return stato;
    }

    public void setStato(TaskState stato) {
        this.stato = stato;
    }

    public String getTesiId() {
        return tesiId;
    }

    public void setTesiId(String tesiId) {
        this.tesiId = tesiId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", studenteId='" + studenteId + '\'' +
                ", relators=" + relators +
                ", descrizione='" + descrizione + '\'' +
                ", nomeTask='" + nomeTask + '\'' +
                ", scadenza='" + scadenza + '\'' +
                ", stato=" + stato +
                ", tesiId='" + tesiId + '\'' +
                '}';
    }
}
