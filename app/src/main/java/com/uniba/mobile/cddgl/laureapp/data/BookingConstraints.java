package com.uniba.mobile.cddgl.laureapp.data;

import androidx.annotation.Keep;

/**
 * Classe che rappresenta l'istanza dei vincoli soddisfatti di una prenotazione
 */
public class BookingConstraints {

    private boolean timelines;
    private boolean averageGrade;
    private boolean necessaryExam;
    private boolean skills;

    @Keep
    public BookingConstraints() {
    }

    public BookingConstraints(boolean timelines, boolean averageGrade, boolean necessaryExam, boolean skills) {
        this.timelines = timelines;
        this.averageGrade = averageGrade;
        this.necessaryExam = necessaryExam;
        this.skills = skills;
    }

    public boolean isTimelines() {
        return timelines;
    }

    public void setTimelines(boolean timelines) {
        this.timelines = timelines;
    }

    public boolean isAverageGrade() {
        return averageGrade;
    }

    public void setAverageGrade(boolean averageGrade) {
        this.averageGrade = averageGrade;
    }

    public boolean isNecessaryExam() {
        return necessaryExam;
    }

    public void setNecessaryExam(boolean necessaryExam) {
        this.necessaryExam = necessaryExam;
    }

    public boolean isSkills() {
        return skills;
    }

    public void setSkills(boolean skills) {
        this.skills = skills;
    }

    @Override
    public String toString() {
        return "BookingConstraints{" +
                "timelines=" + timelines +
                ", averageGrade=" + averageGrade +
                ", necessaryExam=" + necessaryExam +
                ", skills=" + skills +
                '}';
    }
}
