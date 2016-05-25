package com.bjergfelt.himev5.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

/**
 * Created by ABjergfelt on 25-05-2016.
 */
public class CameraUtil {



    public void scalePicture(Bitmap photo, ImageView iv, String mCurrentPhotoPath) {
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

}
