package com.bjergfelt.himev5.Activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bjergfelt.himev5.R;
import com.bjergfelt.himev5.Util.HTTPManager;
import com.bjergfelt.himev5.Util.OwnPreferenceManager;
import com.bjergfelt.himev5.Model.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int MY_LOCATION_REQUEST = 1;

    static Location mLastLocation;
    private Button registerButton;


    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private OwnPreferenceManager ownPreferenceManager;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private UserRegisterTask mAuthRegister = null;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private HTTPManager httpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        buildGoogleApiClient();
        ownPreferenceManager = new OwnPreferenceManager(this);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        Typeface roboto = Typeface.createFromAsset(getAssets(),
                "font/Roboto-Thin.ttf");
        mEmailView.setTypeface(roboto);

        httpManager = new HTTPManager(this);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mPasswordView.setText("Ab1234!");

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        assert mEmailSignInButton != null;
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);




        registerButton = (Button) findViewById(R.id.registerButton);

        assert registerButton != null;
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });


    }

    protected synchronized void buildGoogleApiClient() {
        Log.e("LOGIN", "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

       /* boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {*/
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
    }

    public void attemptRegister(){
        if (mAuthRegister != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        showProgress(true);
        mAuthRegister = new UserRegisterTask(email, password);
        mAuthRegister.execute((Void) null);
    }



    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private Location getLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        Log.e("LOCATION", "Location FOUND:" + mLastLocation);
        return mLastLocation;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e("LOCATION", "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        Log.e("API CONNECTED", "CONNECTED");
        // Once connected with google api, get the location
        getLocation();
        startLocationUpdates();

    }

    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation


            // for ActivityCompat#requestPermissions for more details.



            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_REQUEST);

            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.e("API CONNECTED", "UPDATES");
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getLocation();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }


        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        /*
        Storing location values in SharedPreferences to use it in different activites and removing redundant calls to locationManager.
         */


    }

    @Override
    protected void onResume() {
        super.onResume();

        //checkPlayServices();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        public boolean loginAccepted;
        private final String mEmail;
        private final String mPassword;


        public boolean isLoginAccepted() {
            return loginAccepted;
        }

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Map<String, String> parameters = new HashMap<>();

            parameters.put("email", mEmail);
            parameters.put("password", mPassword);

            JSONObject jsonParams = new JSONObject(parameters);

            String loginURL = "http://apifavour-ab207.rhcloud.com/login";
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, loginURL, jsonParams, new Response.Listener<JSONObject>() {


                @Override
                public void onResponse(JSONObject response) {

                    Log.d("LOGIN", "" + response);

                    try {
                        loginAccepted = !response.get("response").equals("Invalid password") && !response.get("response").equals("User does not exist, please try again") ;
                        if (loginAccepted){
                            OwnPreferenceManager ownPreferenceManager = new OwnPreferenceManager(getApplicationContext());
                            ownPreferenceManager.clear();
                            User user = new User(null, null, mEmail);
                            ownPreferenceManager.storeUser(user);
                            //httpManager.getProfile(mEmail);

                            Log.d("LOGIN", user.getEmail());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.e("LOGIN: ", "error! " + error.getMessage());
                    loginAccepted = false;
                }
            });

            Volley.newRequestQueue(getApplicationContext()).add(jsonRequest);
                try{
                    Thread.sleep(3000);
                    showProgress(true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            return isLoginAccepted();
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //ownPreferenceManager.getUser().setEmail(mEmail);
                finish();

                if (ownPreferenceManager.haveUserMadeProfile()) {
                    //httpManager.getProfile(ownPreferenceManager.getUser().getEmail());
                    Intent myIntent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(myIntent);

                }else{
                    Intent myIntent = new Intent(getApplicationContext(),SetupProfileActivity.class);
                    startActivity(myIntent);
                }




            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {
        public boolean registerAccepted;
        private final String mEmail;
        private final String mPassword;


        public boolean isRegisterAccepted() {
            return registerAccepted;
        }

        UserRegisterTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Map<String, String> parameters = new HashMap<>();

            parameters.put("email", mEmail);
            parameters.put("password", mPassword);

            JSONObject jsonParams = new JSONObject(parameters);

            String loginURL = "http://apifavour-ab207.rhcloud.com/register";
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, loginURL, jsonParams, new Response.Listener<JSONObject>() {


                @Override
                public void onResponse(JSONObject response) {

                    Log.d("Register", "" + response);

                    try {
                        registerAccepted = !response.get("response").equals("Something went wrong...") && !response.get("response").equals("email already registered") && !response.get("response").equals("Password is too weak") && !response.get("response").equals("entered email is not valid") ;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.e("Register: ", "error! " + error.getMessage());
                    registerAccepted = false;
                }
            });

            Volley.newRequestQueue(getApplicationContext()).add(jsonRequest);
            try{
                Thread.sleep(3000);
                showProgress(true);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (isRegisterAccepted()) {
                ownPreferenceManager.clear();
                OwnPreferenceManager ownPreferenceManager = new OwnPreferenceManager(getApplicationContext());
                User user = new User(null, null, mEmail);
                ownPreferenceManager.storeUser(user);
            }
            return isRegisterAccepted();
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //ownPreferenceManager.getUser().setEmail(mEmail);
                finish();
                Intent myIntent = new Intent(getApplicationContext(),SetupProfileActivity.class);
                startActivity(myIntent);


            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

