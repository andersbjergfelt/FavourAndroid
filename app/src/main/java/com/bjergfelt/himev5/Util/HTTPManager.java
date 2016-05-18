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

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.bjergfelt.himev5.jobData.DataProvider;
import com.bjergfelt.himev5.jobData.Job;
import com.bjergfelt.himev5.jobData.JobFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    //TODO Insert real url
    private JobFragment jobFragment = new JobFragment();
    public ArrayList<Job> getJobList() {
        return jobList;
    }


    private static final String prefixURL = "http://favour-godeting.rhcloud.com";
    DataProvider dataProvider = new DataProvider();
    //for Volley API
    public RequestQueue requestQueue;
    static ArrayList<Job> jobList = new ArrayList<>();

    public HTTPManager(Context context) {
        mContext = context;
        //requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }


    //Makes it a bit easier so we do not need to pass context each time

    public static synchronized HTTPManager getInstance() {
        if (null == instance) {
            throw new IllegalStateException(HTTPManager.class.getSimpleName() + "is not initialized, call getInstance(..) first!");

        }
        return instance;
    }

    public void getAllJobs() {
        requestQueue = Volley.newRequestQueue(mContext);

        //------Ajax------
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "http://favour-godeting.rhcloud.com/jobs/getAll", null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            Log.w("response size: ", "" + response.length());
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);

                                String jobName = jsonObject.getString("jobName");
                                String jobId = jsonObject.getString("jobId");
                                String description = jsonObject.getString("description");
                                int salary = jsonObject.getInt("salary");
                                int estimatedTime = jsonObject.getInt("estimatedTime");
                                String category = jsonObject.getString("category");
                                // lat and lng is saved inside "locationLatLng" as two seperate properties.

                                //double lat = (double) latLngArray.get(0);
                                //double lng = (double) latLngArray.get(1);
                                Location location = new Location("jobLocation");
                                location.setLatitude(12.12);
                                location.setLongitude(40.12);
                                // Photo is not working properly yet, therefor outcommented.
                                String photo = jsonObject.getString("photo");
                                boolean jobAssigned = (Boolean) jsonObject.get("jobAssigned");
                                String assignedToUser = jsonObject.getString("assignedToUser");
                                String providedByUser = jsonObject.getString("providedByUser");

                                // Add new job with all the properties.
                                Job job = new Job(jobName, jobId, description, salary, estimatedTime, category, location, photo, assignedToUser, jobAssigned, providedByUser);
                                // fx Job job = new Job(jobName, jobId, description, salary, estimatedTime, category, latLngArray, jobAssigned, assignedToUser, providedByUser);
                                // All jobs fragment
                                // fx allJobsFragment.post.add(job);
                                JobFragment.jobs.add(job);

                            }

                            //Vi refresher listen, da dataen først nu er kommet ind, og er klar til visning.


                            JobFragment.refreshList();
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
        Log.w("AJAX", "GET ALL POSTS, success");
        //------Ajax slut------
    }

    public void addNewJob(String jobName, String jobId, String description, int salary,
                          int estimatedTime, String category, String[] locationLatLng,
                          Bitmap photo, boolean jobAssigned, String assignedToUser,
                          String providedByUser) {
        if (photo != null) {
            try {
                ByteArrayOutputStream blob = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, blob);
                byte[] imageBytes = blob.toByteArray();
                final String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                JSONArray jsonArrayParams = new JSONArray();
                JSONObject jsonObjectParams = new JSONObject();
                jsonObjectParams.put("jobName", jobName);
                jsonObjectParams.put("jobId", jobId);
                jsonObjectParams.put("description", description);
                jsonObjectParams.put("salary", salary);
                jsonObjectParams.put("estimatedTime", estimatedTime);
                jsonObjectParams.put("category", category);


                jsonArrayParams.put(jsonObjectParams);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}

