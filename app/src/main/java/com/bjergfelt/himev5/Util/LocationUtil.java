package com.bjergfelt.himev5.Util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


/**
 * Created by ABjergfelt on 11-04-2016.
 *
 * Location helper class
 * Reusable location around in different activies. It keep activies more clean
 * Implemented with Singleton Pattern
 */


public class LocationUtil extends AppCompatActivity implements ConnectionCallbacks,
        OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    // variable to hold context
    private Context context;

    public static String TAG = "LocationManager";
    static Location mLastLocation;
    private AppCompatActivity activity;
    // Google client to interact with Google API
    private static GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static LocationUtil instance = null;

    locationInterface mMyInterfaceObject;

    private LocationUtil(AppCompatActivity activity, locationInterface myInterface) {
        this.activity = activity;
        mMyInterfaceObject = myInterface;
        Log.w(TAG, "LocationManager STARTED");
        Log.w(TAG, "Building GoogleApiClient");
        buildGoogleApiClient();
        //mGoogleApiClient.connect();
        createLocationRequest();
        //startLocationUpdates();
        getLocation();
        Log.w(TAG, "Reached end");
        Log.w(TAG, "LocationManager CREATED");
    }

    public  void connectToAPI(){
        mGoogleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Log.w(TAG, "API BUILD");
    }


    public static synchronized LocationUtil getInstance(AppCompatActivity mActivity, locationInterface myInterface) {
        if (instance == null) {
            instance = new LocationUtil(mActivity, myInterface);
        }

        Log.w(TAG, "" + instance);
        return instance;
    }

    public static synchronized LocationUtil getInstance() {
        if (null == instance) {
            throw new IllegalStateException(LocationUtil.class.getSimpleName() + "is not initialized, call getInstance(..) first!");

        }
        return instance;

    }

    public Location getLocation() {


        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return TODO;
        }

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        Log.e(TAG, "Location FOUND:" + mLastLocation);
        return mLastLocation;
    }

    public void createLocationRequest() {
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
    public void onConnected(Bundle bundle) {
        Log.w(TAG + "API CONNECTED", "CONNECTED ");
        // Once connected with google api, get the location
        getLocation();
        if (mLastLocation != null){
            mMyInterfaceObject.locationRecieved(mLastLocation);
        }
        startLocationUpdates();

    }
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest,this);

        Log.w("API CONNECTED", "UPDATES");
    }



    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        Log.w("LOCATION CHANGED", "YESS");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.w(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    public Location getNewestLocation(){

        return  mLastLocation;
    }

    public interface locationInterface {
        public void locationRecieved(Location location);
    }


}

