package com.bjergfelt.himev5.jobData;

import android.graphics.Bitmap;
import android.location.Location;

import com.bjergfelt.himev5.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andersbjergfelt on 19/02/2016.
 */

//Just lot of dummy data
    //TODO Exhange it with a database
public class DataProvider {
     static Location locStenloese = new Location("stenloese");
     static Location locFrederikssund = new Location("frederikssund");
     static Location locFrederiksberg = new Location("frederiksberg");
     static Location locHolte = new Location("holte");
     static Location locRanders = new Location("randers");
     static Location locHundested = new Location("hundested");

    public DataProvider(){
        locStenloese.setLongitude(12.19831);
        locStenloese.setLatitude(55.76862);
        locFrederikssund.setLongitude(12.06244);
        locFrederikssund.setLatitude(55.83559);
        locFrederiksberg.setLongitude(12.51332);
        locFrederiksberg.setLatitude(55.67707);
        locHolte.setLongitude(12.47225);
        locHolte.setLatitude(55.81725);
        locRanders.setLongitude(10.03654);
        locRanders.setLatitude(56.46058);
        locHundested.setLongitude(11.85896);
        locHundested.setLatitude(55.96775);
    }

    public static ArrayList<Job> jobList = new ArrayList<>();
    public static Map<String, Job> jobMap = new HashMap<>();
    static {


        addJob("cleaning", "Clean my apartment",
                "Need cleaning in my apartment",
                200, 3, "Cleaning", R.drawable.cleaning, locStenloese, "Stenløse");
        addJob("paintroller","Paint my room",
                "Need to paint my room black",
                600, 5, "Painting", R.drawable.paintroller,locFrederikssund, "Frederikssund");
        addJob("handtruck","Help with moving",
                "Need to move some stuff to my new apartment",
                200, 2, "Moving", R.drawable.handtruck,locFrederiksberg, "Frederiksberg");
        addJob("grass","Mow my lawn",
                "Need somebody to mow my lawn",
                200, 1, "Cleaning",R.drawable.grass, locHolte, "Holte");
        addJob("kitchenscale","Bake cakes ",
                "Need somebody to bake some cakes for me",
                300, 2, "Cooking", R.drawable.kitchenscale,locRanders, "Randers");
        addJob("handtruck","Shopping ",
                "Need somebody to shop for me as I cant make it today",
                300, 1, "Shopping", R.drawable.handtruck, locHundested, "Hundested");


    }

    public static List<String> getJobNames() {
        List<String> list = new ArrayList<>();
        for (Job job : jobList) {
            list.add(job.getName());
        }
        return list;
    }


    public static void addJob(String id, String name, String description, double price, double estimatedTime, String category, int photoId, Location location, String locationName) {
        Job job = new Job(id, name, description, price, estimatedTime, category, photoId, location, locationName);
        jobList.add(job);
        jobMap.put(id, job);
    }

    public static void addJob(String id, String name, String description, double price, double estimatedTime, String category, String location) {
        Job job = new Job(id, name, description, price, estimatedTime, category, location);
        jobList.add(job);
        jobMap.put(id, job);
    }






    public static List<Job> getFilteredList(String searchString) {

        List<Job> filteredList = new ArrayList<>();
        for (Job job : jobList) {
            if (job.getCategory().contains(searchString)) {
                filteredList.add(job);
            }
        }

        return filteredList;

    }

    public static void addJob(String id, String name, String description, double price, double estimatedTime, String handy, String s, Bitmap photo, Location location) {
        Job job = new Job(id,name,description,price,estimatedTime,handy,s,photo, location);
        jobList.add(job);
        jobMap.put(id,job);
    }


    public  ArrayList<Job> getJobList() {
        return jobList;
    }

    public  Map<String, Job> getJobMap() {
        return jobMap;
    }
}