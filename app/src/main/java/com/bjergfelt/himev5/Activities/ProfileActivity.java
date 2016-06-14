package com.bjergfelt.himev5.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bjergfelt.himev5.LocalDB.DBHandler;
import com.bjergfelt.himev5.Model.UserProfile;
import com.bjergfelt.himev5.R;
import com.bjergfelt.himev5.Util.HTTPManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ProfileActivity extends AppCompatActivity {

    private String TAG = ProfileActivity.class.getSimpleName();

    private Button viewProfile;
    private List<UserProfile> userProfileList = new ArrayList<>();
    private RequestQueue requestQueue;
    private HTTPManager httpManager;
    private DBHandler dbHandler = new DBHandler(this);
    UserProfile userProfile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        httpManager = new HTTPManager(this);
        final String email = intent.getStringExtra("name");
        getProfile(email);
        viewProfile = (Button) findViewById(R.id.seeProfileButton);
        //httpManager.getProfile(email);
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserProfile userProfile = userProfileList.get(0);
                Intent intent = new Intent(ProfileActivity.this, ProfileDetailsActivity.class);

                intent.putExtra("profile", userProfile);

                startActivity(intent);
            }
        });


    }

    public void getProfile(String email) {
        requestQueue = Volley.newRequestQueue(this);
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

                            UserProfile userProfile = new UserProfile(firstName, lastName, city, rating, photo);
                            userProfileList.add(userProfile);
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


    }
}
