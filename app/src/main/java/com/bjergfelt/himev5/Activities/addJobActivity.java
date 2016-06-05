package com.bjergfelt.himev5.Activities;

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
import com.bjergfelt.himev5.Util.Constants;
import com.bjergfelt.himev5.Services.FetchAddressIntentService;
import com.bjergfelt.himev5.Util.HTTPManager;
import com.bjergfelt.himev5.Util.OwnPreferenceManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class addJobActivity extends AppCompatActivity {


    private static final int REQUEST_CHOOSE_PHOTO = 2;
    CameraUtil cameraUtil = new CameraUtil();


    //Beskriver hvor mange billeder man vil tage.
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView iv;
    private TextView textHeadline;
    private TextView textDescription;
    private TextView textPlace;
    private TextView textEstimated;
    private TextView textPrice;
    private ImageButton locationButton;
    private Bitmap photo = null;
    String mCurrentPhotoPath;

    //Util klasser, se Util package
    HTTPManager httpManager;
    OwnPreferenceManager preferenceManager;



    //Disse bruges i forbindelse med at få en adresse ud fra location objektet.
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";

    /**
     * Location objektet.
     */
    protected Location mLastLocation;

    /*
    Bliver true eller false afhængigt af om useren har lavet et request til at få adressen. True når brugeren requester adressen, og false
    når adressen er fundet.
    Brugeren kan request en adresse ved at trykke på ImageButton locationButton;
    Der er mulighed for, at brugeren kan trykke på knappen før Google Api er færdigt med at loade.  Derfor holder variablen også styr på
    brugerens intent. Hvis den er true, så vil denne activity prøve at fetche adressen efter GoogleApiClient er connected.

     */
    protected boolean mAddressRequested;


    /**
     * Formateret addresse.
     */
    protected String mAddressOutput;



    /**
     * Bruges til at få respons fra FetchAddressIntentService - se Services package
     */
    private AddressResultReceiver mResultReceiver;
    protected final static String LOCATION_KEY = "location-key";


    final Context context = this;

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

        mAddressRequested = false;
        mAddressOutput = "";
        preferenceManager = new OwnPreferenceManager(this);

        getLocationFromPrefs();
        updateValuesFromBundle(savedInstanceState);
        httpManager = new HTTPManager(this);

        //Brugerens lokation er gemt i SharedPreferences efter brugeren har logget ind. Her hentes de frem.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final double latitude = Double.longBitsToDouble(prefs.getLong("Latitude", 0));
        double longitude = Double.longBitsToDouble(prefs.getLong("Longitude", 0));


        iv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Se ApplyJobDialogFragment for
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
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Tilføj dit job", Snackbar.LENGTH_LONG)
                            .setAction("Tilføj job", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Snackbaren bruges til at tilføje et job

                                    Double[] location = {getLocationFromPrefs().getLatitude(),getLocationFromPrefs().getLongitude()};
                                    String title = textHeadline.getText().toString();
                                    String description = textDescription.getText().toString();
                                    int estimated = Integer.parseInt(textEstimated.getText().toString());
                                    int price = Integer.parseInt(textPrice.getText().toString());
                                    String providedByUser = preferenceManager.getUser().getEmail();

                                    //httpManager har addNewJob, som laver et kald til serveren.
                                    httpManager.addNewJob(title,"",description,price,estimated,"Rengøring",location, photo,false,null, providedByUser);
                                }
                            }).show();
                }
            });
        }

        //Her bliver der efterspurgt adresse ud fra lokation
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
        test();
    }

    public Location getLocationFromPrefs() {
        //Får brugerens lokation som er blevet sat ved login
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
     * Opdaterer adressen i UI.
     */
    protected void displayAddressOutput() {
        textPlace.setText(mAddressOutput);
    }

    /**
     * Viser toasten.
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
        super.onPause();
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

            scalePicture();

        } else if (requestCode == REQUEST_CHOOSE_PHOTO && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = context.getContentResolver().query(
                    selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

           photo = BitmapFactory.decodeFile(filePath);
            Log.w("Image ", "selected!");

            //Det scalerer billedet og gemmes i photo variablen

            scalePicture();
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

    public void scalePicture() {
        // Get the dimensions of the View
        int targetW = iv.getWidth();
        int targetH = iv.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        photo = bitmap;
        iv.setImageBitmap(bitmap);

    }

    private void openGallery() {
        //Åbner galleriet og afventer result - resultatet sendes til onActivityResults med requestcode
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_CHOOSE_PHOTO);
    }



    public void test(){
        textHeadline.setText("Rengøring");
        textDescription.setText("Rengøring af mit værelse");
        textEstimated.setText("2");
        textPrice.setText("200");
    }

}





