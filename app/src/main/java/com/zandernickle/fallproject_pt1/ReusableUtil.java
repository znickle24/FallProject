package com.zandernickle.fallproject_pt1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;

public class ReusableUtil {

    public static void attemptImageCapture(Activity activity, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(intent, requestCode);
        }
    }

    @Nullable
    public static Bitmap onImageCaptureResult(Activity activity, Intent data, int resultCode,
                                              @Nullable String errorMessage) {
        Bitmap image = null;
        if (resultCode == RESULT_OK) {
            image = (Bitmap) data.getExtras().get("data");
        } else {
            String defaultError = "Oops. Something went wrong. Please try again.";
            errorMessage = errorMessage == null ? defaultError : errorMessage;
            Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
        }
        return image;
    }

    /**
     * Compresses a Bitmap to a ByteArray and saves it to a Bundle. Intended for use with the
     * ReusableUtil.bitmapFromBundle method or where a ByteArray is to be decoded to a Bitmap
     * after extraction from the Bundle.
     *
     * @param bundle the Bundle object to which to save the compressed Bitmap.
     * @param bitmap the Bitmap to compress (convert to ByteArray) and save to the Bundle.
     * @param key the Key to access the compressed Bitmap from the Bundle.
     */
    public static void bitmapToBundle(Bundle bundle, Bitmap bitmap, String key) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // The quality argument is functionally irrelevant for PNG files (loss-less compression).
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] compressedImage = outputStream.toByteArray();
        bundle.putByteArray(key, compressedImage);
    }

    /**
     * Extracts a compressed Bitmap from a Bundle. Intended for use with the
     * ReusableUtil.bitmapToBundle method or where the Bitmap is to be decoded from a ByteArray.
     *
     * This method will fail if attempting to extract a Bitmap directly from the Bundle. The
     * Bitmap must have been saved to the Bundle as a ByteArray. If the Bundle does not contain
     * the specified key, the returned Bitmap will be null.
     *
     * @param bundle the Bundle object from which to extract the compressed Bitmap.
     * @param key the Key to access the compressed Bitmap from the Bundle.
     * @return the Bitmap
     */
    @Nullable
    public static Bitmap bitmapFromBundle(Bundle bundle, String key) {
        byte[] compressedImage = bundle.getByteArray(key);
        return BitmapFactory.decodeByteArray(compressedImage, 0, compressedImage.length);
    }

    /**
     * Initializes clickable Views. Use this method to add any additional View.OnClickListeners.
     */
    public static void setOnClickListeners(View.OnClickListener listener, View... views) {
        for (View v : views) {
            v.setOnClickListener(listener);
        }
    }

    /**
     * Initializes EditText TextWatcher.TextChangeListeners. Use this method to add any additional
     * TextChangeListeners.
     */
    public void setTextChangedListeners(TextWatcher textWatcher, EditText... editTexts) {
        for (EditText e : editTexts) {
            e.addTextChangedListener(textWatcher);
        }
    }

}
