package com.uniba.mobile.cddgl.laureapp.data.model;

import com.uniba.mobile.cddgl.laureapp.data.BookingConstraints;
import com.uniba.mobile.cddgl.laureapp.data.BookingState;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Nullable;

public class Booking implements Serializable {

    private String id;
    private String studentId;
    private String profId;
    private String emailStudent;
    private String nameStudent;
    private String surnameStudent;
    private String idThesis;
    private String nameThesis;
    private BookingConstraints constraints;
    private BookingState state;
    private Long timestamp;

    @Nullable
    private String motivation;

    public Booking() {
    }

    public Booking(String studentId, String profId, String emailStudent, String nameStudent, String surnameStudent, String idThesis, String nameThesis, BookingConstraints constraints, @Nullable String motivation) {
        this.id = UUID.randomUUID().toString();
        this.studentId = studentId;
        this.profId = profId;
        this.emailStudent = emailStudent;
        this.nameStudent = nameStudent;
        this.surnameStudent = surnameStudent;
        this.idThesis = idThesis;
        this.nameThesis = nameThesis;
        this.constraints = constraints;
        this.motivation = motivation;
        this.state = BookingState.OPEN;
        this.timestamp = new Date().getTime();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getProfId() {
        return profId;
    }

    public void setProfId(String profId) {
        this.profId = profId;
    }

    public String getEmailStudent() {
        return emailStudent;
    }

    public void setEmailStudent(String emailStudent) {
        this.emailStudent = emailStudent;
    }

    public String getNameStudent() {
        return nameStudent;
    }

    public void setNameStudent(String nameStudent) {
        this.nameStudent = nameStudent;
    }

    public String getSurnameStudent() {
        return surnameStudent;
    }

    public void setSurnameStudent(String surnameStudent) {
        this.surnameStudent = surnameStudent;
    }

    public String getIdThesis() {
        return idThesis;
    }

    public void setIdThesis(String idThesis) {
        this.idThesis = idThesis;
    }

    public String getNameThesis() {
        return nameThesis;
    }

    public void setNameThesis(String nameThesis) {
        this.nameThesis = nameThesis;
    }

    public BookingState getState() {
        return state;
    }

    public void setState(BookingState state) {
        this.state = state;
    }

    @Nullable
    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(@Nullable String motivation) {
        this.motivation = motivation;
    }

    public BookingConstraints getConstraints() {
        return constraints;
    }

    public void setConstraints(BookingConstraints constraints) {
        this.constraints = constraints;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id='" + id + '\'' +
                ", studentId='" + studentId + '\'' +
                ", profId='" + profId + '\'' +
                ", emailStudent='" + emailStudent + '\'' +
                ", nameStudent='" + nameStudent + '\'' +
                ", surnameStudent='" + surnameStudent + '\'' +
                ", idThesis='" + idThesis + '\'' +
                ", nameThesis='" + nameThesis + '\'' +
                ", constraints=" + constraints +
                ", state=" + state +
                ", timestamp=" + timestamp +
                ", motivation='" + motivation + '\'' +
                '}';
    }
}
