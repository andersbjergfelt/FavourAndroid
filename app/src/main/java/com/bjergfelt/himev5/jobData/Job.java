package com.bjergfelt.himev5.jobData;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Job implements Parcelable {
    private String jobName;
    private String jobId;
    private String description;
    private double salary;
    private double estimatedTime;
    private String category;
    private Location locationLatLong;
    private String photo;
    private String assignedToUser;
    private boolean jobAssigned;
    private String providedByUser;

    public Job(String jobName, String jobId, String description, double salary, double estimatedTime, String category, Location locationLatLong, String photo, String assignedToUser, boolean jobAssigned, String providedByUser) {
        this.jobName = jobName;
        this.jobId = jobId;
        this.description = description;
        this.salary = salary;
        this.estimatedTime = estimatedTime;
        this.category = category;
        this.locationLatLong = locationLatLong;
        this.photo = photo;
        this.assignedToUser = assignedToUser;
        this.jobAssigned = jobAssigned;
        this.providedByUser = providedByUser;
    }


    protected Job(Parcel in) {
        jobName = in.readString();
        jobId = in.readString();
        description = in.readString();
        salary = in.readDouble();
        estimatedTime = in.readDouble();
        category = in.readString();
        locationLatLong = in.readParcelable(Location.class.getClassLoader());
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




    public String getName() {
        return jobName;
    }

    public void setName(String name) {
        this.jobName = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return salary;
    }

    public void setPrice(double price) {
        this.salary = price;
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


    public String getJobID() {
        return jobId;
    }

    public void setJobID(String jobID) {
        this.jobId = jobID;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(jobName);
        dest.writeString(jobId);
        dest.writeString(description);
        dest.writeDouble(salary);
        dest.writeDouble(estimatedTime);
        dest.writeString(category);
        dest.writeParcelable(locationLatLong, flags);
        dest.writeString(photo);
    }
}
