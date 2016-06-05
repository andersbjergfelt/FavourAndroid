package com.bjergfelt.himev5.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by andersbjergfelt on 06/05/2016.
 */
public class Applicant implements Serializable {
    String template,name;
    Date timestamp;



    public Applicant() {
    }

    public Applicant(String name, String template, Date timestamp) {
        this.name = name;
        this.template = template;
        this.timestamp = timestamp;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
