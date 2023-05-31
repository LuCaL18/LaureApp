package com.uniba.mobile.cddgl.laureapp.data;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

public class PersonaTesi implements Serializable {

    private String id;
    private String displayName;
    private String email;

    @Nullable
    private List<Integer> permissions;

    public PersonaTesi() {
    }

    public PersonaTesi(String id, String displayName, String email, List<Integer> permissions) {
        this.id = id;
        this.displayName = displayName;
        this.email = email;
        this.permissions = permissions;
    }

    public PersonaTesi(String id, String displayName, String email) {
        this.id = id;
        this.displayName = displayName;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Nullable
    public List<Integer> getPermissions() {
        return permissions;
    }

    public void setPermissions(@Nullable List<Integer> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonaTesi that = (PersonaTesi) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PersonaTesi{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}
