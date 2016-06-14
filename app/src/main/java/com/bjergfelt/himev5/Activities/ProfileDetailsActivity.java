package com.bjergfelt.himev5.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.bjergfelt.himev5.Model.UserProfile;
import com.bjergfelt.himev5.R;

import java.util.ArrayList;
import java.util.List;

public class ProfileDetailsActivity extends AppCompatActivity {

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


        UserProfile userProfile = (UserProfile) intent.getSerializableExtra("profile");


        collapsingToolbarLayout.setTitle(userProfile.getFirstName() + " " + userProfile.getLastName());
        byte[] imageBytes = Base64.decode(userProfile.getPhoto(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        iv.setImageBitmap(bitmap);
        city.setText(userProfile.getCity());
        ratingBar.setNumStars(userProfile.getRating());


    }




}
