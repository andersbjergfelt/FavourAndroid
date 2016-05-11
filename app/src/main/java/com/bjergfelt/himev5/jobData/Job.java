package com.bjergfelt.himev5.jobData;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by andersbjergfelt on 19/02/2016.
 */
public class Job implements Parcelable {
    private String name;
    private String jobID;
    private String description;
    private double price;
    private double estimatedTime;
    private String category;
    private Location locationLatLong;
    private String location;
    private int photoId;
    private Bitmap photo;

    public Job(String id, String name, String description, double price, double estimatedTime, String handy, String s, Bitmap photo, Location location) {
        jobID = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.estimatedTime = estimatedTime;
        handy = category;
        this.location = s;
        this.photo = photo;
        locationLatLong = location;

    }

    protected Job(Parcel in) {
        name = in.readString();
        jobID = in.readString();
        description = in.readString();
        price = in.readDouble();
        estimatedTime = in.readDouble();
        category = in.readString();
        locationLatLong = in.readParcelable(Location.class.getClassLoader());
        location = in.readString();
        photoId = in.readInt();
        photo = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Job> CREATOR = new Creator<Job>() {
        @Override
        public Job createFromParcel(Parcel in) {
            return new Job(in);
        }

        @Override
        public Job[] newArray(int size) {
            return new Job[size];
        }
    };

    public Job(Job job) {
        this.name = job.getName();
        this.jobID = job.getJobID();
        this.description = job.getDescription();
        this.price = job.getPrice();
        this.estimatedTime = job.getEstimatedTime();
        this.category = job.getCategory();
        this.photo = job.getPhoto();
        locationLatLong = job.getLocationLatLong();
    }

    public void setLocation(String location) {
        this.location = location;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public Location getLocationLatLong() {
        return locationLatLong;
    }

    public void setLocation(Location location) {
        this.locationLatLong = locationLatLong;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Job(String name, String description, double price, double estimatedTime, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.estimatedTime = estimatedTime;
        this.category = category;
    }

    public Job(String jobID, String name,  String description, double price, double estimatedTime, String category, int photoId, Location location, String locationName) {
        this.name = name;
        this.jobID = jobID;
        this.description = description;
        this.price = price;
        this.estimatedTime = estimatedTime;
        this.category = category;
        this.photoId = photoId;
        locationLatLong = location;
        this.location = locationName;
    }
    public Job(String jobID, String name,  String description, double price, double estimatedTime, String category, Bitmap photo, Location location) {
        this.name = name;
        this.jobID = jobID;
        this.description = description;
        this.price = price;
        this.estimatedTime = estimatedTime;
        this.category = category;
        this.photo = photo;
        locationLatLong = location;

    }

    public Job(String jobID, String name,  String description, double price, double estimatedTime, String category, String location) {
        this.name = name;
        this.jobID = jobID;
        this.description = description;
        this.price = price;
        this.estimatedTime = estimatedTime;
        this.category = category;
        this.location = location;

    }
    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getLocation() {
        return location;
    }


    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(jobID);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeDouble(estimatedTime);
        dest.writeString(category);
        dest.writeParcelable(locationLatLong, flags);
        dest.writeString(location);
        dest.writeInt(photoId);
        dest.writeParcelable(photo, flags);
    }
}
