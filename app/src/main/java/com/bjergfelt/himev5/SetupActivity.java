package com.bjergfelt.himev5;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bjergfelt.himev5.jobData.Job;
import com.bjergfelt.himev5.jobData.OwnJobsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SetupActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private List<UserProfile> userProfileList;
    private ImageView iv;
    private TextView city;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        userProfileList = new ArrayList<>();
        iv = (ImageView) findViewById(R.id.profilePicture);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        city = (TextView) findViewById(R.id.textCity);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        UserProfile userProfile = (UserProfile) intent.getSerializableExtra("profile");


        collapsingToolbarLayout.setTitle(userProfile.getFirstName() + " " + userProfile.lastName);
        byte[] imageBytes = Base64.decode(userProfile.getPhoto(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        iv.setImageBitmap(bitmap);
        city.setText(userProfile.getCity());
        ratingBar.setNumStars(userProfile.getRating());


    }




}
