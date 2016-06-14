package com.bjergfelt.himev5.LocalDB;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bjergfelt.himev5.Model.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    /*
    En idé der aldrig blev til virkelighed!
    Ville gerne kunne gemme en UserProfile ned i databasen..

    */

    List<UserProfile> userProfiles = new ArrayList<>();



    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "userProfile";
    // UserProfile table name
    private static final String TABLE_PROFILE_USER = "userProfile";
    // UserProfile Table Columns
    String firstName,lastName,city,photo;
    int rating;
    private static final String KEY_ID = "id";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_CITY = "city";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_RATING = "rating";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_PROFILE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FIRSTNAME + " TEXT,"
                + KEY_LASTNAME + " TEXT," + KEY_CITY + " TEXT," + KEY_PHOTO + " TEXT," + KEY_RATING + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
        @Override
       public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE_USER);
    // Creating tables again
        onCreate(db);
    }

    // Adding projectUser
    public void addUserProfile(UserProfile userProfile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FIRSTNAME, userProfile.getFirstName());
        values.put(KEY_LASTNAME, userProfile.getLastName());
        values.put(KEY_CITY, userProfile.getCity());
        values.put(KEY_PHOTO, userProfile.getPhoto());
        values.put(KEY_RATING, userProfile.getRating());
        // Inserting Row
        db.insert(TABLE_PROFILE_USER, null, values);
        db.close(); // Closing database connection
        Log.d("PROJECTUSER","ADDED TO LOCAL DATABASE");
    }

    public List<UserProfile> getUserProfile() {
        List<UserProfile> userProfiles = new ArrayList<UserProfile>();
    // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_PROFILE_USER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

    // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserProfile userProfile = new UserProfile();

                userProfile.setFirstName(cursor.getString(1));
                userProfile.setLastName(cursor.getString(2));
                userProfile.setCity(cursor.getString(3));
                userProfile.setPhoto(cursor.getString(4));
                userProfile.setRating(cursor.getInt(5));

    // Adding contact to list
                userProfiles.add(userProfile);
                Log.d("FOUND USER", userProfile.getFirstName());
            } while (cursor.moveToNext());
        }

// return contact list
        return userProfiles;
    }




}