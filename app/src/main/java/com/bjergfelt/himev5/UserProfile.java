package com.bjergfelt.himev5;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by andersbjergfelt on 05/06/2016.
 */
public class UserProfile implements Serializable {
    String firstName,lastName,city,photo;
    int rating;


    public UserProfile(String firstName, String lastName, String city, int rating, String photo) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.rating = rating;
        this.photo = photo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
