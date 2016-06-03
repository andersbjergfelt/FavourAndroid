package com.bjergfelt.himev5.Util;

/**
 * Making a singleton class handling all requests
 * We instatiate it when application comes up.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bjergfelt.himev5.jobData.DataProvider;
import com.bjergfelt.himev5.jobData.Job;
import com.bjergfelt.himev5.jobData.JobListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
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

    private String jobID = "";
    //TODO Insert real url
    private JobListFragment jobListFragment = new JobListFragment();

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


    public void addNewJob(String jobName, final String jobId, String description, int salary,
                          int estimatedTime, String category, Double[] locationLatLng,
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
                jsonObjectParams.put("jobId", "");
                jsonObjectParams.put("description", description);
                String salaryString = String.valueOf(salary);
                jsonObjectParams.put("salary", salaryString);
                String estimatedTimeString = String.valueOf(estimatedTime);
                jsonObjectParams.put("estimatedTime", estimatedTimeString);
                jsonObjectParams.put("category", category);

                // JSON Array for lattitude and longitude.
                JSONArray latLngArray = new JSONArray();
                // JSON object for lattitude and longitude.
                JSONObject latLngObject = new JSONObject();
                latLngObject.put("lng", "" + locationLatLng[1]);
                latLngObject.put("lat", "" + locationLatLng[0]);

                // Insert the JSON object containing longitude and attitude into the JSON array.
                //latLngArray.put(latLngObject);
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

                jsonObjectParams.put("jobAssigned", "");
                jsonObjectParams.put("assignedToUser", assignedToUser);
                jsonObjectParams.put("providedByUser", providedByUser);




                String postUrl = "http://apifavour-ab207.rhcloud.com/jobs/addNewJob";

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, postUrl, jsonObjectParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w("POST: ", "success!");
                        try {
                            jobID = response.get("_id").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
                Log.e(" NEWJSON: ", jsonObject.getString("lat"));


                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, postUrl, jsonObject, new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w("POST: ", "success!");
                        try {
                            jobID = response.get("_id").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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


    /*public void settingJobID(String jobID){
        String postUrl = "http://apifavour-ab207.rhcloud.com/jobs/editJob/"+jobID;

        Map<String,String> params = new HashMap<>();


        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, postUrl, jsonObjectParams, new Response.Listener<JSONObject>() {




            @Override
            public void onResponse(JSONObject response) {
                Log.w("POST: ", "success!");
                try {
                    jobID = response.get("_id").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("POST: ", "error! " + error.networkResponse);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Content-Type", "application/json; charset=UTF-8");

                return map;
            }
        };

        Volley.newRequestQueue(mContext).add(jsonRequest);
    } catch (JSONException e) {
        e.printStackTrace();
    }
    }*/
}



