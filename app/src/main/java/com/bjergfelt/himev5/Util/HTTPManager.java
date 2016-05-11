package com.bjergfelt.himev5.Util;

/**
 * Making a singleton class handling all requests
 * We instatiate it when application comes up.
 */

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * We use Volley:
 * It is an HTTP library that makes networking for Android Apps easier and faster.
 * .. Automatic scheduling of network requests
 * .. Multiple concurrent network connections.
 * .. Cancel a single request
 * .. Strong ordering that makes it esasy to correctly populate your UI with data fetched async from the network
 */

public class HTTPManager {

    private static final String TAG = "HTTPManager";
    private static HTTPManager instance = null;
    //TODO Insert real url
    private static final String prefixURL = "https://openshift..";

    //for Volley API
    public RequestQueue requestQueue;

    private HTTPManager(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized HTTPManager getInstance(Context context) {
        if (instance == null) {
            instance = new HTTPManager(context);
        }
        return instance;
    }

    //Makes it a bit easier so we do not need to pass context each time

    public static synchronized HTTPManager getInstance() {
        if (null == instance) {
            throw new IllegalStateException(HTTPManager.class.getSimpleName() + "is not initialized, call getInstance(..) first!");

        }
        return instance;
    }

    public void httpGET(String suffix, final httpListener<String> listener) {
        String url = prefixURL + suffix;
        //Following code will make a json object request where the json response will start with object notation
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        if (null != response.toString())
                            listener.getResult(response.toString());

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != error.networkResponse) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    listener.getResult("false");
                }

            }
        });
        requestQueue.add(request);
    }




}
