package com.bjergfelt.himev5.model;

/**
 * Created by ABjergfelt on 03-06-2016.
 */
public class Applicant {

    public String user = "";


    public Applicant(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
