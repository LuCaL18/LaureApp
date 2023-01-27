package com.uniba.mobile.cddgl.laureapp.data.model;

public class ClassificaTesi {

    private Tesi[] classificaTesi;
    private LoggedInUser user;

    public ClassificaTesi (Tesi[] classificaTesi, LoggedInUser user) {
        this.classificaTesi = classificaTesi;
        this.user = user;
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
}
