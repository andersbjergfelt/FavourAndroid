package com.bjergfelt.himev5.Util;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bjergfelt.himev5.model.User;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ABjergfelt on 03-06-2016.
 */
public class Authenticator {
    boolean loginAccepted;
    private Context context;
    OwnPreferenceManager ownPreferenceManager;

    public Authenticator(Context context) {
        this.context = context;
        ownPreferenceManager = new OwnPreferenceManager(context);
    }

    public void registerUser(final String email, String password) {

        //Laver et HashMap, da den minder om JSONObjekt struktur:

        Map<String, String> parameters = new HashMap<>();

        parameters.put("email", email);
        parameters.put("password", password);

        JSONObject params = new JSONObject(parameters);

        String registerURL = "http://apifavour-ab207.rhcloud.com/regiser";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, registerURL, params, new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {
                Log.w("AUTH: ", "success!");
                Log.d("AUTH", "" + response);
                User user = new User();
                user.setEmail(email);
                ownPreferenceManager.storeUser(user);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("AUTH: ", "error! " + error.getMessage());
            }
        });

        Volley.newRequestQueue(context).add(jsonRequest);

    }


    public void login(String email, String password){

        Map<String, String> parameters = new HashMap<>();

        parameters.put("email", email);
        parameters.put("password", password);

        JSONObject params = new JSONObject(parameters);

        String loginURL = "http://apifavour-ab207.rhcloud.com/login";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, loginURL, params, new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {
                Log.d("LOGIN: ", "success!");
                Log.d("LOGIN", "" + response);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("LOGIN: ", "error! " + error.getMessage());

            }
        });

        Volley.newRequestQueue(context).add(jsonRequest);

    }

    public boolean isLoginAccepted(){
        return loginAccepted;
    }

}