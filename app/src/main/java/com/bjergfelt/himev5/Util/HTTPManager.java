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

    public HTTPManager() {

    }

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

                //JSONArray jsonArrayParams = new JSONArray();
                JSONObject jsonObjectParams = new JSONObject();
                jsonObjectParams.put("jobName", jobName);
                jsonObjectParams.put("jobId", jobId);
                jsonObjectParams.put("description", description);
                jsonObjectParams.put("salary", salary);
                jsonObjectParams.put("estimatedTime", estimatedTime);
                jsonObjectParams.put("category", category);

                // JSON Array for lattitude and longitude.
                JSONArray latLngArray = new JSONArray();
                // JSON object for lattitude and longitude.
                JSONObject latLngObject = new JSONObject();
                latLngObject.put("lat", locationLatLng[0]);
                latLngObject.put("lng", locationLatLng[1]);
                // Insert the JSON object containing longitude and attitude into the JSON array.
                latLngArray.put(latLngObject);
                // Insert the latLngArray into the jsonObjectParams
                jsonObjectParams.put("locationLatLng", latLngArray);

                // JSON Array for photo.
                JSONArray photoArray = new JSONArray();
                // JSON object for photo and content type.
                JSONObject photoObject = new JSONObject();
                photoObject.put("image", encodedImage);
                photoObject.put("contentType", "JPEG");
                // Insert the JSON object containing photo and content type into the JSON array.
                photoArray.put(photoObject);
                // Insert the photoArray into the jsonObjectParams
                jsonObjectParams.put("photo", photoArray);

                jsonObjectParams.put("jobAssigned", jobAssigned);
                jsonObjectParams.put("assignedToUser", assignedToUser);
                jsonObjectParams.put("providedByUser", providedByUser);


                Log.e("JSON: ", jsonObjectParams.toString());

                String postUrl = "http://localhost:3000/jobs/addNewJob";

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, postUrl, jsonObjectParams, new Response.Listener<JSONObject>() {
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


        } else {

            try {
                JSONObject jsonObjectParams = new JSONObject();
                jsonObjectParams.put("jobName", jobName);
                jsonObjectParams.put("jobId", jobId);
                jsonObjectParams.put("description", description);
                jsonObjectParams.put("salary", salary);
                jsonObjectParams.put("estimatedTime", estimatedTime);
                jsonObjectParams.put("category", category);

                // JSON Array for lattitude and longitude.
                JSONArray latLngArray = new JSONArray();
                // JSON object for lattitude and longitude.
                JSONObject latLngObject = new JSONObject();
                latLngObject.put("lat", locationLatLng[0]);
                latLngObject.put("lng", locationLatLng[1]);
                // Insert the JSON object containing longitude and attitude into the JSON array.
                latLngArray.put(latLngObject);
                // Insert the latLngArray into the jsonObjectParams
                jsonObjectParams.put("locationLatLng", latLngArray);

                jsonObjectParams.put("jobAssigned", jobAssigned);
                jsonObjectParams.put("assignedToUser", assignedToUser);
                jsonObjectParams.put("providedByUser", providedByUser);

                String postUrl = "http://localhost:3000/jobs/addNewJob";

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, postUrl, jsonObjectParams, new Response.Listener<JSONObject>() {
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
}



