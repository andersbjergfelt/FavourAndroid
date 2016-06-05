package com.bjergfelt.himev5.Util;

/**
 * Making a singleton class handling all requests
 * We instatiate it when application comes up.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bjergfelt.himev5.LocalDB.DBHandler;
import com.bjergfelt.himev5.Model.Job;
import com.bjergfelt.himev5.Fragments.JobListFragment;
import com.bjergfelt.himev5.Model.UserProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * We use Volley:
 * It is an HTTP library that makes networking for Android Apps easier and faster.
 * .. Automatic scheduling of network requests
 * .. Multiple concurrent network connections.
 * .. Cancel a single request
 * .. Strong ordering that makes it esasy to correctly populate your UI with data fetched async from the network
 */

public class HTTPManager {
    private Context mContext;
    private static final String TAG = "HTTPManager";
    private static HTTPManager instance = null;
    private JobListFragment jobListFragment = new JobListFragment();
    DBHandler dbHandler = new DBHandler(mContext);
    List<UserProfile> userProfiles = new ArrayList<>();

    public HTTPManager() {

    }

    //for Volley API
    public RequestQueue requestQueue;
    static ArrayList<Job> jobList = new ArrayList<>();

    public HTTPManager(Context context) {
        mContext = context;

    }


    //Makes it a bit easier so we do not need to pass context each time

    public static synchronized HTTPManager getInstance() {
        if (null == instance) {
            throw new IllegalStateException(HTTPManager.class.getSimpleName() + "is not initialized, call getInstance(..) first!");

        }
        return instance;
    }


    public void addNewJob(String jobName, final String jobId, String description, int salary,
                          int estimatedTime, String category, Double[] locationLatLng,
                          Bitmap photo, boolean jobAssigned, String assignedToUser,
                          String providedByUser) {
        if (photo != null) {
            try {
                ByteArrayOutputStream blob = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 80, blob);
                byte[] imageBytes = blob.toByteArray();
                final String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                //JSONArray jsonArrayParams = new JSONArray();
                JSONObject jsonObjectParams = new JSONObject();
                jsonObjectParams.put("jobName", jobName);
                jsonObjectParams.put("jobId", "");
                jsonObjectParams.put("description", description);
                String salaryString = String.valueOf(salary);
                jsonObjectParams.put("salary", salaryString);
                String estimatedTimeString = String.valueOf(estimatedTime);
                jsonObjectParams.put("estimatedTime", estimatedTimeString);
                jsonObjectParams.put("category", category);
                // JSON object for lattitude and longitude.
                jsonObjectParams.put("lat", ""+locationLatLng[0]);
                jsonObjectParams.put("lng", ""+locationLatLng[1]);
                // Insert the JSON object containing longitude and attitude into the JSON array.
                jsonObjectParams.put("photoData", encodedImage);
                jsonObjectParams.put("photoContent", "JPEG");
                jsonObjectParams.put("jobAssigned", "");
                jsonObjectParams.put("assignedToUser", assignedToUser);
                jsonObjectParams.put("providedByUser", providedByUser);




                String postUrl = "http://apifavour-ab207.rhcloud.com/jobs/addNewJob";

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, postUrl, jsonObjectParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w("POST: ", "success!");

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.e("POST: ", "error! " + error.networkResponse);
                    }
                });

                Volley.newRequestQueue(mContext).add(jsonRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {

            try {

                Map<String, String> params = new HashMap<>();
                params.put("jobName", jobName);
                params.put("jobId", jobId);
                params.put("description", description);
                String salaryString = String.valueOf(salary);
                String estimatedTimeString = String.valueOf(estimatedTime);
                params.put("salary", salaryString);
                params.put("estimatedTime", estimatedTimeString);
                params.put("category", category);
                params.put("lat", ""+locationLatLng[0]);
                params.put("lng", ""+locationLatLng[1]);
                params.put("photoData", "");
                params.put("photoContent", "JPEG");
                String jobAssignedString = String.valueOf(jobAssigned);
                params.put("jobAssigned", jobAssignedString);
                params.put("assignedToUser", assignedToUser);
                params.put("providedByUser", providedByUser);

                String postUrl = "http://apifavour-ab207.rhcloud.com/jobs/addNewJob";

                JSONObject jsonObject = new JSONObject(params);

                Log.e("NEW JSON: ", jsonObject.toString());
                Log.e("NEW JSON: ", jsonObject.getString("lat"));


                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, postUrl, jsonObject, new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w("POST: ", "success!");

                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.e("POST: ", "error! " + error.getMessage());
                    }
                });

                Volley.newRequestQueue(mContext).add(jsonRequest);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    public void getProfile(String email) {
        requestQueue = Volley.newRequestQueue(mContext);
        final List<UserProfile> userProfileList = new ArrayList<>();

        //------Ajax------
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, "http://apifavour-ab207.rhcloud.com/profile/getProfile/" + email, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            String firstName = response.getString("firstname");
                            String lastName = response.getString("lastname");
                            String city = response.getString("city");
                            int rating = response.getInt("rating");
                            String photo = response.getString("photo");

                            // Add new job with all the properties.
                            //Job job = new Job(jobName, jobId, description, salary, estimatedTime, category, location, photo, assignedToUser, jobAssigned, providedByUser);
                            UserProfile userProfile = new UserProfile(firstName, lastName, city, rating, photo);
                            userProfileList.add(userProfile);
                            userProfiles = userProfileList;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.w("ajax error", e.getMessage().toString());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w("error, getProfile", "response error listener: " + error);


            }
        });

        requestQueue.add(jsonArrayRequest);
        Log.w("AJAX", "GET PROFILE, success");


        //------Ajax slut------
    }



    public ArrayList<Job> getAllJobs() {
        requestQueue = Volley.newRequestQueue(mContext);
        final ArrayList<Job> jobs = new ArrayList<>();

        //------Ajax------
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "http://apifavour-ab207.rhcloud.com/jobs/getAll", null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            Log.w("response size: ", "" + response.length());
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject jsonObject = response.getJSONObject(i);
                                String jobName = jsonObject.getString("jobName");
                                String jobId = jsonObject.get("_id").toString();
                                String description = jsonObject.getString("description");
                                int salary = jsonObject.getInt("salary");
                                int estimatedTime = jsonObject.getInt("estimatedTime");
                                String category = jsonObject.getString("category");
                                // lat and lng is saved inside "locationLatLng" as two seperate properties.

                                //double lat = (double) latLngArray.get(0);
                                //double lng = (double) latLngArray.get(1);
                                Location location = new Location("jobLocation");

                                location.setLatitude(jsonObject.getDouble("lat"));
                                location.setLongitude(jsonObject.getDouble("lng"));
                                // Photo is not working properly yet, therefor outcommented.
                                String photo = jsonObject.getString("photoData");
                                boolean jobAssigned = (Boolean) jsonObject.get("jobAssigned");
                                String assignedToUser = jsonObject.getString("assignedToUser");
                                String providedByUser = jsonObject.getString("providedByUser");

                                // Add new job with all the properties.
                                Job job = new Job(jobName, jobId, description, salary, estimatedTime, category, location, photo, assignedToUser, jobAssigned, providedByUser);
                                // fx Job job = new Job(jobName, jobId, description, salary, estimatedTime, category, latLngArray, jobAssigned, assignedToUser, providedByUser);
                                // All jobs fragment
                                // fx allJobsFragment.post.add(job);
                                jobs.add(job);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.w("ajax error", e.getMessage().toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w("error, getAllJobs", "response error listener: " + error);
            }
        });
        requestQueue.add(jsonArrayRequest);
        Log.w("AJAX", "GET ALL JOB, success");
        //------Ajax slut------
        return jobs;
    }

    public List<UserProfile> getUserProfiles() {
        return userProfiles;
    }

    public void setUserProfiles(List<UserProfile> userProfiles) {
        this.userProfiles = userProfiles;
    }
}



