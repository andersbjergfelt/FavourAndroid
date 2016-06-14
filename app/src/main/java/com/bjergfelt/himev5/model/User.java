package com.bjergfelt.himev5.Model;

/**
 * Created by andersbjergfelt on 06/05/2016.
 */
import android.location.Location;

import java.io.Serializable;

public class User implements Serializable {
    String id, name, email;
    Location location;


    public User() {
    }

    public User(String id, String name, String email, Location location) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.location = location;
    }
    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
