package com.bjergfelt.himev5;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bjergfelt.himev5.Util.OwnPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    Bitmap photo = null;
    private String mCurrentPhotoPath;
    //Defines how many pictures we want to capture
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CHOOSE_PHOTO = 2;
    private Button capturePicture;
    private ImageView iv;
    final Context context = this;
    private Button finishProfile;
    private EditText firstName;
    private EditText lastName;
    private EditText city;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle("Profilopsætning");
        setSupportActionBar(toolbar);
        final OwnPreferenceManager ownPreferenceManager = new OwnPreferenceManager(this);

        firstName = (EditText) findViewById(R.id.firstName_text);
        lastName = (EditText) findViewById(R.id.lastName_text);
        city = (EditText) findViewById(R.id.city_text);

        iv = (ImageView) findViewById(R.id.profilePicture);


        finishProfile = (Button) findViewById(R.id.finishProfile);
        finishProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ownPreferenceManager.getUser().getEmail();
                String profileFirstname = String.valueOf(firstName.getText());
                String profileLastName = String.valueOf(lastName.getText());
                String profile_City = String.valueOf(city.getText());

                addProfile(profileFirstname,profileLastName,profile_City,email,photo);
            }
        });


        capturePicture = (Button) findViewById(R.id.capture_profilepicture);
        capturePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on click
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Tag et billede eller vælg fra dit galleri");

                builder.setItems(R.array.dialogPicture_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {

                            case 0:
                                dispatchTakePictureIntent();

                            case 1:
                                openGallery();

                        }
                    }
                });
                AlertDialog alertDialog = builder.create();

                // show it
                alertDialog.show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

            //Coverimage scaleres billedet og gemmes i coverimage-variablen
            //cameraUtil.scalePicture(photo,iv,filePath);
            scalePicture();
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





    public void addProfile(String firstName, String lastName, String city, String email,
                           Bitmap photo) {
        if (photo != null) {
            try {
                ByteArrayOutputStream blob = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 80, blob);
                byte[] imageBytes = blob.toByteArray();
                final String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                //JSONArray jsonArrayParams = new JSONArray();

                JSONObject jsonObjectParams = new JSONObject();
                jsonObjectParams.put("email", email);
                jsonObjectParams.put("firstname", firstName);
                jsonObjectParams.put("lastname", lastName);
                jsonObjectParams.put("city", city);
                jsonObjectParams.put("photo", encodedImage);




                String postUrl = "http://apifavour-ab207.rhcloud.com/profile/createProfile";

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, postUrl, jsonObjectParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w("POST: ", "success!");

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.e("POST: ", "error! " + error.networkResponse);
                    }
                });

                Volley.newRequestQueue(this).add(jsonRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {

            try {
                JSONObject jsonObjectParams = new JSONObject();
                jsonObjectParams.put("email", email);
                jsonObjectParams.put("firstname", firstName);
                jsonObjectParams.put("lastname", lastName);
                jsonObjectParams.put("city", city);

                String postUrl = "http://apifavour-ab207.rhcloud.com/profile/createProfile";


                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, postUrl, jsonObjectParams, new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w("POST: ", "success!");

                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.e("POST: ", "error! " + error.getMessage());
                    }
                });

                Volley.newRequestQueue(this).add(jsonRequest);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
