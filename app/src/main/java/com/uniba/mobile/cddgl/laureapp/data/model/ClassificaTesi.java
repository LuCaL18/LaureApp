package com.uniba.mobile.cddgl.laureapp.data.model;

public class ClassificaTesi {

    private Tesi[] classificaTesi;
    private LoggedInUser user;
    private String studentId;

    public ClassificaTesi (Tesi[] classificaTesi, LoggedInUser user) {
        this.classificaTesi = classificaTesi;
        this.user = user;
    }

    public ClassificaTesi (Tesi[] classificaTesi, String studentId) {
        this.classificaTesi = classificaTesi;
        this.studentId = studentId;
    }

    public Tesi[] getClassificaTesi() {
        return classificaTesi;
    }

    public void setClassificaTesi(Tesi[] classificaTesi) {
        this.classificaTesi = classificaTesi;
    }

    public LoggedInUser getUser() {
        return user;
    }

    public void setUser(LoggedInUser user) {
        this.user = user;
    }

    public void setStudentId(LoggedInUser loggedInUser) {
        this.studentId = loggedInUser.getId();
    }

    public String getStudentId(LoggedInUser loggedInUser) {
        return studentId;
    }
}
