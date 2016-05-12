package com.bjergfelt.himev5.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.bjergfelt.himev5.model.User;

/**
 * Created by andersbjergfelt on 06/05/2016.
 */



/*
 SharedPreferences is an interface for accessing and modifying preference data returned by getSharedPreferences(String,int) feks.
 Modifications to the preferences must go through an SharedPreferences.Editor object to ensure the preference values remain in a consistent state and control when they are committed to storage.
*/
public class PreferenceManager {

    private String TAG = PreferenceManager.class.getSimpleName();

    //Shared Preferences
    SharedPreferences pref;

    //Editor for Shared preferences
    SharedPreferences.Editor editor;

    //Context
    Context _context;

    //Shared pref mode
    int PRIVATE_MODE = 0;

    //Shared pref file name
    private static final String PREF_NAME = "HiMe_Favour";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";



    // All shared Preferences Keys

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_LOCATION = "user_location";
    private static final String KEY_USER_LATITUDE = "user_latitude";
    private static final String KEY_USER_LONGTITUDE = "user_longtitude";
    private static final String KEY_NOTIFICATIONS = "notifications";




    public void storeUser(User user){
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL,user.getEmail());
        editor.putLong(KEY_USER_LONGTITUDE, user.getLongtitude());
        editor.putLong(KEY_USER_LATITUDE, user.getLatitude());
        editor.commit();
        Log.e(TAG, "User is stored in shared preferences + " + user.getName());
    }


    public User getUser(){
        if (pref.getString(KEY_USER_ID,null) != null){
            String id, name, email;
            Location location = new Location("location");
            id = pref.getString(KEY_USER_ID,null);
            name = pref.getString(KEY_USER_NAME,null);
            email = pref.getString(KEY_USER_EMAIL, null);
            double latitude = Double.longBitsToDouble(pref.getLong("Latitude", 0));
            double longitude = Double.longBitsToDouble(pref.getLong("Longitude", 0));
            Log.d("PREFS", "" + latitude + "," + longitude);
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            User user = new User(id,name,email,location);
            return user;
        }
        return null;
    }

    public PreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    /*
        Methods for intro slider. Skip it if false.
     */
    public void setIsFirstTimeLaunch(boolean isFirstTime){
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }


    public void addNotification(String notification){

        //get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != null){
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications(){
        return pref.getString(KEY_NOTIFICATIONS,null);
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }
}
