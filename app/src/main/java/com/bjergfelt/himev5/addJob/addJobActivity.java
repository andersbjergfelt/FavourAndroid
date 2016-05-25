package com.bjergfelt.himev5.addJob;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bjergfelt.himev5.R;
import com.bjergfelt.himev5.Util.CameraUtil;
import com.bjergfelt.himev5.Util.HTTPManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class addJobActivity extends AppCompatActivity {
    private static final int REQUEST_CHOOSE_PHOTO = 2;
    CameraUtil cameraUtil = new CameraUtil();
    //Defines how many pictures we want to capture
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView iv;
    private TextView textHeadline;
    private TextView textDescription;
    private TextView textPlace;
    private TextView textEstimated;
    private TextView textPrice;
    private ImageButton locationButton;
    private Bitmap photo;
    String mCurrentPhotoPath;
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";
    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     * The user requests an address by pressing the Fetch Address button. This may happen
     * before GoogleApiClient connects. This activity uses this boolean to keep track of the
     * user's intent. If the value is true, the activity tries to fetch the address as soon as
     * GoogleApiClient connects.
     */
    protected boolean mAddressRequested;
    /**
     * The formatted location address.
     */
    protected String mAddressOutput;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;

    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    final Context context = this;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);
        mResultReceiver = new AddressResultReceiver(new Handler());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        iv = (ImageView) findViewById(R.id.mImageView);
        textHeadline = (TextView) findViewById(R.id.add_headline);
        textDescription = (TextView) findViewById(R.id.add_description);
        textEstimated = (TextView) findViewById(R.id.add_estimated);
        textPlace = (TextView) findViewById(R.id.add_place);
        textPrice = (TextView) findViewById(R.id.add_price);
        locationButton = (ImageButton) findViewById(R.id.imageButton);
        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mAddressOutput = "";
        getLocationFromPrefs();
        updateValuesFromBundle(savedInstanceState);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        double latitude = Double.longBitsToDouble(prefs.getLong("Latitude", 0));
        double longitude = Double.longBitsToDouble(prefs.getLong("Longitude", 0));


        //buildGoogleApiClient();
        iv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Capture picture or choose from gallery");

                        builder.setItems(R.array.dialogPicture_array, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch (which){

                                    case 0: dispatchTakePictureIntent();

                                    case 1: openGallery();

                                }
                            }
                        });
                AlertDialog alertDialog = builder.create();

                // show it
                alertDialog.show();

                //dispatchTakePictureIntent();
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("AddJob", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                              //  DataProvider.addJob("123", textHeadline.getText().toString(), textDescription.getText().toString(), Double.parseDouble(textPrice.getText().toString()), Double.parseDouble(textEstimated.getText().toString()), "Handy", textPlace.getText().toString(), photo, mLastLocation);

                            }
                        }).show();
            }
        });


        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // We only start the service to fetch the address if GoogleApiClient is connected.
                if (mLastLocation != null) {
                    startIntentService();
                }
                mAddressRequested = true;

                displayAddressOutput();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //displayLocation();
    }

    public Location getLocationFromPrefs() {
        //Restoring preferences from SplashScreen
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        double latitude = Double.longBitsToDouble(prefs.getLong("Latitude", 0));
        double longitude = Double.longBitsToDouble(prefs.getLong("Longitude", 0));
        Log.d("PREFS", "" + latitude + "," + longitude);
        mLastLocation = new Location("location");
        mLastLocation.setLatitude(latitude);
        mLastLocation.setLongitude(longitude);
        return mLastLocation;
    }

    /**
     * Updates fields based on data stored in the bundle
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i("ADDJOB", "Updating values from bundle");
        if (savedInstanceState != null) {

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mLastLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        textPlace.setText(mAddressOutput);
    }

    /**
     * Shows a toast with the given text.
     */
    protected void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();


    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

        // Release the Camera because we don't need it when paused
        // and other activities might need to use it.
        /*if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }*/

        /*
        You should keep the amount of operations done in the onPause() method relatively simple
        in order to allow for a speedy transition to the user's next destination if your activity is actually being stopped.
         */
    }

    /**
     * Stores activity data in the Bundle.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);
        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        savedInstanceState.putParcelable(LOCATION_KEY, getLocationFromPrefs());
        super.onSaveInstanceState(savedInstanceState);
    }


    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                showToast(getString(R.string.address_found));
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;

        }
    }

    /*
     CAMERA
    */
    //returns a unique file name for a new photo using a date-time stamp:
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
           /* Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            iv.setImageBitmap(imageBitmap);*/
            cameraUtil.scalePicture(photo,iv, mCurrentPhotoPath);

        } else if (requestCode == REQUEST_CHOOSE_PHOTO && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = context.getContentResolver().query(
                    selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();

           photo  = BitmapFactory.decodeFile(filePath);
            Log.w("Image ", "selected!");

            //Coverimage scaleres billedet og gemmes i coverimage-variablen
            cameraUtil.scalePicture(photo,iv,filePath);

            //Posten sendes først når der klikkes på send!
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void openGallery() {
        //Åbner galleriet og afventer result - resultatet sendes til onActivityResults med requestcode
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_CHOOSE_PHOTO);
    }

    public void addJob (String jobName, String jobId, String description, int salary,
                        int estimatedTime, String category, String[] locationLatLng,
                        Bitmap photo, boolean jobAssigned, String assignedToUser,
                        String providedByUser) {

        // HTTPManage instanse.
        HTTPManager httpManager = new HTTPManager();
        // Call httpManager method addNewJob and include required variables.
        httpManager.addNewJob(jobName, jobId, description, salary, estimatedTime, category, locationLatLng,
                photo, jobAssigned, assignedToUser, providedByUser);

    }

    public void getAllUserJobs() {
        
    }

}





