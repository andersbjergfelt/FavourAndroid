package com.bjergfelt.himev5.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.bjergfelt.himev5.LocalDB.DBHandler;
import com.bjergfelt.himev5.Model.UserProfile;
import com.bjergfelt.himev5.R;
import com.bjergfelt.himev5.Util.HTTPManager;
import com.bjergfelt.himev5.Util.OwnPreferenceManager;

import java.util.ArrayList;
import java.util.List;


public class ViewOwnProfileActivity extends AppCompatActivity {

    HTTPManager httpManager;
    DBHandler dbHandler;
    private ImageView iv;
    private TextView city;
    List<UserProfile> userProfiles = new ArrayList<>();
    UserProfile userProfile;
    String email = "";
    OwnPreferenceManager ownPreferenceManager;
    private RequestQueue requestQueue;


    public ViewOwnProfileActivity() {


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ownPreferenceManager = new OwnPreferenceManager(this);
        email = ownPreferenceManager.getUser().getEmail();
        httpManager = new HTTPManager(this);
        //userProfiles  = httpManager.getProfile(email);
        httpManager.getProfile(ownPreferenceManager.getUser().getEmail());

        dbHandler = new DBHandler(this);

        iv = (ImageView) findViewById(R.id.profilePicture);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        city = (TextView) findViewById(R.id.textCity);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        Intent intent = getIntent();

        UserProfile userProfile = (UserProfile) intent.getSerializableExtra("profile");

        assert collapsingToolbarLayout != null;
        collapsingToolbarLayout.setTitle(userProfile.getFirstName() + " " + userProfile.getLastName());
        byte[] imageBytes = Base64.decode(userProfile.getPhoto(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        iv.setImageBitmap(bitmap);
        city.setText(userProfile.getCity());
        assert ratingBar != null;
        ratingBar.setNumStars(userProfile.getRating());
    }
}
